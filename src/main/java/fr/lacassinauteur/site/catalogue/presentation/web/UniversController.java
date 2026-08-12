package fr.lacassinauteur.site.catalogue.presentation.web;

import fr.lacassinauteur.site.catalogue.application.result.CollectionResult;
import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.application.result.UniversResult;
import fr.lacassinauteur.site.catalogue.application.usecase.collection.ListerCollectionsParUniversUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.livre.ListerLivresParCollectionUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.univers.ConsulterUniversParSlugUseCase;
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

    private final ConsulterUniversParSlugUseCase consulterUniversParSlugUseCase;
    private final ListerUniversUseCase listerUniversUseCase;
    private final ListerCollectionsParUniversUseCase listerCollectionsParUniversUseCase;
    private final ListerLivresParCollectionUseCase listerLivresParCollectionUseCase;

    public UniversController(
            ConsulterUniversParSlugUseCase consulterUniversParSlugUseCase,
            ListerUniversUseCase listerUniversUseCase,
            ListerCollectionsParUniversUseCase listerCollectionsParUniversUseCase,
            ListerLivresParCollectionUseCase listerLivresParCollectionUseCase) {
        this.consulterUniversParSlugUseCase = consulterUniversParSlugUseCase;
        this.listerUniversUseCase = listerUniversUseCase;
        this.listerCollectionsParUniversUseCase = listerCollectionsParUniversUseCase;
        this.listerLivresParCollectionUseCase = listerLivresParCollectionUseCase;
    }

    @GetMapping("/univers/{slug}")
    public String afficher(@PathVariable String slug, Model model) {
        UniversResult univers = consulterUniversParSlugUseCase.execute(slug);
        List<CollectionResult> collections = listerCollectionsParUniversUseCase.execute(univers.id());

        Map<UUID, List<LivreResult>> livresParCollection = new LinkedHashMap<>();
        for (CollectionResult collection : collections) {
            livresParCollection.put(collection.id(), listerLivresParCollectionUseCase.execute(collection.id()));
        }

        UniversResult autreUnivers = listerUniversUseCase.execute().stream()
                .filter(u -> !u.id().equals(univers.id()))
                .findFirst()
                .orElse(null);

        model.addAttribute("univers", univers);
        model.addAttribute("collections", collections);
        model.addAttribute("livresParCollection", livresParCollection);
        model.addAttribute("autreUnivers", autreUnivers);
        model.addAttribute("metaDescription", tronquer(univers.texte(), 155));
        return "public/univers";
    }

    private String tronquer(String texte, int longueurMax) {
        if (texte == null) {
            return "";
        }
        String nettoye = texte.strip();
        return nettoye.length() <= longueurMax ? nettoye : nettoye.substring(0, longueurMax - 1).strip() + "…";
    }
}
