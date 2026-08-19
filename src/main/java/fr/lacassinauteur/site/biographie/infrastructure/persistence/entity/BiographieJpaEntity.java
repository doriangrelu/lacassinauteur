package fr.lacassinauteur.site.biographie.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "biographie")
public class BiographieJpaEntity {

    @Id
    private UUID id;

    @Column(columnDefinition = "text", nullable = false)
    private String texte;

    @Column(name = "photo_url")
    private String photoUrl;

    /**
     * Toujours {@code true} : porte la contrainte d'unicité qui garantit qu'il n'y a
     * jamais qu'une seule ligne en base (cf. migration V11). Détail de persistance,
     * volontairement absent du modèle de domaine.
     */
    @Column(name = "ligne_unique", nullable = false)
    private boolean ligneUnique = true;

    protected BiographieJpaEntity() {
    }

    public BiographieJpaEntity(UUID id, String texte, String photoUrl) {
        this.id = id;
        this.texte = texte;
        this.photoUrl = photoUrl;
        this.ligneUnique = true;
    }

    public UUID getId() {
        return id;
    }

    public String getTexte() {
        return texte;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public boolean isLigneUnique() {
        return ligneUnique;
    }
}
