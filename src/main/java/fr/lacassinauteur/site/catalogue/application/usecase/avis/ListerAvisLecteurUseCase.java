package fr.lacassinauteur.site.catalogue.application.usecase.avis;

import fr.lacassinauteur.site.catalogue.application.result.AvisLecteurResult;
import fr.lacassinauteur.site.catalogue.domain.port.AvisLecteurRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Listing back-office : tous les avis, les plus récents en tête, pour un écran de
 * modération unique (statut affiché par ligne plutôt que des écrans séparés par
 * statut).
 */
@Component
public class ListerAvisLecteurUseCase {

    private final AvisLecteurRepository avisLecteurRepository;

    public ListerAvisLecteurUseCase(AvisLecteurRepository avisLecteurRepository) {
        this.avisLecteurRepository = avisLecteurRepository;
    }

    public List<AvisLecteurResult> execute() {
        return avisLecteurRepository.findAllOrderByDateSoumissionDesc().stream()
                .map(AvisLecteurResult::depuis)
                .toList();
    }
}
