package fr.lacassinauteur.site.catalogue.infrastructure.persistence.entity;

import fr.lacassinauteur.site.catalogue.domain.model.StatutAvis;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "avis_lecteur")
public class AvisLecteurJpaEntity {

    @Id
    private UUID id;

    @Column(name = "livre_id", nullable = false)
    private UUID livreId;

    @Column(name = "nom_auteur_avis", nullable = false)
    private String nomAuteurAvis;

    @Column(columnDefinition = "text", nullable = false)
    private String texte;

    private Integer note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutAvis statut;

    @Column(name = "date_soumission", nullable = false)
    private LocalDateTime dateSoumission;

    protected AvisLecteurJpaEntity() {
    }

    public AvisLecteurJpaEntity(UUID id, UUID livreId, String nomAuteurAvis, String texte, Integer note,
                                 StatutAvis statut, LocalDateTime dateSoumission) {
        this.id = id;
        this.livreId = livreId;
        this.nomAuteurAvis = nomAuteurAvis;
        this.texte = texte;
        this.note = note;
        this.statut = statut;
        this.dateSoumission = dateSoumission;
    }

    public UUID getId() {
        return id;
    }

    public UUID getLivreId() {
        return livreId;
    }

    public String getNomAuteurAvis() {
        return nomAuteurAvis;
    }

    public String getTexte() {
        return texte;
    }

    public Integer getNote() {
        return note;
    }

    public StatutAvis getStatut() {
        return statut;
    }

    public LocalDateTime getDateSoumission() {
        return dateSoumission;
    }
}
