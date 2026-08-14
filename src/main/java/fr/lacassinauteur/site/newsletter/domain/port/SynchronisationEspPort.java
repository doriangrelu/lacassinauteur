package fr.lacassinauteur.site.newsletter.domain.port;

import fr.lacassinauteur.site.newsletter.domain.model.AbonneNewsletter;

/**
 * Synchronise les abonnés confirmés avec la liste de contacts de l'ESP tiers
 * (Brevo), pour que Thierry puisse composer et envoyer ses newsletters directement
 * depuis l'outil Brevo plutôt que via un éditeur de campagnes maison (cf.
 * ADR-0017 : décision explicite de ne pas réimplémenter ce que Brevo offre déjà).
 * Implémentations : {@code infrastructure.email.BrevoContactSyncAdapter}
 * (prod/défaut) et {@code infrastructure.email.LogSynchronisationEspAdapter}
 * (profil {@code dev}).
 */
public interface SynchronisationEspPort {

    /** Ajoute ou met à jour l'abonné dans la liste Brevo (appelé à la confirmation). */
    void ajouterOuMettreAJour(AbonneNewsletter abonne);

    /** Retire l'abonné de la liste Brevo (appelé à la désinscription). */
    void retirer(AbonneNewsletter abonne);
}
