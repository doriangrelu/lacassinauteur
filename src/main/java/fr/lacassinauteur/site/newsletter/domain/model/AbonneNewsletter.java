package fr.lacassinauteur.site.newsletter.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Abonné à la newsletter. Le cycle de vie (double opt-in) est porté par
 * {@link StatutAbonnement} :
 * inscription (statut {@code EN_ATTENTE_CONFIRMATION}, {@link #jetonConfirmation}
 * généré) -&gt; confirmation ({@link #confirmer()}, statut {@code CONFIRME}) -&gt;
 * éventuelle désinscription ({@link #desinscrire()}, statut {@code DESINSCRIT}).
 *
 * <p><strong>Choix sur le jeton</strong> (cf. ADR-0013) : {@link #jetonConfirmation}
 * sert à la fois de jeton de confirmation (lien envoyé à l'inscription) et de jeton
 * de désinscription (lien inclus dans l'email de bienvenue puis, plus tard, dans
 * chaque envoi) — un seul jeton stable par abonné plutôt que deux valeurs à gérer en
 * parallèle. Il n'est régénéré qu'en cas de ré-inscription après désinscription (cf.
 * {@link #relancerInscription()}), pour invalider l'ancien lien.</p>
 */
public class AbonneNewsletter {

    private final UUID id;
    private String prenom;
    private final Email email;
    private StatutAbonnement statut;
    private LocalDateTime dateInscription;
    private LocalDateTime dateConfirmation;
    private UUID jetonConfirmation;

    public AbonneNewsletter(UUID id, String prenom, Email email, StatutAbonnement statut,
                             LocalDateTime dateInscription, LocalDateTime dateConfirmation, UUID jetonConfirmation) {
        this.id = id;
        this.prenom = prenom;
        this.email = email;
        this.statut = statut;
        this.dateInscription = dateInscription;
        this.dateConfirmation = dateConfirmation;
        this.jetonConfirmation = jetonConfirmation;
    }

    public static AbonneNewsletter creer(String prenom, Email email) {
        return new AbonneNewsletter(
                UUID.randomUUID(), prenom, email, StatutAbonnement.EN_ATTENTE_CONFIRMATION,
                LocalDateTime.now(), null, UUID.randomUUID());
    }

    /** Confirme l'inscription (lien de confirmation cliqué). Idempotent côté use case appelant. */
    public void confirmer() {
        this.statut = StatutAbonnement.CONFIRME;
        this.dateConfirmation = LocalDateTime.now();
    }

    /** Désinscrit l'abonné (lien de désinscription cliqué). */
    public void desinscrire() {
        this.statut = StatutAbonnement.DESINSCRIT;
    }

    /**
     * Relance le parcours de double opt-in : régénère le jeton et repart de
     * {@code EN_ATTENTE_CONFIRMATION}. Utilisé pour une ré-inscription après
     * désinscription (l'ancien lien de désinscription doit être invalidé).
     */
    public void relancerInscription() {
        this.statut = StatutAbonnement.EN_ATTENTE_CONFIRMATION;
        this.jetonConfirmation = UUID.randomUUID();
        this.dateInscription = LocalDateTime.now();
        this.dateConfirmation = null;
    }

    public UUID id() {
        return id;
    }

    public String prenom() {
        return prenom;
    }

    public Email email() {
        return email;
    }

    public StatutAbonnement statut() {
        return statut;
    }

    public LocalDateTime dateInscription() {
        return dateInscription;
    }

    public LocalDateTime dateConfirmation() {
        return dateConfirmation;
    }

    public UUID jetonConfirmation() {
        return jetonConfirmation;
    }
}
