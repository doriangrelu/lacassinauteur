package fr.lacassinauteur.site.catalogue.presentation.web;

import fr.lacassinauteur.site.catalogue.application.result.CollectionResult;
import fr.lacassinauteur.site.catalogue.application.result.UniversResult;
import fr.lacassinauteur.site.catalogue.application.usecase.collection.ConsulterCollectionParSlugUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.collection.ListerCollectionsParUniversUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.livre.ListerLivresParCollectionUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.univers.ConsulterUniversUseCase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CollectionController {

    private final ConsulterCollectionParSlugUseCase consulterCollectionParSlugUseCase;
    private final ConsulterUniversUseCase consulterUniversUseCase;
    private final ListerCollectionsParUniversUseCase listerCollectionsParUniversUseCase;
    private final ListerLivresParCollectionUseCase listerLivresParCollectionUseCase;

    public CollectionController(
            ConsulterCollectionParSlugUseCase consulterCollectionParSlugUseCase,
            ConsulterUniversUseCase consulterUniversUseCase,
            ListerCollectionsParUniversUseCase listerCollectionsParUniversUseCase,
            ListerLivresParCollectionUseCase listerLivresParCollectionUseCase) {
        this.consulterCollectionParSlugUseCase = consulterCollectionParSlugUseCase;
        this.consulterUniversUseCase = consulterUniversUseCase;
        this.listerCollectionsParUniversUseCase = listerCollectionsParUniversUseCase;
        this.listerLivresParCollectionUseCase = listerLivresParCollectionUseCase;
    }

    @GetMapping("/collections/{slug}")
    public String afficher(@PathVariable String slug, Model model) {
        CollectionResult collection = consulterCollectionParSlugUseCase.execute(slug);
        UniversResult univers = consulterUniversUseCase.execute(collection.universId());

        CollectionResult autreCollection = listerCollectionsParUniversUseCase.execute(collection.universId()).stream()
                .filter(c -> !c.id().equals(collection.id()))
                .findFirst()
                .orElse(null);

        model.addAttribute("collection", collection);
        model.addAttribute("univers", univers);
        model.addAttribute("autreCollection", autreCollection);
        model.addAttribute("livres", listerLivresParCollectionUseCase.execute(collection.id()));
        model.addAttribute("metaDescription", tronquer(collection.texte(), 155));
        return "public/collection";
    }

    private String tronquer(String texte, int longueurMax) {
        if (texte == null) {
            return "";
        }
        String nettoye = texte.strip();
        return nettoye.length() <= longueurMax ? nettoye : nettoye.substring(0, longueurMax - 1).strip() + "…";
    }
}
