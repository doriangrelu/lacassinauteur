package fr.lacassinauteur.site.catalogue.application.usecase.collection;

import fr.lacassinauteur.site.catalogue.application.command.CreerCollectionCommand;
import fr.lacassinauteur.site.catalogue.application.result.CollectionResult;
import fr.lacassinauteur.site.catalogue.domain.model.Collection;
import fr.lacassinauteur.site.catalogue.domain.port.CollectionRepository;
import org.springframework.stereotype.Component;

@Component
public class CreerCollectionUseCase {

    private final CollectionRepository collectionRepository;

    public CreerCollectionUseCase(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public CollectionResult execute(CreerCollectionCommand command) {
        Collection collection = Collection.creer(
                command.universId(), command.nom(), command.sousTitre(), command.texte(), command.ordre());
        return CollectionResult.depuis(collectionRepository.save(collection));
    }
}
