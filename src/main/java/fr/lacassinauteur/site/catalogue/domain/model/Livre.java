package fr.lacassinauteur.site.catalogue.domain.model;

import java.util.Optional;
import java.util.UUID;

public class Livre {

    private final UUID id;
    private final String slug;
    private UUID collectionId;
    private String titre;
    private String sousTitre;
    private String couvertureUrl;
    private String pitchCourt;
    private String resume;
    private LienAchat lienAchat;
    private int ordre;
    private boolean derniereParution;

    public Livre(UUID id, String slug, UUID collectionId, String titre, String sousTitre, String couvertureUrl,
                 String pitchCourt, String resume, LienAchat lienAchat, int ordre, boolean derniereParution) {
        this.id = id;
        this.slug = slug;
        this.collectionId = collectionId;
        this.titre = titre;
        this.sousTitre = sousTitre;
        this.couvertureUrl = couvertureUrl;
        this.pitchCourt = pitchCourt;
        this.resume = resume;
        this.lienAchat = lienAchat;
        this.ordre = ordre;
        this.derniereParution = derniereParution;
    }

    public static Livre creer(String slug, UUID collectionId, String titre, String sousTitre, String couvertureUrl,
                               String pitchCourt, String resume, int ordre) {
        return new Livre(UUID.randomUUID(), slug, collectionId, titre, sousTitre, couvertureUrl, pitchCourt, resume, null, ordre, false);
    }

    public void modifier(UUID collectionId, String titre, String sousTitre, String couvertureUrl,
                          String pitchCourt, String resume, int ordre) {
        this.collectionId = collectionId;
        this.titre = titre;
        this.sousTitre = sousTitre;
        this.couvertureUrl = couvertureUrl;
        this.pitchCourt = pitchCourt;
        this.resume = resume;
        this.ordre = ordre;
    }

    public void publier(LienAchat lienAchat) {
        this.lienAchat = lienAchat;
    }

    public void retirerLienAchat() {
        this.lienAchat = null;
    }

    public void changerOrdre(int ordre) {
        this.ordre = ordre;
    }

    public void marquerCommeDerniereParution() {
        this.derniereParution = true;
    }

    public void retirerDerniereParution() {
        this.derniereParution = false;
    }

    public UUID id() {
        return id;
    }

    public String slug() {
        return slug;
    }

    public UUID collectionId() {
        return collectionId;
    }

    public String titre() {
        return titre;
    }

    public String sousTitre() {
        return sousTitre;
    }

    public String couvertureUrl() {
        return couvertureUrl;
    }

    public String pitchCourt() {
        return pitchCourt;
    }

    public String resume() {
        return resume;
    }

    public Optional<LienAchat> lienAchat() {
        return Optional.ofNullable(lienAchat);
    }

    public boolean disponible() {
        return lienAchat != null;
    }

    public int ordre() {
        return ordre;
    }

    public boolean derniereParution() {
        return derniereParution;
    }
}
