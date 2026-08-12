package fr.lacassinauteur.site.catalogue.application.usecase.livre;

import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.domain.port.LivreRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ListerLivresParCollectionUseCase {

    private final LivreRepository livreRepository;

    public ListerLivresParCollectionUseCase(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    public List<LivreResult> execute(UUID collectionId) {
        return livreRepository.findByCollectionIdOrderByOrdre(collectionId).stream()
                .map(LivreResult::depuis)
                .toList();
    }
}
