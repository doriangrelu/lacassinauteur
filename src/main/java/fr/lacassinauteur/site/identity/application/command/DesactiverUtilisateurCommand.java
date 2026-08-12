package fr.lacassinauteur.site.identity.application.command;

import java.util.UUID;

public record DesactiverUtilisateurCommand(UUID utilisateurId) {
}
