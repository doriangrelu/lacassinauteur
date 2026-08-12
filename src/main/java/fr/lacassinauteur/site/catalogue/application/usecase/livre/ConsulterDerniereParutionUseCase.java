package fr.lacassinauteur.site.catalogue.application.usecase.livre;

import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.domain.port.LivreRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ConsulterDerniereParutionUseCase {

    private final LivreRepository livreRepository;

    public ConsulterDerniereParutionUseCase(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    public Optional<LivreResult> execute() {
        return livreRepository.findDerniereParution().map(LivreResult::depuis);
    }
}
