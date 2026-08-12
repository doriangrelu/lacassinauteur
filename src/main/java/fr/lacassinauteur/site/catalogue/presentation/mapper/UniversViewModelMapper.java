package fr.lacassinauteur.site.catalogue.presentation.mapper;

import fr.lacassinauteur.site.catalogue.application.result.UniversResult;
import fr.lacassinauteur.site.catalogue.presentation.viewmodel.UniversViewModel;
import org.springframework.stereotype.Component;

@Component
public class UniversViewModelMapper {

    public UniversViewModel versViewModel(UniversResult result) {
        return new UniversViewModel(result.id(), result.nom(), result.sousTitre(), result.texte(), result.photoUrl(), result.ordre());
    }
}
