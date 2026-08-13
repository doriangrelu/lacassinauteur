package fr.lacassinauteur.site.contact.application.usecase;

import fr.lacassinauteur.site.contact.application.result.MessageContactResult;
import fr.lacassinauteur.site.contact.domain.port.MessageContactRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListerMessagesContactUseCase {

    private final MessageContactRepository messageContactRepository;

    public ListerMessagesContactUseCase(MessageContactRepository messageContactRepository) {
        this.messageContactRepository = messageContactRepository;
    }

    public List<MessageContactResult> execute() {
        return messageContactRepository.findAllOrderByDateReceptionDesc().stream()
                .map(MessageContactResult::depuis)
                .toList();
    }
}
