package fr.lacassinauteur.site.newsletter.domain.model;

import java.util.regex.Pattern;

/**
 * Value object email, propre au domaine {@code newsletter}. Dupliqué depuis
 * {@code identity.domain.model.Email} plutôt que réutilisé directement : les domaines
 * ne s'appellent jamais entre eux par import direct de leurs classes internes (cf.
 * docs/architecture/domain-model.md), et {@code shared} est réservé aux capacités
 * réellement transverses — une petite classe dupliquée est préférable à un couplage
 * artificiel entre deux domaines indépendants.
 */
public record Email(String valeur) {

    private static final Pattern FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Email {
        if (valeur == null || !FORMAT.matcher(valeur).matches()) {
            throw new IllegalArgumentException("Adresse email invalide : " + valeur);
        }
        valeur = valeur.toLowerCase();
    }
}
