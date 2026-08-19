package fr.lacassinauteur.site.legal.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "informations_legales")
public class InformationsLegalesJpaEntity {

    @Id
    private UUID id;

    @Column(name = "editeur_nom")
    private String editeurNom;

    @Column(name = "editeur_statut")
    private String editeurStatut;

    @Column(name = "editeur_adresse")
    private String editeurAdresse;

    @Column(name = "editeur_email")
    private String editeurEmail;

    @Column(name = "directeur_publication")
    private String directeurPublication;

    @Column(name = "hebergeur_nom")
    private String hebergeurNom;

    @Column(name = "hebergeur_adresse")
    private String hebergeurAdresse;

    @Column(name = "conservation_newsletter_mois", nullable = false)
    private int conservationNewsletterMois;

    @Column(name = "conservation_contact_mois", nullable = false)
    private int conservationContactMois;

    /**
     * Toujours {@code true} : porte la contrainte d'unicité garantissant une seule
     * ligne (cf. migration V12), même mécanisme que la table biographie.
     */
    @Column(name = "ligne_unique", nullable = false)
    private boolean ligneUnique = true;

    protected InformationsLegalesJpaEntity() {
    }

    public InformationsLegalesJpaEntity(UUID id, String editeurNom, String editeurStatut, String editeurAdresse,
                                         String editeurEmail, String directeurPublication, String hebergeurNom,
                                         String hebergeurAdresse, int conservationNewsletterMois,
                                         int conservationContactMois) {
        this.id = id;
        this.editeurNom = editeurNom;
        this.editeurStatut = editeurStatut;
        this.editeurAdresse = editeurAdresse;
        this.editeurEmail = editeurEmail;
        this.directeurPublication = directeurPublication;
        this.hebergeurNom = hebergeurNom;
        this.hebergeurAdresse = hebergeurAdresse;
        this.conservationNewsletterMois = conservationNewsletterMois;
        this.conservationContactMois = conservationContactMois;
        this.ligneUnique = true;
    }

    public UUID getId() {
        return id;
    }

    public String getEditeurNom() {
        return editeurNom;
    }

    public String getEditeurStatut() {
        return editeurStatut;
    }

    public String getEditeurAdresse() {
        return editeurAdresse;
    }

    public String getEditeurEmail() {
        return editeurEmail;
    }

    public String getDirecteurPublication() {
        return directeurPublication;
    }

    public String getHebergeurNom() {
        return hebergeurNom;
    }

    public String getHebergeurAdresse() {
        return hebergeurAdresse;
    }

    public int getConservationNewsletterMois() {
        return conservationNewsletterMois;
    }

    public int getConservationContactMois() {
        return conservationContactMois;
    }

    public boolean isLigneUnique() {
        return ligneUnique;
    }
}
