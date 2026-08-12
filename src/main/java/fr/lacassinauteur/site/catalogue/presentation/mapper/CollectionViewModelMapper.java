package fr.lacassinauteur.site.catalogue.presentation.mapper;

import fr.lacassinauteur.site.catalogue.application.result.CollectionResult;
import fr.lacassinauteur.site.catalogue.presentation.viewmodel.CollectionViewModel;
import org.springframework.stereotype.Component;

@Component
public class CollectionViewModelMapper {

    public CollectionViewModel versViewModel(CollectionResult result, String universNom) {
        return new CollectionViewModel(
                result.id(), result.universId(), universNom, result.nom(), result.sousTitre(), result.texte(), result.ordre());
    }
}
