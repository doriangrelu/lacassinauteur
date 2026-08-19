package fr.lacassinauteur.site.biographie.presentation.web;

import fr.lacassinauteur.site.biographie.application.result.BiographieResult;
import fr.lacassinauteur.site.biographie.application.usecase.ConsulterBiographieUseCase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Page publique de présentation de l'auteur. Contrairement à la page
 * professionnelle des livres, celle-ci est pleinement indexable : c'est une porte
 * d'entrée du site.
 */
@Controller
public class BiographieController {

    private static final String META_DESCRIPTION =
            "Thierry Lacassin, auteur de romans noirs, d'enquêtes de territoire et de récits de "
                    + "trajectoires humaines. Découvrez son parcours et sa façon d'écrire.";

    private final ConsulterBiographieUseCase consulterBiographieUseCase;

    public BiographieController(ConsulterBiographieUseCase consulterBiographieUseCase) {
        this.consulterBiographieUseCase = consulterBiographieUseCase;
    }

    @GetMapping("/auteur")
    public String afficher(Model model) {
        BiographieResult biographie = consulterBiographieUseCase.execute();

        model.addAttribute("biographie", biographie);
        model.addAttribute("metaDescription", META_DESCRIPTION);
        return "public/auteur";
    }
}
