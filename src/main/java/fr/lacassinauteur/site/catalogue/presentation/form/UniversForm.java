package fr.lacassinauteur.site.catalogue.presentation.form;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public class UniversForm {

    @NotBlank
    private String nom;

    private String sousTitre;

    private String texte;

    private MultipartFile photo;

    private MultipartFile photoComplementaire;

    private String photoComplementaireLegende;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getSousTitre() {
        return sousTitre;
    }

    public void setSousTitre(String sousTitre) {
        this.sousTitre = sousTitre;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public MultipartFile getPhoto() {
        return photo;
    }

    public void setPhoto(MultipartFile photo) {
        this.photo = photo;
    }

    public MultipartFile getPhotoComplementaire() {
        return photoComplementaire;
    }

    public void setPhotoComplementaire(MultipartFile photoComplementaire) {
        this.photoComplementaire = photoComplementaire;
    }

    public String getPhotoComplementaireLegende() {
        return photoComplementaireLegende;
    }

    public void setPhotoComplementaireLegende(String photoComplementaireLegende) {
        this.photoComplementaireLegende = photoComplementaireLegende;
    }
}
