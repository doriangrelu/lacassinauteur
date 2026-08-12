package fr.lacassinauteur.site.catalogue.application.usecase.collection;

import fr.lacassinauteur.site.catalogue.application.command.ModifierCollectionCommand;
import fr.lacassinauteur.site.catalogue.application.result.CollectionResult;
import fr.lacassinauteur.site.catalogue.domain.exception.CollectionIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.model.Collection;
import fr.lacassinauteur.site.catalogue.domain.port.CollectionRepository;
import org.springframework.stereotype.Component;

@Component
public class ModifierCollectionUseCase {

    private final CollectionRepository collectionRepository;

    public ModifierCollectionUseCase(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public CollectionResult execute(ModifierCollectionCommand command) {
        Collection collection = collectionRepository.findById(command.collectionId())
                .orElseThrow(() -> new CollectionIntrouvableException(command.collectionId()));

        collection.modifier(command.universId(), command.nom(), command.sousTitre(), command.texte(), command.ordre());

        return CollectionResult.depuis(collectionRepository.save(collection));
    }
}
