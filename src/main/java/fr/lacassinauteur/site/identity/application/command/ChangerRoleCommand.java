package fr.lacassinauteur.site.identity.application.command;

import fr.lacassinauteur.site.identity.domain.model.Role;

import java.util.UUID;

public record ChangerRoleCommand(UUID utilisateurId, Role nouveauRole) {
}
