package fr.lacassinauteur.site.identity.application.command;

public record ReinitialiserMotDePasseCommand(String jeton, String nouveauMotDePasseClair) {
}
