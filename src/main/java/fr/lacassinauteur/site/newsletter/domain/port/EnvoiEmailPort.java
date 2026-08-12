package fr.lacassinauteur.site.newsletter.domain.port;

import fr.lacassinauteur.site.newsletter.domain.model.Email;

/**
 * Port d'envoi des emails transactionnels de la newsletter (double opt-in). Volontairement
 * minimal : pas d'envoi de campagne (cf. ADR-0013, {@code CampagneNewsletter} hors
 * périmètre v1). Implémentations : {@code infrastructure.email.BrevoEmailAdapter}
 * (prod/défaut) et {@code infrastructure.email.LogEmailAdapter} (profil {@code dev}).
 */
public interface EnvoiEmailPort {

    /** Email de double opt-in, envoyé à l'inscription (et à sa relance). */
    void envoyerEmailConfirmation(Email destinataire, String prenom, String lienConfirmation);

    /**
     * Email de bienvenue envoyé une fois l'inscription confirmée, incluant le lien de
     * désinscription (cf. domain-model.md : « lien présent sur chaque envoi »).
     */
    void envoyerEmailBienvenue(Email destinataire, String prenom, String lienDesinscription);
}
