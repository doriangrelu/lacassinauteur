package fr.lacassinauteur.site.actualite.presentation.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public class ActualiteForm {

    @NotBlank
    private String titre;

    private String texte;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    private String lieu;

    private String lienBilletterie;

    private MultipartFile image;

    private boolean archiveeManuellement;

    private boolean misEnAvant;

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getLienBilletterie() {
        return lienBilletterie;
    }

    public void setLienBilletterie(String lienBilletterie) {
        this.lienBilletterie = lienBilletterie;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public boolean isArchiveeManuellement() {
        return archiveeManuellement;
    }

    public void setArchiveeManuellement(boolean archiveeManuellement) {
        this.archiveeManuellement = archiveeManuellement;
    }

    public boolean isMisEnAvant() {
        return misEnAvant;
    }

    public void setMisEnAvant(boolean misEnAvant) {
        this.misEnAvant = misEnAvant;
    }
}
