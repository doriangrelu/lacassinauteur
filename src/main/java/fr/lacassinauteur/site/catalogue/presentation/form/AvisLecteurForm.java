package fr.lacassinauteur.site.catalogue.presentation.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Formulaire public de soumission d'un avis lecteur, protégé par honeypot (cf.
 * {@code contact.presentation.form.MessageContactForm}, même approche).
 */
public class AvisLecteurForm {

    @NotBlank
    @Size(max = 255)
    private String nomAuteurAvis;

    @NotBlank
    @Size(max = 2000)
    private String texte;

    @Min(1)
    @Max(5)
    private Integer note;

    /**
     * Honeypot anti-spam : champ masqué en CSS, jamais rempli par un visiteur humain.
     */
    private String siteWeb;

    /** Jeton reCAPTCHA v3 (cf. ADR-0019), vérifié côté contrôleur. */
    private String captchaToken;

    public String getNomAuteurAvis() {
        return nomAuteurAvis;
    }

    public void setNomAuteurAvis(String nomAuteurAvis) {
        this.nomAuteurAvis = nomAuteurAvis;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public Integer getNote() {
        return note;
    }

    public void setNote(Integer note) {
        this.note = note;
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
