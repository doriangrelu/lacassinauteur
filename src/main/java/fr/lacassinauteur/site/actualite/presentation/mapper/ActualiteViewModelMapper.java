package fr.lacassinauteur.site.actualite.presentation.mapper;

import fr.lacassinauteur.site.actualite.application.result.ActualiteResult;
import fr.lacassinauteur.site.actualite.presentation.viewmodel.ActualiteViewModel;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class ActualiteViewModelMapper {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);

    public ActualiteViewModel versViewModel(ActualiteResult result) {
        return new ActualiteViewModel(
                result.id(), result.titre(), result.texte(), result.date(), FORMAT_DATE.format(result.date()),
                result.lieu(), result.lienBilletterie(), result.imageUrl(), result.archiveeManuellement(),
                result.misEnAvant(), result.type());
    }
}
