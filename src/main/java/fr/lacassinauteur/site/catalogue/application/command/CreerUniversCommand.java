package fr.lacassinauteur.site.catalogue.application.command;

public record CreerUniversCommand(String nom, String sousTitre, String texte, byte[] photoContenu,
                                   String photoNomFichier, int ordre) {
}
