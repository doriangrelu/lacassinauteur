package fr.lacassinauteur.site.actualite.domain.port;

import fr.lacassinauteur.site.actualite.domain.model.Actualite;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FakeActualiteRepository implements ActualiteRepository {

    private final ConcurrentMap<UUID, Actualite> stockage = new ConcurrentHashMap<>();

    @Override
    public Actualite save(Actualite actualite) {
        stockage.put(actualite.id(), actualite);
        return actualite;
    }

    @Override
    public Optional<Actualite> findById(UUID id) {
        return Optional.ofNullable(stockage.get(id));
    }

    @Override
    public void supprimer(UUID id) {
        stockage.remove(id);
    }

    @Override
    public List<Actualite> findAllOrderByDateDesc() {
        return stockage.values().stream()
                .sorted(Comparator.comparing(Actualite::date).reversed())
                .toList();
    }
}
