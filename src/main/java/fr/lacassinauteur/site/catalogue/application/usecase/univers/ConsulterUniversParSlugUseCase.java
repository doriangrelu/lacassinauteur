package fr.lacassinauteur.site.catalogue.application.usecase.univers;

import fr.lacassinauteur.site.catalogue.application.result.UniversResult;
import fr.lacassinauteur.site.catalogue.domain.exception.UniversIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.port.UniversRepository;
import org.springframework.stereotype.Component;

@Component
public class ConsulterUniversParSlugUseCase {

    private final UniversRepository universRepository;

    public ConsulterUniversParSlugUseCase(UniversRepository universRepository) {
        this.universRepository = universRepository;
    }

    public UniversResult execute(String slug) {
        return universRepository.findBySlug(slug)
                .map(UniversResult::depuis)
                .orElseThrow(() -> new UniversIntrouvableException(slug));
    }
}
