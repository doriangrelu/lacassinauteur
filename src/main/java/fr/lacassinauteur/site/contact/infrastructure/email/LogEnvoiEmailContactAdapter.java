package fr.lacassinauteur.site.contact.infrastructure.email;

import fr.lacassinauteur.site.contact.domain.model.MessageContact;
import fr.lacassinauteur.site.contact.domain.port.EnvoiEmailContactPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Adaptateur de développement : n'envoie aucun email réel, se contente de logguer le
 * message. Évite de dépendre d'identifiants SMTP réels pour tester le parcours
 * complet en local, cf. ADR-0014 (même approche que
 * {@code newsletter.infrastructure.email.LogEmailAdapter}, ADR-0013).
 */
@Component
@Profile("dev")
public class LogEnvoiEmailContactAdapter implements EnvoiEmailContactPort {

    private static final Logger LOG = LoggerFactory.getLogger(LogEnvoiEmailContactAdapter.class);

    @Override
    public void envoyerNotification(MessageContact message) {
        LOG.info("""
                [contact/dev] Nouveau message de {} <{}>
                Objet : {}
                Message : {}""",
                message.nom(), message.email(), message.objet(), message.message());
    }
}
