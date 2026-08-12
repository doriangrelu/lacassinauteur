package fr.lacassinauteur.site.catalogue.application.usecase.collection;

import fr.lacassinauteur.site.catalogue.application.result.CollectionResult;
import fr.lacassinauteur.site.catalogue.domain.exception.CollectionIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.port.CollectionRepository;
import org.springframework.stereotype.Component;

@Component
public class ConsulterCollectionParSlugUseCase {

    private final CollectionRepository collectionRepository;

    public ConsulterCollectionParSlugUseCase(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public CollectionResult execute(String slug) {
        return collectionRepository.findBySlug(slug)
                .map(CollectionResult::depuis)
                .orElseThrow(() -> new CollectionIntrouvableException(slug));
    }
}
