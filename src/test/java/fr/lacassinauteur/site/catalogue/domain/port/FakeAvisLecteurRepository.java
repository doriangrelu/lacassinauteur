package fr.lacassinauteur.site.catalogue.domain.port;

import fr.lacassinauteur.site.catalogue.domain.model.AvisLecteur;
import fr.lacassinauteur.site.catalogue.domain.model.StatutAvis;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FakeAvisLecteurRepository implements AvisLecteurRepository {

    private final ConcurrentMap<UUID, AvisLecteur> stockage = new ConcurrentHashMap<>();

    @Override
    public AvisLecteur save(AvisLecteur avisLecteur) {
        stockage.put(avisLecteur.id(), avisLecteur);
        return avisLecteur;
    }

    @Override
    public Optional<AvisLecteur> findById(UUID id) {
        return Optional.ofNullable(stockage.get(id));
    }

    @Override
    public List<AvisLecteur> findAllOrderByDateSoumissionDesc() {
        return stockage.values().stream()
                .sorted(Comparator.comparing(AvisLecteur::dateSoumission).reversed())
                .toList();
    }

    @Override
    public List<AvisLecteur> findByLivreIdAndStatutOrderByDateSoumissionDesc(UUID livreId, StatutAvis statut) {
        return stockage.values().stream()
                .filter(avis -> avis.livreId().equals(livreId) && avis.statut() == statut)
                .sorted(Comparator.comparing(AvisLecteur::dateSoumission).reversed())
                .toList();
    }
}
