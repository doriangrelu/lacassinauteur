package fr.lacassinauteur.site.contact.application.usecase;

import fr.lacassinauteur.site.contact.application.result.MessageContactResult;
import fr.lacassinauteur.site.contact.domain.exception.MessageContactIntrouvableException;
import fr.lacassinauteur.site.contact.domain.model.MessageContact;
import fr.lacassinauteur.site.contact.domain.port.MessageContactRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Consulter un message le marque automatiquement comme lu (cf. StatutMessage). */
@Component
public class ConsulterMessageContactUseCase {

    private final MessageContactRepository messageContactRepository;

    public ConsulterMessageContactUseCase(MessageContactRepository messageContactRepository) {
        this.messageContactRepository = messageContactRepository;
    }

    public MessageContactResult execute(UUID id) {
        MessageContact message = messageContactRepository.findById(id)
                .orElseThrow(() -> new MessageContactIntrouvableException(id));

        message.marquerLu();
        message = messageContactRepository.save(message);

        return MessageContactResult.depuis(message);
    }
}
