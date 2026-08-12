package fr.lacassinauteur.site.catalogue.application.usecase.livre;

import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.domain.exception.LivreIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.port.LivreRepository;
import org.springframework.stereotype.Component;

@Component
public class ConsulterLivreParSlugUseCase {

    private final LivreRepository livreRepository;

    public ConsulterLivreParSlugUseCase(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    public LivreResult execute(String slug) {
        return livreRepository.findBySlug(slug)
                .map(LivreResult::depuis)
                .orElseThrow(() -> new LivreIntrouvableException(slug));
    }
}
