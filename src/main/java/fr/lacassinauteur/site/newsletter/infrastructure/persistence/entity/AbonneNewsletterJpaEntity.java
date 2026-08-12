package fr.lacassinauteur.site.newsletter.infrastructure.persistence.entity;

import fr.lacassinauteur.site.newsletter.domain.model.StatutAbonnement;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "abonne_newsletter")
public class AbonneNewsletterJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutAbonnement statut;

    @Column(name = "date_inscription", nullable = false)
    private LocalDateTime dateInscription;

    @Column(name = "date_confirmation")
    private LocalDateTime dateConfirmation;

    @Column(name = "jeton_confirmation", nullable = false, unique = true)
    private UUID jetonConfirmation;

    protected AbonneNewsletterJpaEntity() {
    }

    public AbonneNewsletterJpaEntity(UUID id, String prenom, String email, StatutAbonnement statut,
                                      LocalDateTime dateInscription, LocalDateTime dateConfirmation,
                                      UUID jetonConfirmation) {
        this.id = id;
        this.prenom = prenom;
        this.email = email;
        this.statut = statut;
        this.dateInscription = dateInscription;
        this.dateConfirmation = dateConfirmation;
        this.jetonConfirmation = jetonConfirmation;
    }

    public UUID getId() {
        return id;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public StatutAbonnement getStatut() {
        return statut;
    }

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public LocalDateTime getDateConfirmation() {
        return dateConfirmation;
    }

    public UUID getJetonConfirmation() {
        return jetonConfirmation;
    }
}
