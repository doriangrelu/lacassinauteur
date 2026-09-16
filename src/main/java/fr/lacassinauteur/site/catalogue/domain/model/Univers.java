package fr.lacassinauteur.site.catalogue.domain.model;

import java.util.Optional;
import java.util.UUID;

public class Univers {

    private final UUID id;
    private final String slug;
    private String nom;
    private String sousTitre;
    private String texte;
    private String photoUrl;
    private PhotoLegendee photoComplementaire;
    private int ordre;

    public Univers(UUID id, String slug, String nom, String sousTitre, String texte, String photoUrl,
                    PhotoLegendee photoComplementaire, int ordre) {
        this.id = id;
        this.slug = slug;
        this.nom = nom;
        this.sousTitre = sousTitre;
        this.texte = texte;
        this.photoUrl = photoUrl;
        this.photoComplementaire = photoComplementaire;
        this.ordre = ordre;
    }

    public static Univers creer(String slug, String nom, String sousTitre, String texte, String photoUrl,
                                 PhotoLegendee photoComplementaire, int ordre) {
        return new Univers(UUID.randomUUID(), slug, nom, sousTitre, texte, photoUrl, photoComplementaire, ordre);
    }

    public void modifier(String nom, String sousTitre, String texte, String photoUrl,
                          PhotoLegendee photoComplementaire, int ordre) {
        this.nom = nom;
        this.sousTitre = sousTitre;
        this.texte = texte;
        this.photoUrl = photoUrl;
        this.photoComplementaire = photoComplementaire;
        this.ordre = ordre;
    }

    public void changerOrdre(int ordre) {
        this.ordre = ordre;
    }

    public UUID id() {
        return id;
    }

    public String slug() {
        return slug;
    }

    public String nom() {
        return nom;
    }

    public String sousTitre() {
        return sousTitre;
    }

    public String texte() {
        return texte;
    }

    public String photoUrl() {
        return photoUrl;
    }

    public Optional<PhotoLegendee> photoComplementaire() {
        return Optional.ofNullable(photoComplementaire);
    }

    public int ordre() {
        return ordre;
    }
}
