package fr.lacassinauteur.site.catalogue.domain.model;

import java.util.UUID;

public class Collection {

    private final UUID id;
    private UUID universId;
    private String nom;
    private String sousTitre;
    private String texte;
    private int ordre;

    public Collection(UUID id, UUID universId, String nom, String sousTitre, String texte, int ordre) {
        this.id = id;
        this.universId = universId;
        this.nom = nom;
        this.sousTitre = sousTitre;
        this.texte = texte;
        this.ordre = ordre;
    }

    public static Collection creer(UUID universId, String nom, String sousTitre, String texte, int ordre) {
        return new Collection(UUID.randomUUID(), universId, nom, sousTitre, texte, ordre);
    }

    public void modifier(UUID universId, String nom, String sousTitre, String texte, int ordre) {
        this.universId = universId;
        this.nom = nom;
        this.sousTitre = sousTitre;
        this.texte = texte;
        this.ordre = ordre;
    }

    public UUID id() {
        return id;
    }

    public UUID universId() {
        return universId;
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

    public int ordre() {
        return ordre;
    }
}
