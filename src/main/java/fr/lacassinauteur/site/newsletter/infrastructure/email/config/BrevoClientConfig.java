package fr.lacassinauteur.site.newsletter.infrastructure.email.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;

/**
 * Câblage du client HTTP vers l'API transactionnelle Brevo. Cf.
 * {@code BrevoEmailAdapter} pour le détail. Même profil que l'adaptateur
 * ({@code !dev}) : inutile de construire ce bean quand {@code LogEmailAdapter} est
 * actif à la place.
 */
@Configuration
@Profile("!dev")
@EnableConfigurationProperties(BrevoNewsletterProperties.class)
public class BrevoClientConfig {

    private static final String BASE_URL = "https://api.brevo.com/v3";

    @Bean
    public RestClient brevoRestClient(BrevoNewsletterProperties proprietes) {
        return RestClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("api-key", proprietes.getApiKey())
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
