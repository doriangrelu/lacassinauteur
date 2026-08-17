package fr.lacassinauteur.site.identity.infrastructure.email.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;

/**
 * Câblage du client HTTP vers l'API transactionnelle Brevo pour le domaine
 * identity. Même mécanique que
 * {@code newsletter.infrastructure.email.config.BrevoClientConfig}, dupliquée
 * volontairement pour ne pas coupler les deux domaines (cf.
 * package-structure.md), mais pointant sur le même compte Brevo.
 */
@Configuration
@Profile("!dev")
@EnableConfigurationProperties(BrevoIdentityProperties.class)
public class BrevoIdentityClientConfig {

    private static final String BASE_URL = "https://api.brevo.com/v3";

    @Bean
    public RestClient brevoIdentityRestClient(BrevoIdentityProperties proprietes) {
        return RestClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("api-key", proprietes.getApiKey())
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
