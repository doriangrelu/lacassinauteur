package fr.lacassinauteur.site.catalogue.application.usecase.collection;

import fr.lacassinauteur.site.catalogue.domain.exception.CollectionIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.model.Collection;
import fr.lacassinauteur.site.catalogue.domain.port.CollectionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ReordonnerCollectionsUseCase {

    private final CollectionRepository collectionRepository;

    public ReordonnerCollectionsUseCase(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public void execute(List<UUID> idsOrdonnes) {
        for (int index = 0; index < idsOrdonnes.size(); index++) {
            UUID id = idsOrdonnes.get(index);
            Collection collection = collectionRepository.findById(id).orElseThrow(() -> new CollectionIntrouvableException(id));
            collection.changerOrdre(index + 1);
            collectionRepository.save(collection);
        }
    }
}
