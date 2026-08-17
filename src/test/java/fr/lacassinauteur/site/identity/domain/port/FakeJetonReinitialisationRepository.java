package fr.lacassinauteur.site.identity.domain.port;

import fr.lacassinauteur.site.identity.domain.model.JetonReinitialisation;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FakeJetonReinitialisationRepository implements JetonReinitialisationRepository {

    private final ConcurrentMap<UUID, JetonReinitialisation> stockage = new ConcurrentHashMap<>();

    @Override
    public JetonReinitialisation save(JetonReinitialisation jeton) {
        stockage.put(jeton.id(), jeton);
        return jeton;
    }

    @Override
    public Optional<JetonReinitialisation> findById(UUID id) {
        return Optional.ofNullable(stockage.get(id));
    }

    @Override
    public Optional<JetonReinitialisation> findValidePourUtilisateur(UUID utilisateurId) {
        return stockage.values().stream()
                .filter(jeton -> jeton.utilisateurId().equals(utilisateurId))
                .filter(jeton -> jeton.dateExpiration().isAfter(Instant.now()))
                .findFirst();
    }

    @Override
    public void deleteById(UUID id) {
        stockage.remove(id);
    }

    @Override
    public void deleteParUtilisateur(UUID utilisateurId) {
        stockage.values().removeIf(jeton -> jeton.utilisateurId().equals(utilisateurId));
    }
}
