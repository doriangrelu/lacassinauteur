package fr.lacassinauteur.site.catalogue.presentation.web;

import fr.lacassinauteur.site.catalogue.application.result.CollectionResult;
import fr.lacassinauteur.site.catalogue.application.result.UniversResult;
import fr.lacassinauteur.site.catalogue.application.usecase.collection.ConsulterCollectionUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.collection.ListerCollectionsParUniversUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.livre.ListerLivresParCollectionUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.univers.ConsulterUniversUseCase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Controller
public class CollectionController {

    private final ConsulterCollectionUseCase consulterCollectionUseCase;
    private final ConsulterUniversUseCase consulterUniversUseCase;
    private final ListerCollectionsParUniversUseCase listerCollectionsParUniversUseCase;
    private final ListerLivresParCollectionUseCase listerLivresParCollectionUseCase;

    public CollectionController(
            ConsulterCollectionUseCase consulterCollectionUseCase,
            ConsulterUniversUseCase consulterUniversUseCase,
            ListerCollectionsParUniversUseCase listerCollectionsParUniversUseCase,
            ListerLivresParCollectionUseCase listerLivresParCollectionUseCase) {
        this.consulterCollectionUseCase = consulterCollectionUseCase;
        this.consulterUniversUseCase = consulterUniversUseCase;
        this.listerCollectionsParUniversUseCase = listerCollectionsParUniversUseCase;
        this.listerLivresParCollectionUseCase = listerLivresParCollectionUseCase;
    }

    @GetMapping("/collections/{id}")
    public String afficher(@PathVariable UUID id, Model model) {
        CollectionResult collection = consulterCollectionUseCase.execute(id);
        UniversResult univers = consulterUniversUseCase.execute(collection.universId());

        CollectionResult autreCollection = listerCollectionsParUniversUseCase.execute(collection.universId()).stream()
                .filter(c -> !c.id().equals(id))
                .findFirst()
                .orElse(null);

        model.addAttribute("collection", collection);
        model.addAttribute("univers", univers);
        model.addAttribute("autreCollection", autreCollection);
        model.addAttribute("livres", listerLivresParCollectionUseCase.execute(id));
        return "public/collection";
    }
}
