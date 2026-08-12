package fr.lacassinauteur.site.catalogue.presentation.web;

import fr.lacassinauteur.site.catalogue.application.usecase.livre.ConsulterDerniereParutionUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.univers.ListerUniversUseCase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccueilController {

    private final ListerUniversUseCase listerUniversUseCase;
    private final ConsulterDerniereParutionUseCase consulterDerniereParutionUseCase;

    public AccueilController(ListerUniversUseCase listerUniversUseCase, ConsulterDerniereParutionUseCase consulterDerniereParutionUseCase) {
        this.listerUniversUseCase = listerUniversUseCase;
        this.consulterDerniereParutionUseCase = consulterDerniereParutionUseCase;
    }

    @GetMapping("/")
    public String accueil(Model model) {
        model.addAttribute("universList", listerUniversUseCase.execute());
        model.addAttribute("derniereParution", consulterDerniereParutionUseCase.execute().orElse(null));
        model.addAttribute("metaDescription",
                "Thierry Lacassin, auteur de romans noirs et de récits de trajectoires humaines. "
                        + "Découvrez ses univers, ses collections et ses parutions.");
        return "public/accueil";
    }
}
