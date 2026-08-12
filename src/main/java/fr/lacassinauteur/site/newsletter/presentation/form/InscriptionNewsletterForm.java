package fr.lacassinauteur.site.newsletter.presentation.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class InscriptionNewsletterForm {

    @NotBlank
    private String prenom;

    @NotBlank
    @Email
    private String email;

    /**
     * Honeypot anti-spam (cf. docs/architecture/tech-stack.md) : champ masqué en CSS,
     * jamais rempli par un visiteur humain. S'il arrive non vide, la soumission est
     * silencieusement ignorée côté contrôleur (aucune erreur affichée, pour ne pas
     * révéler le mécanisme à un bot).
     */
    private String siteWeb;

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSiteWeb() {
        return siteWeb;
    }

    public void setSiteWeb(String siteWeb) {
        this.siteWeb = siteWeb;
    }
}
