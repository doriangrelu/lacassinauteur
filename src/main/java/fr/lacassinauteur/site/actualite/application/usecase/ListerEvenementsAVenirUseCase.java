package fr.lacassinauteur.site.actualite.application.usecase;

import fr.lacassinauteur.site.actualite.application.result.ActualiteResult;
import fr.lacassinauteur.site.actualite.domain.model.TypeActualite;
import fr.lacassinauteur.site.actualite.domain.port.ActualiteRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * Page publique : événements à venir triés du plus proche au plus lointain.
 */
@Component
public class ListerEvenementsAVenirUseCase {

    private final ActualiteRepository actualiteRepository;

    public ListerEvenementsAVenirUseCase(ActualiteRepository actualiteRepository) {
        this.actualiteRepository = actualiteRepository;
    }

    public List<ActualiteResult> execute() {
        LocalDate aujourdHui = LocalDate.now();
        return actualiteRepository.findAllOrderByDateDesc().stream()
                .map(actualite -> ActualiteResult.depuis(actualite, aujourdHui))
                .filter(resultat -> resultat.type() == TypeActualite.EVENEMENT_A_VENIR)
                .sorted(Comparator.comparing(ActualiteResult::date))
                .toList();
    }
}
