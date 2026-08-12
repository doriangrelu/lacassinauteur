package fr.lacassinauteur.site.identity.domain.port;

import fr.lacassinauteur.site.identity.domain.model.Email;
import fr.lacassinauteur.site.identity.domain.model.Utilisateur;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FakeUtilisateurRepository implements UtilisateurRepository {

    private final ConcurrentMap<UUID, Utilisateur> stockage = new ConcurrentHashMap<>();

    @Override
    public Utilisateur save(Utilisateur utilisateur) {
        stockage.put(utilisateur.id(), utilisateur);
        return utilisateur;
    }

    @Override
    public Optional<Utilisateur> findById(UUID id) {
        return Optional.ofNullable(stockage.get(id));
    }

    @Override
    public Optional<Utilisateur> findByEmail(Email email) {
        return stockage.values().stream()
                .filter(utilisateur -> utilisateur.email().equals(email))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(Email email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public List<Utilisateur> findAll() {
        return List.copyOf(stockage.values());
    }
}
