package fr.lacassinauteur.site.biographie.domain.model;

import java.util.UUID;

/**
 * Présentation publique de l'auteur : un texte et une photo, affichés sur la page
 * « Auteur » du site public.
 *
 * <p><strong>Enregistrement unique</strong> : contrairement aux autres entités du
 * projet, il n'existe qu'une seule biographie, créée une fois par le seeder puis
 * uniquement modifiée — jamais créée ni supprimée depuis le back-office. L'unicité
 * est garantie côté base (contrainte, cf. migration V11) et non seulement par
 * convention applicative.
 */
public class Biographie {

    private final UUID id;
    private String texte;
    private String photoUrl;

    public Biographie(UUID id, String texte, String photoUrl) {
        this.id = id;
        this.texte = texte;
        this.photoUrl = blancVersNull(photoUrl);
    }

    public static Biographie initiale(String texte, String photoUrl) {
        return new Biographie(UUID.randomUUID(), texte, photoUrl);
    }

    public void modifier(String texte, String photoUrl) {
        this.texte = texte;
        this.photoUrl = blancVersNull(photoUrl);
    }

    /**
     * Même normalisation que {@code Actualite} : une photo « vide » n'est pas une
     * valeur significative, la ramener à null évite qu'un {@code th:if} de
     * présentation la considère comme renseignée et affiche une image cassée.
     */
    private static String blancVersNull(String valeur) {
        return (valeur == null || valeur.isBlank()) ? null : valeur;
    }

    public UUID id() {
        return id;
    }

    public String texte() {
        return texte;
    }

    public String photoUrl() {
        return photoUrl;
    }
}
