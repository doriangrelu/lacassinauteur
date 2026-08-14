package fr.lacassinauteur.site.catalogue.domain.port;

import fr.lacassinauteur.site.catalogue.domain.model.Collection;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FakeCollectionRepository implements CollectionRepository {

    private final ConcurrentMap<UUID, Collection> stockage = new ConcurrentHashMap<>();

    @Override
    public Collection save(Collection collection) {
        stockage.put(collection.id(), collection);
        return collection;
    }

    @Override
    public Optional<Collection> findById(UUID id) {
        return Optional.ofNullable(stockage.get(id));
    }

    @Override
    public Optional<Collection> findBySlug(String slug) {
        return stockage.values().stream().filter(collection -> collection.slug().equals(slug)).findFirst();
    }

    @Override
    public boolean existsBySlug(String slug) {
        return findBySlug(slug).isPresent();
    }

    @Override
    public List<Collection> findByUniversIdOrderByOrdre(UUID universId) {
        return stockage.values().stream()
                .filter(collection -> collection.universId().equals(universId))
                .sorted(Comparator.comparingInt(Collection::ordre))
                .toList();
    }

    @Override
    public List<Collection> findAllOrderByOrdre() {
        return stockage.values().stream()
                .sorted(Comparator.comparingInt(Collection::ordre))
                .toList();
    }
}
