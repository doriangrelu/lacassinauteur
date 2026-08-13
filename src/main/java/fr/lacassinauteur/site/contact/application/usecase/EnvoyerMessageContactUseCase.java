package fr.lacassinauteur.site.contact.application.usecase;

import fr.lacassinauteur.site.contact.application.command.EnvoyerMessageContactCommand;
import fr.lacassinauteur.site.contact.application.result.MessageContactResult;
import fr.lacassinauteur.site.contact.domain.model.MessageContact;
import fr.lacassinauteur.site.contact.domain.port.EnvoiEmailContactPort;
import fr.lacassinauteur.site.contact.domain.port.MessageContactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EnvoyerMessageContactUseCase {

    private static final Logger LOG = LoggerFactory.getLogger(EnvoyerMessageContactUseCase.class);

    private final MessageContactRepository messageContactRepository;
    private final EnvoiEmailContactPort envoiEmailContactPort;

    public EnvoyerMessageContactUseCase(MessageContactRepository messageContactRepository, EnvoiEmailContactPort envoiEmailContactPort) {
        this.messageContactRepository = messageContactRepository;
        this.envoiEmailContactPort = envoiEmailContactPort;
    }

    public MessageContactResult execute(EnvoyerMessageContactCommand command) {
        MessageContact message = MessageContact.creer(command.nom(), command.email(), command.objet(), command.message());
        message = messageContactRepository.save(message);

        // Le message est déjà enregistré : un échec de notification (SMTP non
        // encore configuré, hôte injoignable...) ne doit pas transformer un envoi
        // réussi en erreur 500 pour le visiteur. Thierry verra quand même le
        // message dans le back-office.
        try {
            envoiEmailContactPort.envoyerNotification(message);
        } catch (RuntimeException exception) {
            LOG.warn("Échec de la notification email pour le message de contact {}", message.id(), exception);
        }

        return MessageContactResult.depuis(message);
    }
}
