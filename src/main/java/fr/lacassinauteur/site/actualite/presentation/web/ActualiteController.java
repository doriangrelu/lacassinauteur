package fr.lacassinauteur.site.actualite.presentation.web;

import fr.lacassinauteur.site.actualite.application.usecase.ListerActualitesPasseesMisesEnAvantUseCase;
import fr.lacassinauteur.site.actualite.application.usecase.ListerEvenementsAVenirUseCase;
import fr.lacassinauteur.site.actualite.presentation.mapper.ActualiteViewModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ActualiteController {

    private final ListerEvenementsAVenirUseCase listerEvenementsAVenirUseCase;
    private final ListerActualitesPasseesMisesEnAvantUseCase listerActualitesPasseesMisesEnAvantUseCase;
    private final ActualiteViewModelMapper mapper;

    public ActualiteController(
            ListerEvenementsAVenirUseCase listerEvenementsAVenirUseCase,
            ListerActualitesPasseesMisesEnAvantUseCase listerActualitesPasseesMisesEnAvantUseCase,
            ActualiteViewModelMapper mapper) {
        this.listerEvenementsAVenirUseCase = listerEvenementsAVenirUseCase;
        this.listerActualitesPasseesMisesEnAvantUseCase = listerActualitesPasseesMisesEnAvantUseCase;
        this.mapper = mapper;
    }

    @GetMapping("/actualites")
    public String actualites(Model model) {
        model.addAttribute("evenementsAVenir", listerEvenementsAVenirUseCase.execute().stream().map(mapper::versViewModel).toList());
        model.addAttribute("actualitesPassees", listerActualitesPasseesMisesEnAvantUseCase.execute().stream().map(mapper::versViewModel).toList());
        model.addAttribute("metaDescription",
                "Salons, dédicaces et actualités de Thierry Lacassin, auteur de romans noirs et de récits de trajectoires humaines.");
        return "public/actualites";
    }
}
