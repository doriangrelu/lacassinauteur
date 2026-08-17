package fr.lacassinauteur.site.identity.infrastructure.email.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration de l'adaptateur Brevo du domaine identity (cf. ADR-0018).
 * Réutilise volontairement les mêmes variables d'environnement que
 * {@code newsletter.infrastructure.email.config.BrevoNewsletterProperties}
 * ({@code BREVO_API_KEY}, {@code BREVO_EXPEDITEUR_EMAIL}/{@code _NOM}) : même
 * compte Brevo, un seul expéditeur à gérer — cf. application.yml.
 */
@ConfigurationProperties(prefix = "app.identity.brevo")
public class BrevoIdentityProperties {

    private String apiKey = "";

    private String expediteurEmail = "newsletter@thierrylacassin-auteur.fr";

    private String expediteurNom = "Thierry Lacassin";

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getExpediteurEmail() {
        return expediteurEmail;
    }

    public void setExpediteurEmail(String expediteurEmail) {
        this.expediteurEmail = expediteurEmail;
    }

    public String getExpediteurNom() {
        return expediteurNom;
    }

    public void setExpediteurNom(String expediteurNom) {
        this.expediteurNom = expediteurNom;
    }
}
