package fr.lacassinauteur.site.contact.application.usecase;

import fr.lacassinauteur.site.contact.domain.exception.MessageContactIntrouvableException;
import fr.lacassinauteur.site.contact.domain.model.MessageContact;
import fr.lacassinauteur.site.contact.domain.port.MessageContactRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MarquerMessageTraiteUseCase {

    private final MessageContactRepository messageContactRepository;

    public MarquerMessageTraiteUseCase(MessageContactRepository messageContactRepository) {
        this.messageContactRepository = messageContactRepository;
    }

    public void execute(UUID id) {
        MessageContact message = messageContactRepository.findById(id)
                .orElseThrow(() -> new MessageContactIntrouvableException(id));

        message.marquerTraite();
        messageContactRepository.save(message);
    }
}
