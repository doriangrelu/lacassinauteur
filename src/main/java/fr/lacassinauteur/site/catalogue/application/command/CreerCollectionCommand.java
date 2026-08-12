package fr.lacassinauteur.site.catalogue.application.command;

import java.util.UUID;

public record CreerCollectionCommand(UUID universId, String nom, String sousTitre, String texte, int ordre) {
}
