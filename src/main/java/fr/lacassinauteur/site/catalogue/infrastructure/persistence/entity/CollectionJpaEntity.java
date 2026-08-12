package fr.lacassinauteur.site.catalogue.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "collection")
public class CollectionJpaEntity {

    @Id
    private UUID id;

    @Column(name = "univers_id", nullable = false)
    private UUID universId;

    @Column(nullable = false)
    private String nom;

    @Column(name = "sous_titre")
    private String sousTitre;

    @Column(columnDefinition = "text")
    private String texte;

    @Column(nullable = false)
    private int ordre;

    protected CollectionJpaEntity() {
    }

    public CollectionJpaEntity(UUID id, UUID universId, String nom, String sousTitre, String texte, int ordre) {
        this.id = id;
        this.universId = universId;
        this.nom = nom;
        this.sousTitre = sousTitre;
        this.texte = texte;
        this.ordre = ordre;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUniversId() {
        return universId;
    }

    public String getNom() {
        return nom;
    }

    public String getSousTitre() {
        return sousTitre;
    }

    public String getTexte() {
        return texte;
    }

    public int getOrdre() {
        return ordre;
    }
}
