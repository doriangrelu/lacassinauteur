package fr.lacassinauteur.site.catalogue.domain.model;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class AvisLecteur {

    private final UUID id;
    private final UUID livreId;
    private final String nomAuteurAvis;
    private final String texte;
    private Integer note;
    private StatutAvis statut;
    private final LocalDateTime dateSoumission;

    public AvisLecteur(UUID id, UUID livreId, String nomAuteurAvis, String texte, Integer note,
                        StatutAvis statut, LocalDateTime dateSoumission) {
        this.id = id;
        this.livreId = livreId;
        this.nomAuteurAvis = nomAuteurAvis;
        this.texte = texte;
        this.note = note;
        this.statut = statut;
        this.dateSoumission = dateSoumission;
    }

    /**
     * Soumission publique : toujours créée avec le statut {@link StatutAvis#EN_ATTENTE},
     * modérée ensuite depuis le back-office (cf. {@link #approuver()} / {@link #rejeter()}).
     */
    public static AvisLecteur soumettre(UUID livreId, String nomAuteurAvis, String texte, Integer note) {
        return new AvisLecteur(UUID.randomUUID(), livreId, nomAuteurAvis, texte, note, StatutAvis.EN_ATTENTE,
                LocalDateTime.now());
    }

    public void approuver() {
        this.statut = StatutAvis.PUBLIE;
    }

    public void rejeter() {
        this.statut = StatutAvis.REJETE;
    }

    public UUID id() {
        return id;
    }

    public UUID livreId() {
        return livreId;
    }

    public String nomAuteurAvis() {
        return nomAuteurAvis;
    }

    public String texte() {
        return texte;
    }

    public Optional<Integer> note() {
        return Optional.ofNullable(note);
    }

    public StatutAvis statut() {
        return statut;
    }

    public LocalDateTime dateSoumission() {
        return dateSoumission;
    }
}
