package fr.lacassinauteur.site.catalogue.application.command;

import java.util.UUID;

public record ModifierUniversCommand(UUID universId, String nom, String sousTitre, String texte,
                                      byte[] nouvellePhotoContenu, String nouvellePhotoNomFichier, int ordre) {
}
