package fr.lacassinauteur.site.legal.presentation.form;

import jakarta.validation.constraints.Min;

public class InformationsLegalesForm {

    private String editeurNom;
    private String editeurStatut;
    private String editeurAdresse;
    private String editeurEmail;
    private String directeurPublication;
    private String hebergeurNom;
    private String hebergeurAdresse;

    @Min(1)
    private int conservationNewsletterMois;

    @Min(1)
    private int conservationContactMois;

    public String getEditeurNom() {
        return editeurNom;
    }

    public void setEditeurNom(String editeurNom) {
        this.editeurNom = editeurNom;
    }

    public String getEditeurStatut() {
        return editeurStatut;
    }

    public void setEditeurStatut(String editeurStatut) {
        this.editeurStatut = editeurStatut;
    }

    public String getEditeurAdresse() {
        return editeurAdresse;
    }

    public void setEditeurAdresse(String editeurAdresse) {
        this.editeurAdresse = editeurAdresse;
    }

    public String getEditeurEmail() {
        return editeurEmail;
    }

    public void setEditeurEmail(String editeurEmail) {
        this.editeurEmail = editeurEmail;
    }

    public String getDirecteurPublication() {
        return directeurPublication;
    }

    public void setDirecteurPublication(String directeurPublication) {
        this.directeurPublication = directeurPublication;
    }

    public String getHebergeurNom() {
        return hebergeurNom;
    }

    public void setHebergeurNom(String hebergeurNom) {
        this.hebergeurNom = hebergeurNom;
    }

    public String getHebergeurAdresse() {
        return hebergeurAdresse;
    }

    public void setHebergeurAdresse(String hebergeurAdresse) {
        this.hebergeurAdresse = hebergeurAdresse;
    }

    public int getConservationNewsletterMois() {
        return conservationNewsletterMois;
    }

    public void setConservationNewsletterMois(int conservationNewsletterMois) {
        this.conservationNewsletterMois = conservationNewsletterMois;
    }

    public int getConservationContactMois() {
        return conservationContactMois;
    }

    public void setConservationContactMois(int conservationContactMois) {
        this.conservationContactMois = conservationContactMois;
    }
}
