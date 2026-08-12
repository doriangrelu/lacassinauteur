package fr.lacassinauteur.site.catalogue.presentation.web;

import fr.lacassinauteur.site.catalogue.application.result.CollectionResult;
import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.application.result.UniversResult;
import fr.lacassinauteur.site.catalogue.application.usecase.collection.ListerCollectionsParUniversUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.livre.ListerLivresParCollectionUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.univers.ConsulterUniversUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.univers.ListerUniversUseCase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class UniversController {

    private final ConsulterUniversUseCase consulterUniversUseCase;
    private final ListerUniversUseCase listerUniversUseCase;
    private final ListerCollectionsParUniversUseCase listerCollectionsParUniversUseCase;
    private final ListerLivresParCollectionUseCase listerLivresParCollectionUseCase;

    public UniversController(
            ConsulterUniversUseCase consulterUniversUseCase,
            ListerUniversUseCase listerUniversUseCase,
            ListerCollectionsParUniversUseCase listerCollectionsParUniversUseCase,
            ListerLivresParCollectionUseCase listerLivresParCollectionUseCase) {
        this.consulterUniversUseCase = consulterUniversUseCase;
        this.listerUniversUseCase = listerUniversUseCase;
        this.listerCollectionsParUniversUseCase = listerCollectionsParUniversUseCase;
        this.listerLivresParCollectionUseCase = listerLivresParCollectionUseCase;
    }

    @GetMapping("/univers/{id}")
    public String afficher(@PathVariable UUID id, Model model) {
        UniversResult univers = consulterUniversUseCase.execute(id);
        List<CollectionResult> collections = listerCollectionsParUniversUseCase.execute(id);

        Map<UUID, List<LivreResult>> livresParCollection = new LinkedHashMap<>();
        for (CollectionResult collection : collections) {
            livresParCollection.put(collection.id(), listerLivresParCollectionUseCase.execute(collection.id()));
        }

        UniversResult autreUnivers = listerUniversUseCase.execute().stream()
                .filter(u -> !u.id().equals(id))
                .findFirst()
                .orElse(null);

        model.addAttribute("univers", univers);
        model.addAttribute("collections", collections);
        model.addAttribute("livresParCollection", livresParCollection);
        model.addAttribute("autreUnivers", autreUnivers);
        return "public/univers";
    }
}
