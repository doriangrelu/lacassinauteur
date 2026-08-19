package fr.lacassinauteur.site.legal.presentation.web;

import fr.lacassinauteur.site.legal.application.result.InformationsLegalesResult;
import fr.lacassinauteur.site.legal.application.usecase.ConsulterInformationsLegalesUseCase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Pages légales publiques. Indexables : elles doivent être trouvables, y compris
 * par un visiteur qui cherche comment exercer ses droits.
 */
@Controller
public class PagesLegalesController {

    private final ConsulterInformationsLegalesUseCase consulterInformationsLegalesUseCase;

    public PagesLegalesController(ConsulterInformationsLegalesUseCase consulterInformationsLegalesUseCase) {
        this.consulterInformationsLegalesUseCase = consulterInformationsLegalesUseCase;
    }

    @GetMapping("/mentions-legales")
    public String mentionsLegales(Model model) {
        model.addAttribute("informations", informations());
        model.addAttribute("metaDescription",
                "Mentions légales du site de Thierry Lacassin : éditeur, directeur de publication, hébergeur.");
        return "public/mentions-legales";
    }

    @GetMapping("/confidentialite")
    public String confidentialite(Model model) {
        model.addAttribute("informations", informations());
        model.addAttribute("metaDescription",
                "Politique de confidentialité du site de Thierry Lacassin : données collectées, "
                        + "finalités, durées de conservation et exercice de vos droits.");
        return "public/confidentialite";
    }

    private InformationsLegalesResult informations() {
        return consulterInformationsLegalesUseCase.execute();
    }
}
