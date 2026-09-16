package fr.lacassinauteur.site.config;

import fr.lacassinauteur.site.identity.infrastructure.security.KeycloakIntrospectionOidcUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import static org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;

@Configuration
public class SecurityConfig {

    private static final String URL_DECONNEXION = "/backoffice/deconnexion";

    // reCAPTCHA v3 (cf. ADR-0019) est le seul contenu tiers du site : script +
    // iframe/XHR internes servis depuis google.com/gstatic.com. La redirection de
    // connexion (GET, déclenchée par l'AuthenticationEntryPoint) n'est pas
    // concernée par "form-action" — seules les soumissions de <form> le sont.
    // Aucun script ni style inline nulle part dans les templates (vérifié) — pas
    // de 'unsafe-inline' nécessaire.
    //
    // "form-action" DOIT en revanche inclure Keycloak : la déconnexion
    // (formulaire POST vers /backoffice/deconnexion) déclenche une redirection
    // HTTP vers l'endpoint de fin de session Keycloak (cross-origin), et
    // "form-action" s'applique à TOUTE la chaîne de redirection issue d'une
    // soumission de formulaire, pas seulement à l'URL déclarée dans l'attribut
    // "action" — un piège découvert en production (cf. ADR-0033) : la
    // déconnexion locale fonctionnait, mais le navigateur bloquait
    // silencieusement le saut vers Keycloak (net::ERR_ABORTED, aucune erreur
    // visible côté utilisateur), laissant la session Keycloak active et
    // permettant une reconnexion muette au prochain accès au back-office.
    private static final String CSP =
            "default-src 'self'; "
                    + "script-src 'self' https://www.google.com/recaptcha/ https://www.gstatic.com/recaptcha/; "
                    + "style-src 'self'; "
                    + "img-src 'self'; "
                    + "font-src 'self'; "
                    + "connect-src 'self' https://www.google.com/recaptcha/; "
                    + "frame-src https://www.google.com/recaptcha/; "
                    + "object-src 'none'; "
                    + "base-uri 'self'; "
                    + "form-action 'self' https://iabilis.fr; "
                    + "frame-ancestors 'none'";

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            ClientRegistrationRepository clientRegistrationRepository,
            KeycloakIntrospectionOidcUserService keycloakIntrospectionOidcUserService)
            throws Exception {
        http.headers(headers -> headers.contentSecurityPolicy(csp -> csp.policyDirectives(CSP)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/backoffice/**")
                        .hasAuthority(KeycloakIntrospectionOidcUserService.ROLE_REALM_BACKOFFICE)
                        .anyRequest().permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .authorizationRequestResolver(pkceAuthorizationRequestResolver(clientRegistrationRepository)))
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(keycloakIntrospectionOidcUserService))
                        // "/backoffice/comptes" n'existe plus (gestion de comptes déplacée dans la
                        // console Keycloak, cf. ADR-0033) : la redirection par défaut peut à nouveau
                        // pointer vers n'importe quelle page accessible à tout utilisateur autorisé.
                        .defaultSuccessUrl("/backoffice/univers", true))
                .logout(logout -> logout
                        .logoutUrl(URL_DECONNEXION)
                        .logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository)));

        return http.build();
    }

    /**
     * PKCE en plus du secret client (cf. ADR-0033) : Spring ne l'active
     * automatiquement que pour un client public ({@code client-authentication-method: none}).
     * Notre client reste confidentiel (secret gardé côté serveur, comme
     * {@code BREVO_API_KEY}) — {@code withPkce()} force PKCE malgré tout, en
     * défense en profondeur contre le vol de code d'autorisation, conformément à
     * OAuth 2.1.
     */
    private OAuth2AuthorizationRequestResolver pkceAuthorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {
        DefaultOAuth2AuthorizationRequestResolver resolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
        resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
        return resolver;
    }

    /**
     * Déconnexion RP-Initiated (OIDC) : ferme aussi la session Keycloak, pas
     * seulement la session Spring — sans ça, une reconnexion immédiate après
     * "déconnexion" ne re-demanderait aucun identifiant (session Keycloak encore
     * active côté navigateur).
     */
    private LogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedLogoutSuccessHandler handler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        handler.setPostLogoutRedirectUri("{baseUrl}/");
        return handler;
    }
}
