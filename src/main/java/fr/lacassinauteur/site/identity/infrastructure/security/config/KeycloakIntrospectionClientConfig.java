package fr.lacassinauteur.site.identity.infrastructure.security.config;

import fr.lacassinauteur.site.identity.infrastructure.security.KeycloakIntrospectionOidcUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Câblage du client HTTP vers l'endpoint d'introspection Keycloak. Pas d'URL de
 * base ni d'authentification par défaut ici : contrairement à Brevo (cf.
 * {@code BrevoClientConfig}), l'URL d'introspection est découverte par royaume et
 * les identifiants client sont propres à chaque requête (cf.
 * {@code KeycloakIntrospectionOidcUserService}).
 */
@Configuration
public class KeycloakIntrospectionClientConfig {

    @Bean
    public KeycloakIntrospectionOidcUserService keycloakIntrospectionOidcUserService() {
        return new KeycloakIntrospectionOidcUserService(RestClient.create());
    }
}
