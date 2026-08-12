package fr.lacassinauteur.site.catalogue.application.command;

import java.util.UUID;

public record ModifierLivreCommand(UUID livreId, UUID collectionId, String titre, String sousTitre,
                                    String couvertureUrl, String pitchCourt, String resume, int ordre) {
}
