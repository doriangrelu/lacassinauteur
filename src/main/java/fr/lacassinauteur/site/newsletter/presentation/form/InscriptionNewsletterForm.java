package fr.lacassinauteur.site.newsletter.presentation.form;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class InscriptionNewsletterForm {

    @NotBlank
    private String prenom;

    @NotBlank
    @Email
    private String email;

    /**
     * Consentement RGPD, obligatoire et jamais pré-coché (cf. ADR-0029). Aucune
     * date de consentement n'est stockée à part : l'inscription ne peut pas aboutir
     * sans cette case, donc {@code date_inscription} date de fait le consentement.
     */
    @AssertTrue(message = "Vous devez accepter que vos données soient utilisées pour vous envoyer la newsletter.")
    private boolean consentement;

    /**
     * Honeypot anti-spam (cf. docs/architecture/tech-stack.md) : champ masqué en CSS,
     * jamais rempli par un visiteur humain. S'il arrive non vide, la soumission est
     * silencieusement ignorée côté contrôleur (aucune erreur affichée, pour ne pas
     * révéler le mécanisme à un bot).
     */
    private String siteWeb;

    /** Jeton reCAPTCHA v3 (cf. ADR-0019), vérifié côté contrôleur. */
    private String captchaToken;

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

    public boolean isConsentement() {
        return consentement;
    }

    public void setConsentement(boolean consentement) {
        this.consentement = consentement;
    }

    public String getSiteWeb() {
        return siteWeb;
    }

    public void setSiteWeb(String siteWeb) {
        this.siteWeb = siteWeb;
    }

    public String getCaptchaToken() {
        return captchaToken;
    }

    public void setCaptchaToken(String captchaToken) {
        this.captchaToken = captchaToken;
    }
}
