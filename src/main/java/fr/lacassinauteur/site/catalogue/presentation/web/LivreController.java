package fr.lacassinauteur.site.catalogue.presentation.web;

import fr.lacassinauteur.site.catalogue.application.result.CollectionResult;
import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.application.result.UniversResult;
import fr.lacassinauteur.site.catalogue.application.usecase.collection.ConsulterCollectionUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.livre.ConsulterLivreUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.univers.ConsulterUniversUseCase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Controller
public class LivreController {

    private final ConsulterLivreUseCase consulterLivreUseCase;
    private final ConsulterCollectionUseCase consulterCollectionUseCase;
    private final ConsulterUniversUseCase consulterUniversUseCase;

    public LivreController(
            ConsulterLivreUseCase consulterLivreUseCase,
            ConsulterCollectionUseCase consulterCollectionUseCase,
            ConsulterUniversUseCase consulterUniversUseCase) {
        this.consulterLivreUseCase = consulterLivreUseCase;
        this.consulterCollectionUseCase = consulterCollectionUseCase;
        this.consulterUniversUseCase = consulterUniversUseCase;
    }

    @GetMapping("/livres/{id}")
    public String afficher(@PathVariable UUID id, Model model) {
        LivreResult livre = consulterLivreUseCase.execute(id);
        CollectionResult collection = consulterCollectionUseCase.execute(livre.collectionId());
        UniversResult univers = consulterUniversUseCase.execute(collection.universId());

        model.addAttribute("livre", livre);
        model.addAttribute("collection", collection);
        model.addAttribute("univers", univers);
        return "public/livre";
    }
}
