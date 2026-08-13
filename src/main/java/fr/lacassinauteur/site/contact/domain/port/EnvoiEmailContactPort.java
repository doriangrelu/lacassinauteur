package fr.lacassinauteur.site.contact.domain.port;

import fr.lacassinauteur.site.contact.domain.model.MessageContact;

/**
 * Notifie l'auteur par email transactionnel qu'un nouveau message de contact est
 * arrivé. Implémentations : {@code infrastructure.email.SmtpEnvoiEmailContactAdapter}
 * (prod/défaut, SMTP réel) et {@code infrastructure.email.LogEnvoiEmailContactAdapter}
 * (profil {@code dev}) — cf. ADR-0014.
 */
public interface EnvoiEmailContactPort {

    void envoyerNotification(MessageContact message);
}
