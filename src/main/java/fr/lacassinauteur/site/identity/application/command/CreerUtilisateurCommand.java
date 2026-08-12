package fr.lacassinauteur.site.identity.application.command;

import fr.lacassinauteur.site.identity.domain.model.Role;

public record CreerUtilisateurCommand(String email, String motDePasseClair, Role role) {
}
