package fr.lacassinauteur.site.identity.domain.model;

public record MotDePasseHache(String valeur) {

    public MotDePasseHache {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Le mot de passe haché ne peut pas être vide");
        }
    }
}
