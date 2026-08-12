package fr.lacassinauteur.site.actualite.application.usecase;

import fr.lacassinauteur.site.actualite.application.result.ActualiteResult;
import fr.lacassinauteur.site.actualite.domain.port.ActualiteRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Liste complète pour le back-office (toutes les actualités, quel que soit leur
 * type dérivé ou leur mise en avant), triée de la plus récente à la plus ancienne.
 */
@Component
public class ListerActualitesUseCase {

    private final ActualiteRepository actualiteRepository;

    public ListerActualitesUseCase(ActualiteRepository actualiteRepository) {
        this.actualiteRepository = actualiteRepository;
    }

    public List<ActualiteResult> execute() {
        return actualiteRepository.findAllOrderByDateDesc().stream()
                .map(ActualiteResult::depuis)
                .toList();
    }
}
