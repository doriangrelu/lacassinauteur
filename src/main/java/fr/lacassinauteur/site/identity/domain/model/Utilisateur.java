package fr.lacassinauteur.site.identity.domain.model;

import java.util.UUID;

public class Utilisateur {

    private final UUID id;
    private Email email;
    private MotDePasseHache motDePasseHache;
    private Role role;
    private boolean actif;

    public Utilisateur(UUID id, Email email, MotDePasseHache motDePasseHache, Role role, boolean actif) {
        this.id = id;
        this.email = email;
        this.motDePasseHache = motDePasseHache;
        this.role = role;
        this.actif = actif;
    }

    public static Utilisateur creer(Email email, MotDePasseHache motDePasseHache, Role role) {
        return new Utilisateur(UUID.randomUUID(), email, motDePasseHache, role, true);
    }

    public void changerRole(Role nouveauRole) {
        this.role = nouveauRole;
    }

    public void changerMotDePasse(MotDePasseHache nouveauMotDePasseHache) {
        this.motDePasseHache = nouveauMotDePasseHache;
    }

    public void desactiver() {
        this.actif = false;
    }

    public void reactiver() {
        this.actif = true;
    }

    public UUID id() {
        return id;
    }

    public Email email() {
        return email;
    }

    public MotDePasseHache motDePasseHache() {
        return motDePasseHache;
    }

    public Role role() {
        return role;
    }

    public boolean actif() {
        return actif;
    }
}
