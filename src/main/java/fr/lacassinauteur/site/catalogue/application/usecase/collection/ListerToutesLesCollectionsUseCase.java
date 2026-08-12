package fr.lacassinauteur.site.catalogue.application.usecase.collection;

import fr.lacassinauteur.site.catalogue.application.result.CollectionResult;
import fr.lacassinauteur.site.catalogue.domain.port.CollectionRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListerToutesLesCollectionsUseCase {

    private final CollectionRepository collectionRepository;

    public ListerToutesLesCollectionsUseCase(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public List<CollectionResult> execute() {
        return collectionRepository.findAllOrderByOrdre().stream()
                .map(CollectionResult::depuis)
                .toList();
    }
}
