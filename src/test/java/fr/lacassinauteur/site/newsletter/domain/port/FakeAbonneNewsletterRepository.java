package fr.lacassinauteur.site.newsletter.domain.port;

import fr.lacassinauteur.site.newsletter.domain.model.AbonneNewsletter;
import fr.lacassinauteur.site.newsletter.domain.model.Email;
import fr.lacassinauteur.site.newsletter.domain.model.StatutAbonnement;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FakeAbonneNewsletterRepository implements AbonneNewsletterRepository {

    private final ConcurrentMap<UUID, AbonneNewsletter> stockage = new ConcurrentHashMap<>();

    @Override
    public AbonneNewsletter save(AbonneNewsletter abonne) {
        stockage.put(abonne.id(), abonne);
        return abonne;
    }

    @Override
    public Optional<AbonneNewsletter> findById(UUID id) {
        return Optional.ofNullable(stockage.get(id));
    }

    @Override
    public Optional<AbonneNewsletter> findByEmail(Email email) {
        return stockage.values().stream().filter(abonne -> abonne.email().equals(email)).findFirst();
    }

    @Override
    public Optional<AbonneNewsletter> findByJeton(UUID jetonConfirmation) {
        return stockage.values().stream()
                .filter(abonne -> abonne.jetonConfirmation().equals(jetonConfirmation))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(Email email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public List<AbonneNewsletter> findAllOrderByDateInscriptionDesc() {
        return stockage.values().stream()
                .sorted(Comparator.comparing(AbonneNewsletter::dateInscription).reversed())
                .toList();
    }

    @Override
    public List<AbonneNewsletter> findAllConfirmes() {
        return stockage.values().stream()
                .filter(abonne -> abonne.statut() == StatutAbonnement.CONFIRME)
                .toList();
    }
}
