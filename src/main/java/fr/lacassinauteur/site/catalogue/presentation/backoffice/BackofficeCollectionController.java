package fr.lacassinauteur.site.catalogue.presentation.backoffice;

import fr.lacassinauteur.site.catalogue.application.command.CreerCollectionCommand;
import fr.lacassinauteur.site.catalogue.application.command.ModifierCollectionCommand;
import fr.lacassinauteur.site.catalogue.application.result.CollectionResult;
import fr.lacassinauteur.site.catalogue.application.result.UniversResult;
import fr.lacassinauteur.site.catalogue.application.usecase.collection.ConsulterCollectionUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.collection.CreerCollectionUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.collection.ListerToutesLesCollectionsUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.collection.ModifierCollectionUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.collection.ReordonnerCollectionsUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.univers.ListerUniversUseCase;
import fr.lacassinauteur.site.catalogue.presentation.form.CollectionForm;
import fr.lacassinauteur.site.catalogue.presentation.mapper.CollectionViewModelMapper;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/backoffice/collections")
public class BackofficeCollectionController {

    private final ListerToutesLesCollectionsUseCase listerToutesLesCollectionsUseCase;
    private final ConsulterCollectionUseCase consulterCollectionUseCase;
    private final CreerCollectionUseCase creerCollectionUseCase;
    private final ModifierCollectionUseCase modifierCollectionUseCase;
    private final ReordonnerCollectionsUseCase reordonnerCollectionsUseCase;
    private final ListerUniversUseCase listerUniversUseCase;
    private final CollectionViewModelMapper mapper;

    public BackofficeCollectionController(
            ListerToutesLesCollectionsUseCase listerToutesLesCollectionsUseCase,
            ConsulterCollectionUseCase consulterCollectionUseCase,
            CreerCollectionUseCase creerCollectionUseCase,
            ModifierCollectionUseCase modifierCollectionUseCase,
            ReordonnerCollectionsUseCase reordonnerCollectionsUseCase,
            ListerUniversUseCase listerUniversUseCase,
            CollectionViewModelMapper mapper) {
        this.listerToutesLesCollectionsUseCase = listerToutesLesCollectionsUseCase;
        this.consulterCollectionUseCase = consulterCollectionUseCase;
        this.creerCollectionUseCase = creerCollectionUseCase;
        this.modifierCollectionUseCase = modifierCollectionUseCase;
        this.reordonnerCollectionsUseCase = reordonnerCollectionsUseCase;
        this.listerUniversUseCase = listerUniversUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public String liste(Model model) {
        List<UniversResult> universList = listerUniversUseCase.execute();
        Map<UUID, String> nomsUnivers = universList.stream()
                .collect(Collectors.toMap(UniversResult::id, UniversResult::nom));

        model.addAttribute("collections", listerToutesLesCollectionsUseCase.execute().stream()
                .map(result -> mapper.versViewModel(result, nomsUnivers.get(result.universId())))
                .toList());
        model.addAttribute("universList", universList);
        if (!model.containsAttribute("formulaire")) {
            model.addAttribute("formulaire", new CollectionForm());
        }
        return "backoffice/catalogue/collection-liste";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("formulaire") CollectionForm formulaire, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return liste(model);
        }

        int prochainOrdre = listerToutesLesCollectionsUseCase.execute().size() + 1;
        creerCollectionUseCase.execute(new CreerCollectionCommand(
                formulaire.getUniversId(), formulaire.getNom(), formulaire.getSousTitre(), formulaire.getTexte(), prochainOrdre));

        return "redirect:/backoffice/collections";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable UUID id, Model model) {
        if (!model.containsAttribute("formulaire")) {
            CollectionResult collection = consulterCollectionUseCase.execute(id);
            CollectionForm formulaire = new CollectionForm();
            formulaire.setUniversId(collection.universId());
            formulaire.setNom(collection.nom());
            formulaire.setSousTitre(collection.sousTitre());
            formulaire.setTexte(collection.texte());
            model.addAttribute("formulaire", formulaire);
        }
        model.addAttribute("collectionId", id);
        model.addAttribute("universList", listerUniversUseCase.execute());
        return "backoffice/catalogue/collection-modifier";
    }

    @PostMapping("/{id}")
    public String modifier(@PathVariable UUID id, @Valid @ModelAttribute("formulaire") CollectionForm formulaire,
                            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return formulaireModification(id, model);
        }

        int ordreActuel = consulterCollectionUseCase.execute(id).ordre();
        modifierCollectionUseCase.execute(new ModifierCollectionCommand(
                id, formulaire.getUniversId(), formulaire.getNom(), formulaire.getSousTitre(), formulaire.getTexte(), ordreActuel));

        return "redirect:/backoffice/collections";
    }

    @PostMapping("/reordonner")
    public String reordonner(@RequestParam("id") List<UUID> idsOrdonnes) {
        reordonnerCollectionsUseCase.execute(idsOrdonnes);
        return "redirect:/backoffice/collections";
    }
}
