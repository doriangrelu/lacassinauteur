package fr.lacassinauteur.site.catalogue.application.usecase.collection;

import fr.lacassinauteur.site.catalogue.application.result.CollectionResult;
import fr.lacassinauteur.site.catalogue.domain.port.CollectionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ListerCollectionsParUniversUseCase {

    private final CollectionRepository collectionRepository;

    public ListerCollectionsParUniversUseCase(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public List<CollectionResult> execute(UUID universId) {
        return collectionRepository.findByUniversIdOrderByOrdre(universId).stream()
                .map(CollectionResult::depuis)
                .toList();
    }
}
