package fr.lacassinauteur.site.catalogue.application.usecase.livre;

import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.domain.exception.LivreIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.port.LivreRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsulterLivreUseCase {

    private final LivreRepository livreRepository;

    public ConsulterLivreUseCase(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    public LivreResult execute(UUID livreId) {
        return livreRepository.findById(livreId)
                .map(LivreResult::depuis)
                .orElseThrow(() -> new LivreIntrouvableException(livreId));
    }
}
