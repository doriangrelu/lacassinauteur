package fr.lacassinauteur.site.catalogue.application.command;

import java.util.UUID;

public record CreerLivreCommand(UUID collectionId, String titre, String sousTitre, String couvertureUrl,
                                 String pitchCourt, String resume, int ordre) {
}
