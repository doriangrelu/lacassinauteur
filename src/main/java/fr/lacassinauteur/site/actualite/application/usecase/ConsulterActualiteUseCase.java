package fr.lacassinauteur.site.actualite.application.usecase;

import fr.lacassinauteur.site.actualite.application.result.ActualiteResult;
import fr.lacassinauteur.site.actualite.domain.exception.ActualiteIntrouvableException;
import fr.lacassinauteur.site.actualite.domain.port.ActualiteRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsulterActualiteUseCase {

    private final ActualiteRepository actualiteRepository;

    public ConsulterActualiteUseCase(ActualiteRepository actualiteRepository) {
        this.actualiteRepository = actualiteRepository;
    }

    public ActualiteResult execute(UUID id) {
        return actualiteRepository.findById(id)
                .map(ActualiteResult::depuis)
                .orElseThrow(() -> new ActualiteIntrouvableException(id));
    }
}
