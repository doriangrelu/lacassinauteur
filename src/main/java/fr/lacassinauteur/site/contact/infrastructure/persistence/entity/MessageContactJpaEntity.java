package fr.lacassinauteur.site.contact.infrastructure.persistence.entity;

import fr.lacassinauteur.site.contact.domain.model.StatutMessage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "message_contact")
public class MessageContactJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String objet;

    @Column(nullable = false, columnDefinition = "text")
    private String message;

    @Column(name = "date_reception", nullable = false)
    private LocalDateTime dateReception;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutMessage statut;

    protected MessageContactJpaEntity() {
    }

    public MessageContactJpaEntity(UUID id, String nom, String email, String objet, String message,
                                    LocalDateTime dateReception, StatutMessage statut) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.objet = objet;
        this.message = message;
        this.dateReception = dateReception;
        this.statut = statut;
    }

    public UUID getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }

    public String getObjet() {
        return objet;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDateReception() {
        return dateReception;
    }

    public StatutMessage getStatut() {
        return statut;
    }
}
