package fr.lacassinauteur.site.catalogue.application.usecase.univers;

import fr.lacassinauteur.site.catalogue.application.result.UniversResult;
import fr.lacassinauteur.site.catalogue.domain.exception.UniversIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.port.UniversRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsulterUniversUseCase {

    private final UniversRepository universRepository;

    public ConsulterUniversUseCase(UniversRepository universRepository) {
        this.universRepository = universRepository;
    }

    public UniversResult execute(UUID universId) {
        return universRepository.findById(universId)
                .map(UniversResult::depuis)
                .orElseThrow(() -> new UniversIntrouvableException(universId));
    }
}
