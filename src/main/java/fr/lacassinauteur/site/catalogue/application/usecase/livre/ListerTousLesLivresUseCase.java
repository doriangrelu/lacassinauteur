package fr.lacassinauteur.site.catalogue.application.usecase.livre;

import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.domain.port.LivreRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListerTousLesLivresUseCase {

    private final LivreRepository livreRepository;

    public ListerTousLesLivresUseCase(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    public List<LivreResult> execute() {
        return livreRepository.findAllOrderByOrdre().stream()
                .map(LivreResult::depuis)
                .toList();
    }
}
