package fr.lacassinauteur.site.shared.domain.model;

import java.text.Normalizer;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Identifiant lisible et stable utilisé dans les URLs publiques (SEO), généré une
 * fois à la création d'un contenu et jamais modifié ensuite pour ne pas casser les
 * liens/le référencement déjà construits.
 */
public record Slug(String valeur) {

    public Slug {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Le slug ne peut pas être vide");
        }
    }

    public static Slug depuis(String texte) {
        String normalise = Normalizer.normalize(texte, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");

        return new Slug(normalise.isBlank() ? "sans-titre" : normalise);
    }

    /**
     * Génère un slug à partir du texte donné, garanti unique selon le prédicat fourni
     * (ex. {@code slug -> repository.findBySlug(slug).isPresent()}), en ajoutant un
     * suffixe numérique en cas de collision.
     */
    public static Slug genererUnique(String texte, Predicate<String> slugDejaPris) {
        Slug base = depuis(texte);
        if (!slugDejaPris.test(base.valeur())) {
            return base;
        }

        int compteur = 2;
        Slug candidat;
        do {
            candidat = new Slug(base.valeur() + "-" + compteur);
            compteur++;
        } while (slugDejaPris.test(candidat.valeur()));

        return candidat;
    }
}
