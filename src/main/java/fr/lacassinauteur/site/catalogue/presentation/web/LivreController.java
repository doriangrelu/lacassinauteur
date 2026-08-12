package fr.lacassinauteur.site.catalogue.presentation.web;

import fr.lacassinauteur.site.catalogue.application.result.CollectionResult;
import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.application.result.UniversResult;
import fr.lacassinauteur.site.catalogue.application.usecase.collection.ConsulterCollectionUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.livre.ConsulterLivreParSlugUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.univers.ConsulterUniversUseCase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class LivreController {

    private final ConsulterLivreParSlugUseCase consulterLivreParSlugUseCase;
    private final ConsulterCollectionUseCase consulterCollectionUseCase;
    private final ConsulterUniversUseCase consulterUniversUseCase;

    public LivreController(
            ConsulterLivreParSlugUseCase consulterLivreParSlugUseCase,
            ConsulterCollectionUseCase consulterCollectionUseCase,
            ConsulterUniversUseCase consulterUniversUseCase) {
        this.consulterLivreParSlugUseCase = consulterLivreParSlugUseCase;
        this.consulterCollectionUseCase = consulterCollectionUseCase;
        this.consulterUniversUseCase = consulterUniversUseCase;
    }

    @GetMapping("/livres/{slug}")
    public String afficher(@PathVariable String slug, Model model) {
        LivreResult livre = consulterLivreParSlugUseCase.execute(slug);
        CollectionResult collection = consulterCollectionUseCase.execute(livre.collectionId());
        UniversResult univers = consulterUniversUseCase.execute(collection.universId());

        model.addAttribute("livre", livre);
        model.addAttribute("collection", collection);
        model.addAttribute("univers", univers);
        model.addAttribute("metaDescription", tronquer(
                livre.pitchCourt() != null ? livre.pitchCourt() : livre.resume(), 155));
        return "public/livre";
    }

    private String tronquer(String texte, int longueurMax) {
        if (texte == null) {
            return "";
        }
        String nettoye = texte.strip();
        return nettoye.length() <= longueurMax ? nettoye : nettoye.substring(0, longueurMax - 1).strip() + "…";
    }
}
