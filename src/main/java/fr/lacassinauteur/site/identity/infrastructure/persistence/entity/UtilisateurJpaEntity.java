package fr.lacassinauteur.site.identity.infrastructure.persistence.entity;

import fr.lacassinauteur.site.identity.domain.model.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "utilisateur")
public class UtilisateurJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "mot_de_passe_hache", nullable = false)
    private String motDePasseHache;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private boolean actif;

    protected UtilisateurJpaEntity() {
    }

    public UtilisateurJpaEntity(UUID id, String email, String motDePasseHache, Role role, boolean actif) {
        this.id = id;
        this.email = email;
        this.motDePasseHache = motDePasseHache;
        this.role = role;
        this.actif = actif;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getMotDePasseHache() {
        return motDePasseHache;
    }

    public Role getRole() {
        return role;
    }

    public boolean isActif() {
        return actif;
    }
}
