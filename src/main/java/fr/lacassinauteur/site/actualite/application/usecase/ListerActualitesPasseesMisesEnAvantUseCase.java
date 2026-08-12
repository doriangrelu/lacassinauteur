package fr.lacassinauteur.site.actualite.application.usecase;

import fr.lacassinauteur.site.actualite.application.result.ActualiteResult;
import fr.lacassinauteur.site.actualite.domain.model.TypeActualite;
import fr.lacassinauteur.site.actualite.domain.port.ActualiteRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Page publique : sélection manuelle d'actualités passées (« dernières actualités »)
 * — pas l'historique complet, cf. domain-model.md.
 */
@Component
public class ListerActualitesPasseesMisesEnAvantUseCase {

    private final ActualiteRepository actualiteRepository;

    public ListerActualitesPasseesMisesEnAvantUseCase(ActualiteRepository actualiteRepository) {
        this.actualiteRepository = actualiteRepository;
    }

    public List<ActualiteResult> execute() {
        LocalDate aujourdHui = LocalDate.now();
        return actualiteRepository.findAllOrderByDateDesc().stream()
                .map(actualite -> ActualiteResult.depuis(actualite, aujourdHui))
                .filter(resultat -> resultat.type() == TypeActualite.ACTUALITE_PASSEE && resultat.misEnAvant())
                .toList();
    }
}
