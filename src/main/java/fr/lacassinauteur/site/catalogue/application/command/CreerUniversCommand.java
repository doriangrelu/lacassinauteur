package fr.lacassinauteur.site.catalogue.application.command;

public record CreerUniversCommand(String nom, String sousTitre, String texte, String photoUrl, int ordre) {
}
