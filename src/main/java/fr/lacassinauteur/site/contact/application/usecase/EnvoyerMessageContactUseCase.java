package fr.lacassinauteur.site.contact.application.usecase;

import fr.lacassinauteur.site.contact.application.command.EnvoyerMessageContactCommand;
import fr.lacassinauteur.site.contact.application.result.MessageContactResult;
import fr.lacassinauteur.site.contact.domain.model.MessageContact;
import fr.lacassinauteur.site.contact.domain.port.EnvoiEmailContactPort;
import fr.lacassinauteur.site.contact.domain.port.MessageContactRepository;
import org.springframework.stereotype.Component;

@Component
public class EnvoyerMessageContactUseCase {

    private final MessageContactRepository messageContactRepository;
    private final EnvoiEmailContactPort envoiEmailContactPort;

    public EnvoyerMessageContactUseCase(MessageContactRepository messageContactRepository, EnvoiEmailContactPort envoiEmailContactPort) {
        this.messageContactRepository = messageContactRepository;
        this.envoiEmailContactPort = envoiEmailContactPort;
    }

    public MessageContactResult execute(EnvoyerMessageContactCommand command) {
        MessageContact message = MessageContact.creer(command.nom(), command.email(), command.objet(), command.message());
        message = messageContactRepository.save(message);

        envoiEmailContactPort.envoyerNotification(message);

        return MessageContactResult.depuis(message);
    }
}
