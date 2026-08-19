package fr.lacassinauteur.site.legal.domain.model;

import java.util.UUID;

/**
 * Variables injectées dans les pages légales publiques (mentions légales et
 * politique de confidentialité).
 *
 * <p><strong>Enregistrement unique</strong>, même parti pris que
 * {@code Biographie} (cf. ADR-0028) : créé une fois par le seeder puis uniquement
 * modifié, l'unicité étant garantie en base par une contrainte plutôt que par
 * convention applicative.
 *
 * <p>Tous les champs sont facultatifs côté domaine : le site doit rester
 * affichable même tant que l'éditeur n'a pas renseigné son identité complète. Les
 * gabarits affichent alors une mention « à compléter » plutôt qu'un vide
 * silencieux, pour que le manque se voie.
 */
public class InformationsLegales {

    private final UUID id;
    private String editeurNom;
    private String editeurStatut;
    private String editeurAdresse;
    private String editeurEmail;
    private String directeurPublication;
    private String hebergeurNom;
    private String hebergeurAdresse;
    private int conservationNewsletterMois;
    private int conservationContactMois;

    public InformationsLegales(UUID id, String editeurNom, String editeurStatut, String editeurAdresse,
                                String editeurEmail, String directeurPublication, String hebergeurNom,
                                String hebergeurAdresse, int conservationNewsletterMois,
                                int conservationContactMois) {
        this.id = id;
        this.editeurNom = blancVersNull(editeurNom);
        this.editeurStatut = blancVersNull(editeurStatut);
        this.editeurAdresse = blancVersNull(editeurAdresse);
        this.editeurEmail = blancVersNull(editeurEmail);
        this.directeurPublication = blancVersNull(directeurPublication);
        this.hebergeurNom = blancVersNull(hebergeurNom);
        this.hebergeurAdresse = blancVersNull(hebergeurAdresse);
        this.conservationNewsletterMois = conservationNewsletterMois;
        this.conservationContactMois = conservationContactMois;
    }

    public static InformationsLegales initiales(String hebergeurNom, String hebergeurAdresse,
                                                 int conservationNewsletterMois, int conservationContactMois) {
        return new InformationsLegales(UUID.randomUUID(), null, null, null, null, null,
                hebergeurNom, hebergeurAdresse, conservationNewsletterMois, conservationContactMois);
    }

    public void modifier(String editeurNom, String editeurStatut, String editeurAdresse, String editeurEmail,
                          String directeurPublication, String hebergeurNom, String hebergeurAdresse,
                          int conservationNewsletterMois, int conservationContactMois) {
        this.editeurNom = blancVersNull(editeurNom);
        this.editeurStatut = blancVersNull(editeurStatut);
        this.editeurAdresse = blancVersNull(editeurAdresse);
        this.editeurEmail = blancVersNull(editeurEmail);
        this.directeurPublication = blancVersNull(directeurPublication);
        this.hebergeurNom = blancVersNull(hebergeurNom);
        this.hebergeurAdresse = blancVersNull(hebergeurAdresse);
        this.conservationNewsletterMois = conservationNewsletterMois;
        this.conservationContactMois = conservationContactMois;
    }

    /**
     * Vrai quand les mentions obligatoires (LCEN) sont renseignées. Sert à alerter
     * l'auteur en back-office plutôt qu'à masquer la page : une page légale
     * incomplète reste préférable à une page absente.
     */
    public boolean completes() {
        return editeurNom != null && editeurAdresse != null && editeurEmail != null
                && directeurPublication != null && hebergeurNom != null;
    }

    private static String blancVersNull(String valeur) {
        return (valeur == null || valeur.isBlank()) ? null : valeur;
    }

    public UUID id() {
        return id;
    }

    public String editeurNom() {
        return editeurNom;
    }

    public String editeurStatut() {
        return editeurStatut;
    }

    public String editeurAdresse() {
        return editeurAdresse;
    }

    public String editeurEmail() {
        return editeurEmail;
    }

    public String directeurPublication() {
        return directeurPublication;
    }

    public String hebergeurNom() {
        return hebergeurNom;
    }

    public String hebergeurAdresse() {
        return hebergeurAdresse;
    }

    public int conservationNewsletterMois() {
        return conservationNewsletterMois;
    }

    public int conservationContactMois() {
        return conservationContactMois;
    }
}
