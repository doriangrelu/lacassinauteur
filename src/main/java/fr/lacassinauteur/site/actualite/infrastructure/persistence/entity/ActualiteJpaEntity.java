package fr.lacassinauteur.site.actualite.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "actualite")
public class ActualiteJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "text")
    private String texte;

    @Column(nullable = false)
    private LocalDate date;

    private String lieu;

    @Column(name = "lien_billetterie")
    private String lienBilletterie;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "archivee_manuellement", nullable = false)
    private boolean archiveeManuellement;

    @Column(name = "mis_en_avant", nullable = false)
    private boolean misEnAvant;

    protected ActualiteJpaEntity() {
    }

    public ActualiteJpaEntity(UUID id, String titre, String texte, LocalDate date, String lieu, String lienBilletterie,
                               String imageUrl, boolean archiveeManuellement, boolean misEnAvant) {
        this.id = id;
        this.titre = titre;
        this.texte = texte;
        this.date = date;
        this.lieu = lieu;
        this.lienBilletterie = lienBilletterie;
        this.imageUrl = imageUrl;
        this.archiveeManuellement = archiveeManuellement;
        this.misEnAvant = misEnAvant;
    }

    public UUID getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getTexte() {
        return texte;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getLieu() {
        return lieu;
    }

    public String getLienBilletterie() {
        return lienBilletterie;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isArchiveeManuellement() {
        return archiveeManuellement;
    }

    public boolean isMisEnAvant() {
        return misEnAvant;
    }
}
