package fr.lacassinauteur.site.catalogue.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "livre")
public class LivreJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "collection_id", nullable = false)
    private UUID collectionId;

    @Column(nullable = false)
    private String titre;

    @Column(name = "sous_titre")
    private String sousTitre;

    @Column(name = "couverture_url")
    private String couvertureUrl;

    @Column(name = "pitch_court", columnDefinition = "text")
    private String pitchCourt;

    @Column(columnDefinition = "text")
    private String resume;

    @Column(name = "lien_achat_url")
    private String lienAchatUrl;

    @Column(name = "lien_achat_libelle")
    private String lienAchatLibelle;

    @Column(nullable = false)
    private int ordre;

    @Column(name = "derniere_parution", nullable = false)
    private boolean derniereParution;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "format")
    private String format;

    @Column(name = "nombre_pages")
    private Integer nombrePages;

    @Column(name = "prix")
    private BigDecimal prix;

    @Column(name = "lieux_distribution")
    private String lieuxDistribution;

    @Column(name = "pitch_editeur", columnDefinition = "text")
    private String pitchEditeur;

    @Column(name = "synopsis_editeur", columnDefinition = "text")
    private String synopsisEditeur;

    protected LivreJpaEntity() {
    }

    public LivreJpaEntity(UUID id, String slug, UUID collectionId, String titre, String sousTitre, String couvertureUrl,
                           String pitchCourt, String resume, String lienAchatUrl, String lienAchatLibelle,
                           int ordre, boolean derniereParution, String isbn, String format, Integer nombrePages,
                           BigDecimal prix, String lieuxDistribution, String pitchEditeur, String synopsisEditeur) {
        this.id = id;
        this.slug = slug;
        this.collectionId = collectionId;
        this.titre = titre;
        this.sousTitre = sousTitre;
        this.couvertureUrl = couvertureUrl;
        this.pitchCourt = pitchCourt;
        this.resume = resume;
        this.lienAchatUrl = lienAchatUrl;
        this.lienAchatLibelle = lienAchatLibelle;
        this.ordre = ordre;
        this.derniereParution = derniereParution;
        this.isbn = isbn;
        this.format = format;
        this.nombrePages = nombrePages;
        this.prix = prix;
        this.lieuxDistribution = lieuxDistribution;
        this.pitchEditeur = pitchEditeur;
        this.synopsisEditeur = synopsisEditeur;
    }

    public UUID getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public UUID getCollectionId() {
        return collectionId;
    }

    public String getTitre() {
        return titre;
    }

    public String getSousTitre() {
        return sousTitre;
    }

    public String getCouvertureUrl() {
        return couvertureUrl;
    }

    public String getPitchCourt() {
        return pitchCourt;
    }

    public String getResume() {
        return resume;
    }

    public String getLienAchatUrl() {
        return lienAchatUrl;
    }

    public String getLienAchatLibelle() {
        return lienAchatLibelle;
    }

    public int getOrdre() {
        return ordre;
    }

    public boolean isDerniereParution() {
        return derniereParution;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getFormat() {
        return format;
    }

    public Integer getNombrePages() {
        return nombrePages;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public String getLieuxDistribution() {
        return lieuxDistribution;
    }

    public String getPitchEditeur() {
        return pitchEditeur;
    }

    public String getSynopsisEditeur() {
        return synopsisEditeur;
    }
}
