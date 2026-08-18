package fr.lacassinauteur.site.config;

import fr.lacassinauteur.site.identity.infrastructure.security.LoginRateLimiter;
import fr.lacassinauteur.site.identity.infrastructure.security.LoginRateLimitingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final String PAGE_CONNEXION = "/backoffice/connexion";

    // reCAPTCHA v3 (cf. ADR-0019) est le seul contenu tiers du site : script +
    // iframe/XHR internes servis depuis google.com/gstatic.com. Aucun script ni
    // style inline nulle part dans les templates (vérifié) — pas de 'unsafe-inline'
    // nécessaire.
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
                    + "form-action 'self'; "
                    + "frame-ancestors 'none'";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, LoginRateLimiter loginRateLimiter) throws Exception {
        http.headers(headers -> headers.contentSecurityPolicy(csp -> csp.policyDirectives(CSP)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PAGE_CONNEXION, "/backoffice/mot-de-passe-oublie", "/backoffice/reinitialiser-mot-de-passe")
                        .permitAll()
                        .requestMatchers("/backoffice/**").hasAnyRole("ADMIN", "AUTEUR")
                        .anyRequest().permitAll())
                .formLogin(form -> form
                        .loginPage(PAGE_CONNEXION)
                        .loginProcessingUrl(PAGE_CONNEXION)
                        // "/backoffice/comptes" est reserve a l'ADMIN (cf. CompteController) : la
                        // redirection par defaut apres connexion doit pointer vers une page
                        // accessible aux deux roles, sans quoi un AUTEUR tombe sur une erreur
                        // 403 juste apres s'etre connecte.
                        .defaultSuccessUrl("/backoffice/univers", true)
                        .failureUrl(PAGE_CONNEXION + "?erreur")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/backoffice/deconnexion")
                        .logoutSuccessUrl(PAGE_CONNEXION + "?deconnecte"))
                .addFilterBefore(
                        new LoginRateLimitingFilter(loginRateLimiter, PAGE_CONNEXION),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
