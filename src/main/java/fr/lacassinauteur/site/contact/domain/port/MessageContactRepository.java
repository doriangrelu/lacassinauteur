package fr.lacassinauteur.site.contact.domain.port;

import fr.lacassinauteur.site.contact.domain.model.MessageContact;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageContactRepository {

    MessageContact save(MessageContact message);

    Optional<MessageContact> findById(UUID id);

    List<MessageContact> findAllOrderByDateReceptionDesc();
}
