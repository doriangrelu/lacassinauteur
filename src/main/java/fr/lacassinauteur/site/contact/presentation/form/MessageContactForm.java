package fr.lacassinauteur.site.contact.presentation.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MessageContactForm {

    @NotBlank
    @Size(max = 255)
    private String nom;

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    @NotBlank
    @Size(max = 255)
    private String objet;

    @NotBlank
    private String message;

    /**
     * Honeypot anti-spam (cf. docs/architecture/tech-stack.md, même approche que
     * {@code newsletter.presentation.form.InscriptionNewsletterForm}) : champ masqué
     * en CSS, jamais rempli par un visiteur humain.
     */
    private String siteWeb;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSiteWeb() {
        return siteWeb;
    }

    public void setSiteWeb(String siteWeb) {
        this.siteWeb = siteWeb;
    }
}
