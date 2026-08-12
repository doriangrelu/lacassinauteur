package fr.lacassinauteur.site.identity.domain.model;

import java.util.regex.Pattern;

public record Email(String valeur) {

    private static final Pattern FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Email {
        if (valeur == null || !FORMAT.matcher(valeur).matches()) {
            throw new IllegalArgumentException("Adresse email invalide : " + valeur);
        }
        valeur = valeur.toLowerCase();
    }
}
