package fr.lacassinauteur.site.identity.application.result;

import fr.lacassinauteur.site.identity.domain.model.Role;
import fr.lacassinauteur.site.identity.domain.model.Utilisateur;

import java.util.UUID;

public record UtilisateurResult(UUID id, String email, Role role, boolean actif) {

    public static UtilisateurResult depuis(Utilisateur utilisateur) {
        return new UtilisateurResult(
                utilisateur.id(),
                utilisateur.email().valeur(),
                utilisateur.role(),
                utilisateur.actif());
    }
}
