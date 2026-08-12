package fr.lacassinauteur.site.catalogue.presentation.mapper;

import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.presentation.viewmodel.LivreViewModel;
import org.springframework.stereotype.Component;

@Component
public class LivreViewModelMapper {

    public LivreViewModel versViewModel(LivreResult result, String collectionNom) {
        return new LivreViewModel(
                result.id(), result.collectionId(), collectionNom, result.titre(), result.sousTitre(),
                result.couvertureUrl(), result.pitchCourt(), result.resume(), result.lienAchatUrl(),
                result.lienAchatLibelle(), result.disponible(), result.derniereParution(), result.ordre());
    }
}
