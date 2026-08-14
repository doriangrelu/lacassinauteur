package fr.lacassinauteur.site.catalogue.domain.port;

import fr.lacassinauteur.site.catalogue.domain.model.Univers;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FakeUniversRepository implements UniversRepository {

    private final ConcurrentMap<UUID, Univers> stockage = new ConcurrentHashMap<>();

    @Override
    public Univers save(Univers univers) {
        stockage.put(univers.id(), univers);
        return univers;
    }

    @Override
    public Optional<Univers> findById(UUID id) {
        return Optional.ofNullable(stockage.get(id));
    }

    @Override
    public Optional<Univers> findBySlug(String slug) {
        return stockage.values().stream().filter(univers -> univers.slug().equals(slug)).findFirst();
    }

    @Override
    public boolean existsBySlug(String slug) {
        return findBySlug(slug).isPresent();
    }

    @Override
    public List<Univers> findAllOrderByOrdre() {
        return stockage.values().stream()
                .sorted(Comparator.comparingInt(Univers::ordre))
                .toList();
    }
}
