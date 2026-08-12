package fr.lacassinauteur.site.catalogue.application.usecase.univers;

import fr.lacassinauteur.site.catalogue.application.result.UniversResult;
import fr.lacassinauteur.site.catalogue.domain.port.UniversRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListerUniversUseCase {

    private final UniversRepository universRepository;

    public ListerUniversUseCase(UniversRepository universRepository) {
        this.universRepository = universRepository;
    }

    public List<UniversResult> execute() {
        return universRepository.findAllOrderByOrdre().stream()
                .map(UniversResult::depuis)
                .toList();
    }
}
