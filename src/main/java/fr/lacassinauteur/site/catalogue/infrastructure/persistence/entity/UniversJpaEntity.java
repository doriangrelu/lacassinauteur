package fr.lacassinauteur.site.catalogue.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "univers")
public class UniversJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String nom;

    @Column(name = "sous_titre")
    private String sousTitre;

    @Column(columnDefinition = "text")
    private String texte;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(nullable = false)
    private int ordre;

    protected UniversJpaEntity() {
    }

    public UniversJpaEntity(UUID id, String slug, String nom, String sousTitre, String texte, String photoUrl, int ordre) {
        this.id = id;
        this.slug = slug;
        this.nom = nom;
        this.sousTitre = sousTitre;
        this.texte = texte;
        this.photoUrl = photoUrl;
        this.ordre = ordre;
    }

    public UUID getId() {
        return id;
    }

    public String getSlug() {
        return slug;
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public int getOrdre() {
        return ordre;
    }
}
