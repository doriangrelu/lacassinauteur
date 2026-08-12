package fr.lacassinauteur.site.catalogue.domain.port;

import fr.lacassinauteur.site.catalogue.domain.model.Livre;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FakeLivreRepository implements LivreRepository {

    private final ConcurrentMap<UUID, Livre> stockage = new ConcurrentHashMap<>();

    @Override
    public Livre save(Livre livre) {
        stockage.put(livre.id(), livre);
        return livre;
    }

    @Override
    public Optional<Livre> findById(UUID id) {
        return Optional.ofNullable(stockage.get(id));
    }

    @Override
    public List<Livre> findByCollectionIdOrderByOrdre(UUID collectionId) {
        return stockage.values().stream()
                .filter(livre -> livre.collectionId().equals(collectionId))
                .sorted(Comparator.comparingInt(Livre::ordre))
                .toList();
    }

    @Override
    public List<Livre> findAllOrderByOrdre() {
        return stockage.values().stream()
                .sorted(Comparator.comparingInt(Livre::ordre))
                .toList();
    }

    @Override
    public Optional<Livre> findDerniereParution() {
        return stockage.values().stream()
                .filter(Livre::derniereParution)
                .findFirst();
    }
}
