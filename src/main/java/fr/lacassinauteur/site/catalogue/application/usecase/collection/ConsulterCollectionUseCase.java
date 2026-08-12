package fr.lacassinauteur.site.catalogue.application.usecase.collection;

import fr.lacassinauteur.site.catalogue.application.result.CollectionResult;
import fr.lacassinauteur.site.catalogue.domain.exception.CollectionIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.port.CollectionRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsulterCollectionUseCase {

    private final CollectionRepository collectionRepository;

    public ConsulterCollectionUseCase(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public CollectionResult execute(UUID collectionId) {
        return collectionRepository.findById(collectionId)
                .map(CollectionResult::depuis)
                .orElseThrow(() -> new CollectionIntrouvableException(collectionId));
    }
}
