package fr.lacassinauteur.site.catalogue.application.usecase.avis;

import fr.lacassinauteur.site.catalogue.application.result.AvisLecteurResult;
import fr.lacassinauteur.site.catalogue.domain.model.StatutAvis;
import fr.lacassinauteur.site.catalogue.domain.port.AvisLecteurRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Listing public : uniquement les avis publiés (modérés) d'un livre, affichés sur sa
 * page de détail.
 */
@Component
public class ListerAvisLecteurPublieParLivreUseCase {

    private final AvisLecteurRepository avisLecteurRepository;

    public ListerAvisLecteurPublieParLivreUseCase(AvisLecteurRepository avisLecteurRepository) {
        this.avisLecteurRepository = avisLecteurRepository;
    }

    public List<AvisLecteurResult> execute(UUID livreId) {
        return avisLecteurRepository.findByLivreIdAndStatutOrderByDateSoumissionDesc(livreId, StatutAvis.PUBLIE).stream()
                .map(AvisLecteurResult::depuis)
                .toList();
    }
}
