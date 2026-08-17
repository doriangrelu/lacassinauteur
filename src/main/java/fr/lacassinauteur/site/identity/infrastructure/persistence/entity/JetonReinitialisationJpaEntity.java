package fr.lacassinauteur.site.identity.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "jeton_reinitialisation")
public class JetonReinitialisationJpaEntity {

    @Id
    private UUID id;

    @Column(name = "utilisateur_id", nullable = false)
    private UUID utilisateurId;

    @Column(nullable = false, length = 1000)
    private String jeton;

    @Column(name = "date_expiration", nullable = false)
    private Instant dateExpiration;

    protected JetonReinitialisationJpaEntity() {
    }

    public JetonReinitialisationJpaEntity(UUID id, UUID utilisateurId, String jeton, Instant dateExpiration) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.jeton = jeton;
        this.dateExpiration = dateExpiration;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUtilisateurId() {
        return utilisateurId;
    }

    public String getJeton() {
        return jeton;
    }

    public Instant getDateExpiration() {
        return dateExpiration;
    }
}
