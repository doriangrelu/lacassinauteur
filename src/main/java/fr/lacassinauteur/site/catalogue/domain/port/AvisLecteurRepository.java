package fr.lacassinauteur.site.catalogue.domain.port;

import fr.lacassinauteur.site.catalogue.domain.model.AvisLecteur;
import fr.lacassinauteur.site.catalogue.domain.model.StatutAvis;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AvisLecteurRepository {

    AvisLecteur save(AvisLecteur avisLecteur);

    Optional<AvisLecteur> findById(UUID id);

    List<AvisLecteur> findAllOrderByDateSoumissionDesc();

    List<AvisLecteur> findByLivreIdAndStatutOrderByDateSoumissionDesc(UUID livreId, StatutAvis statut);
}
