package fr.lacassinauteur.site.contact.domain.port;

import fr.lacassinauteur.site.contact.domain.model.MessageContact;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FakeMessageContactRepository implements MessageContactRepository {

    private final ConcurrentMap<UUID, MessageContact> stockage = new ConcurrentHashMap<>();

    @Override
    public MessageContact save(MessageContact message) {
        stockage.put(message.id(), message);
        return message;
    }

    @Override
    public Optional<MessageContact> findById(UUID id) {
        return Optional.ofNullable(stockage.get(id));
    }

    @Override
    public List<MessageContact> findAllOrderByDateReceptionDesc() {
        return stockage.values().stream()
                .sorted(Comparator.comparing(MessageContact::dateReception).reversed())
                .toList();
    }
}
