package fr.lacassinauteur.site.catalogue.domain.model;

import java.util.UUID;

public class Univers {

    private final UUID id;
    private final String slug;
    private String nom;
    private String sousTitre;
    private String texte;
    private String photoUrl;
    private int ordre;

    public Univers(UUID id, String slug, String nom, String sousTitre, String texte, String photoUrl, int ordre) {
        this.id = id;
        this.slug = slug;
        this.nom = nom;
        this.sousTitre = sousTitre;
        this.texte = texte;
        this.photoUrl = photoUrl;
        this.ordre = ordre;
    }

    public static Univers creer(String slug, String nom, String sousTitre, String texte, String photoUrl, int ordre) {
        return new Univers(UUID.randomUUID(), slug, nom, sousTitre, texte, photoUrl, ordre);
    }

    public void modifier(String nom, String sousTitre, String texte, String photoUrl, int ordre) {
        this.nom = nom;
        this.sousTitre = sousTitre;
        this.texte = texte;
        this.photoUrl = photoUrl;
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

    public int ordre() {
        return ordre;
    }
}
