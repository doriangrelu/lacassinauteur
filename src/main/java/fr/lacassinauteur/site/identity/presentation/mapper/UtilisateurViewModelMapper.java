package fr.lacassinauteur.site.identity.presentation.mapper;

import fr.lacassinauteur.site.identity.application.result.UtilisateurResult;
import fr.lacassinauteur.site.identity.presentation.viewmodel.UtilisateurViewModel;
import org.springframework.stereotype.Component;

@Component
public class UtilisateurViewModelMapper {

    public UtilisateurViewModel versViewModel(UtilisateurResult result) {
        return new UtilisateurViewModel(result.id(), result.email(), result.role().name(), result.actif());
    }
}
