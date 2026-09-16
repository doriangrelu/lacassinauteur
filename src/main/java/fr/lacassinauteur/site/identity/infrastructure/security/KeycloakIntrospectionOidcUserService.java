package fr.lacassinauteur.site.identity.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Charge le principal OIDC sans jamais appeler l'endpoint userinfo : les jetons
 * d'accès émis par le royaume Keycloak du site sont opaques ("token light", cf.
 * ADR-0033) et ne portent aucun rôle directement lisible. La seule source
 * d'autorisation est donc l'introspection RFC 7662 du jeton d'accès
 * (endpoint découvert via la métadonnée OIDC {@code introspection_endpoint}), qui
 * révèle les claims associées côté serveur Keycloak — dont le rôle realm
 * {@value #ROLE_REALM_BACKOFFICE}, seul habilité à entrer dans le back-office. Ce
 * claim ({@code realm_access.roles}) est celui que Keycloak peuple par défaut
 * (mapper "roles" standard), aucune configuration de mapper dédiée n'est
 * nécessaire côté royaume.
 */
public class KeycloakIntrospectionOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    /**
     * Nom du rôle realm Keycloak et de l'autorité Spring Security qui en
     * découle — volontairement identiques pour ne pas maintenir deux
     * vocabulaires. Doit exister, à l'identique, sur les royaumes prod et de
     * test, cf. mode-operatoire-deploiement.md.
     */
    public static final String ROLE_REALM_BACKOFFICE = "AUTEUR";

    private static final Logger LOG = LoggerFactory.getLogger(KeycloakIntrospectionOidcUserService.class);

    private final RestClient restClient;

    public KeycloakIntrospectionOidcUserService(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcIdToken idToken = userRequest.getIdToken();
        Map<String, Object> reponseIntrospection = introspecter(userRequest);

        Set<GrantedAuthority> autorites = new LinkedHashSet<>();
        autorites.add(new OidcUserAuthority(idToken));
        if (autoriseBackoffice(reponseIntrospection)) {
            autorites.add(new SimpleGrantedAuthority(ROLE_REALM_BACKOFFICE));
        } else {
            LOG.warn("Autorite '{}' absente de l'introspection pour {} (active={}) : acces back-office refuse.",
                    ROLE_REALM_BACKOFFICE, idToken.getSubject(), reponseIntrospection.get("active"));
        }

        return new DefaultOidcUser(autorites, idToken);
    }

    private Map<String, Object> introspecter(OidcUserRequest userRequest) {
        ClientRegistration inscription = userRequest.getClientRegistration();
        Object endpoint = inscription.getProviderDetails().getConfigurationMetadata().get("introspection_endpoint");
        if (endpoint == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error("introspection_endpoint_absent"),
                    "Le document de découverte OIDC de " + inscription.getRegistrationId()
                            + " ne publie aucun introspection_endpoint.");
        }

        MultiValueMap<String, String> corps = new LinkedMultiValueMap<>();
        corps.add("token", userRequest.getAccessToken().getTokenValue());
        corps.add("token_type_hint", "access_token");

        Map<String, Object> reponse;
        try {
            reponse = restClient.post()
                    .uri(endpoint.toString())
                    .headers(headers -> headers.setBasicAuth(inscription.getClientId(), inscription.getClientSecret()))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(corps)
                    .retrieve()
                    .body(Map.class);
        } catch (RuntimeException exception) {
            throw new OAuth2AuthenticationException(new OAuth2Error("introspection_echouee"),
                    "Échec de l'introspection du jeton d'accès Keycloak.", exception);
        }

        if (reponse == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error("introspection_reponse_vide"),
                    "Réponse vide de l'endpoint d'introspection Keycloak.");
        }
        return reponse;
    }

    @SuppressWarnings("unchecked")
    private boolean autoriseBackoffice(Map<String, Object> reponseIntrospection) {
        if (!Boolean.TRUE.equals(reponseIntrospection.get("active"))) {
            return false;
        }

        Object realmAccess = reponseIntrospection.get("realm_access");
        if (!(realmAccess instanceof Map<?, ?> realmAccessMap)) {
            return false;
        }

        Object roles = realmAccessMap.get("roles");
        return roles instanceof List<?> listeRoles && listeRoles.contains(ROLE_REALM_BACKOFFICE);
    }
}
