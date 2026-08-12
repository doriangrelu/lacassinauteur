package fr.lacassinauteur.site.newsletter.presentation.mapper;

import fr.lacassinauteur.site.newsletter.application.result.AbonneNewsletterResult;
import fr.lacassinauteur.site.newsletter.presentation.viewmodel.AbonneNewsletterViewModel;
import org.springframework.stereotype.Component;

@Component
public class AbonneNewsletterViewModelMapper {

    public AbonneNewsletterViewModel versViewModel(AbonneNewsletterResult result) {
        return new AbonneNewsletterViewModel(
                result.id(), result.prenom(), result.email(), result.statut(), result.dateInscription());
    }
}
