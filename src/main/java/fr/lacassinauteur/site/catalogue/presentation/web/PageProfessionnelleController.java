package fr.lacassinauteur.site.catalogue.presentation.web;

import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.application.usecase.livre.ConsulterLivreParSlugUseCase;
import fr.lacassinauteur.site.catalogue.domain.exception.LivreIntrouvableException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Page technique à destination des professionnels du livre (libraires, presse) :
 * volontairement non référencée dans la navigation publique, accessible uniquement
 * par URL directe (ex. QR code sur un support presse). N'existe que pour un livre
 * publié dont la fiche professionnelle a été renseignée — sinon 404, comme les
 * autres pages catalogue introuvables.
 */
@Controller
public class PageProfessionnelleController {

    private final ConsulterLivreParSlugUseCase consulterLivreParSlugUseCase;

    public PageProfessionnelleController(ConsulterLivreParSlugUseCase consulterLivreParSlugUseCase) {
        this.consulterLivreParSlugUseCase = consulterLivreParSlugUseCase;
    }

    @GetMapping("/livres/{slug}/pro")
    public String afficher(@PathVariable String slug, Model model) {
        LivreResult livre = consulterLivreParSlugUseCase.execute(slug);
        if (!livre.disponible() || !livre.ficheProfessionnelleRenseignee()) {
            throw new LivreIntrouvableException(slug);
        }

        model.addAttribute("livre", livre);
        return "public/page-pro";
    }
}
