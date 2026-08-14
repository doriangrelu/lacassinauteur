package fr.lacassinauteur.site.newsletter.infrastructure.email.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration de l'adaptateur Brevo (cf. ADR-0002, ADR-0013). {@code apiKey} est
 * lue depuis la variable d'environnement {@code BREVO_API_KEY} (jamais commitée),
 * cf. {@code application.yml}.
 */
@ConfigurationProperties(prefix = "app.newsletter.brevo")
public class BrevoNewsletterProperties {

    /** Clé API transactionnelle Brevo (variable d'environnement {@code BREVO_API_KEY}). */
    private String apiKey = "";

    /** Adresse d'expédition des emails transactionnels newsletter. */
    private String expediteurEmail = "newsletter@thierrylacassin-auteur.fr";

    /** Nom affiché comme expéditeur. */
    private String expediteurNom = "Thierry Lacassin";
    /** Identifiant numérique de la liste de contacts Brevo à synchroniser (0 = non configurée). */
    private long listeId = 0L;

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

    public long getListeId() {
        return listeId;
    }

    public void setListeId(long listeId) {
        this.listeId = listeId;
    }
}
