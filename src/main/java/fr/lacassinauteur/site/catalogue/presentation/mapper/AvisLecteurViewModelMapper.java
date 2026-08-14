package fr.lacassinauteur.site.catalogue.presentation.mapper;

import fr.lacassinauteur.site.catalogue.application.result.AvisLecteurResult;
import fr.lacassinauteur.site.catalogue.presentation.viewmodel.AvisLecteurViewModel;
import org.springframework.stereotype.Component;

@Component
public class AvisLecteurViewModelMapper {

    public AvisLecteurViewModel versViewModel(AvisLecteurResult result, String livreTitre) {
        return new AvisLecteurViewModel(
                result.id(), result.livreId(), livreTitre, result.nomAuteurAvis(), result.texte(), result.note(),
                result.statut(), result.dateSoumission());
    }
}
