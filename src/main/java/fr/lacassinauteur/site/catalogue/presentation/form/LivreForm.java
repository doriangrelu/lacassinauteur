package fr.lacassinauteur.site.catalogue.presentation.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.UUID;

public class LivreForm {

    @NotNull
    private UUID collectionId;

    @NotBlank
    private String titre;

    private String sousTitre;

    private MultipartFile couverture;

    private String pitchCourt;

    private String resume;

    private int ordre;

    private String isbn;

    private String format;

    private Integer nombrePages;

    private BigDecimal prix;

    private String lieuxDistribution;

    private String pitchEditeur;

    private String synopsisEditeur;

    public UUID getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(UUID collectionId) {
        this.collectionId = collectionId;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getSousTitre() {
        return sousTitre;
    }

    public void setSousTitre(String sousTitre) {
        this.sousTitre = sousTitre;
    }

    public MultipartFile getCouverture() {
        return couverture;
    }

    public void setCouverture(MultipartFile couverture) {
        this.couverture = couverture;
    }

    public String getPitchCourt() {
        return pitchCourt;
    }

    public void setPitchCourt(String pitchCourt) {
        this.pitchCourt = pitchCourt;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public int getOrdre() {
        return ordre;
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getNombrePages() {
        return nombrePages;
    }

    public void setNombrePages(Integer nombrePages) {
        this.nombrePages = nombrePages;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public String getLieuxDistribution() {
        return lieuxDistribution;
    }

    public void setLieuxDistribution(String lieuxDistribution) {
        this.lieuxDistribution = lieuxDistribution;
    }

    public String getPitchEditeur() {
        return pitchEditeur;
    }

    public void setPitchEditeur(String pitchEditeur) {
        this.pitchEditeur = pitchEditeur;
    }

    public String getSynopsisEditeur() {
        return synopsisEditeur;
    }

    public void setSynopsisEditeur(String synopsisEditeur) {
        this.synopsisEditeur = synopsisEditeur;
    }
}
