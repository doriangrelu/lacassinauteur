package fr.lacassinauteur.site.contact.application.command;

public record EnvoyerMessageContactCommand(String nom, String email, String objet, String message) {
}
