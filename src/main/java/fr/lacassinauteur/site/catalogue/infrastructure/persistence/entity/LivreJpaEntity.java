package fr.lacassinauteur.site.catalogue.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "livre")
public class LivreJpaEntity {

    @Id
    private UUID id;

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

    protected LivreJpaEntity() {
    }

    public LivreJpaEntity(UUID id, UUID collectionId, String titre, String sousTitre, String couvertureUrl,
                           String pitchCourt, String resume, String lienAchatUrl, String lienAchatLibelle,
                           int ordre, boolean derniereParution) {
        this.id = id;
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
    }

    public UUID getId() {
        return id;
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
}
