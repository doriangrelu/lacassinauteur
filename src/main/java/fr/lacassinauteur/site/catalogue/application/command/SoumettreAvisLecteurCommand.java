package fr.lacassinauteur.site.catalogue.application.command;

import java.util.UUID;

public record SoumettreAvisLecteurCommand(UUID livreId, String nomAuteurAvis, String texte, Integer note) {
}
