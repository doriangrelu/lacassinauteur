package fr.lacassinauteur.site.catalogue.presentation.backoffice;

import fr.lacassinauteur.site.catalogue.application.command.CreerLivreCommand;
import fr.lacassinauteur.site.catalogue.application.command.DefinirDerniereParutionCommand;
import fr.lacassinauteur.site.catalogue.application.command.ModifierLivreCommand;
import fr.lacassinauteur.site.catalogue.application.command.PublierLivreCommand;
import fr.lacassinauteur.site.catalogue.application.result.CollectionResult;
import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.application.usecase.collection.ListerToutesLesCollectionsUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.livre.ConsulterLivreUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.livre.CreerLivreUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.livre.DefinirDerniereParutionUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.livre.ListerTousLesLivresUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.livre.ModifierLivreUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.livre.PublierLivreUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.livre.RetirerLienAchatUseCase;
import fr.lacassinauteur.site.catalogue.presentation.form.LivreForm;
import fr.lacassinauteur.site.catalogue.presentation.form.PublierLivreForm;
import fr.lacassinauteur.site.catalogue.presentation.mapper.LivreViewModelMapper;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/backoffice/livres")
public class BackofficeLivreController {

    private final ListerTousLesLivresUseCase listerTousLesLivresUseCase;
    private final ConsulterLivreUseCase consulterLivreUseCase;
    private final CreerLivreUseCase creerLivreUseCase;
    private final ModifierLivreUseCase modifierLivreUseCase;
    private final PublierLivreUseCase publierLivreUseCase;
    private final RetirerLienAchatUseCase retirerLienAchatUseCase;
    private final DefinirDerniereParutionUseCase definirDerniereParutionUseCase;
    private final ListerToutesLesCollectionsUseCase listerToutesLesCollectionsUseCase;
    private final LivreViewModelMapper mapper;

    public BackofficeLivreController(
            ListerTousLesLivresUseCase listerTousLesLivresUseCase,
            ConsulterLivreUseCase consulterLivreUseCase,
            CreerLivreUseCase creerLivreUseCase,
            ModifierLivreUseCase modifierLivreUseCase,
            PublierLivreUseCase publierLivreUseCase,
            RetirerLienAchatUseCase retirerLienAchatUseCase,
            DefinirDerniereParutionUseCase definirDerniereParutionUseCase,
            ListerToutesLesCollectionsUseCase listerToutesLesCollectionsUseCase,
            LivreViewModelMapper mapper) {
        this.listerTousLesLivresUseCase = listerTousLesLivresUseCase;
        this.consulterLivreUseCase = consulterLivreUseCase;
        this.creerLivreUseCase = creerLivreUseCase;
        this.modifierLivreUseCase = modifierLivreUseCase;
        this.publierLivreUseCase = publierLivreUseCase;
        this.retirerLienAchatUseCase = retirerLienAchatUseCase;
        this.definirDerniereParutionUseCase = definirDerniereParutionUseCase;
        this.listerToutesLesCollectionsUseCase = listerToutesLesCollectionsUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public String liste(Model model) {
        List<CollectionResult> collections = listerToutesLesCollectionsUseCase.execute();
        Map<UUID, String> nomsCollections = collections.stream()
                .collect(Collectors.toMap(CollectionResult::id, CollectionResult::nom));

        model.addAttribute("livres", listerTousLesLivresUseCase.execute().stream()
                .map(result -> mapper.versViewModel(result, nomsCollections.get(result.collectionId())))
                .toList());
        model.addAttribute("collections", collections);
        if (!model.containsAttribute("formulaire")) {
            model.addAttribute("formulaire", new LivreForm());
        }
        if (!model.containsAttribute("formulairePublication")) {
            model.addAttribute("formulairePublication", new PublierLivreForm());
        }
        return "backoffice/catalogue/livre-liste";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("formulaire") LivreForm formulaire, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return liste(model);
        }

        creerLivreUseCase.execute(new CreerLivreCommand(
                formulaire.getCollectionId(), formulaire.getTitre(), formulaire.getSousTitre(),
                formulaire.getCouvertureUrl(), formulaire.getPitchCourt(), formulaire.getResume(), formulaire.getOrdre()));

        return "redirect:/backoffice/livres";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable UUID id, Model model) {
        LivreResult livre = consulterLivreUseCase.execute(id);

        if (!model.containsAttribute("formulaire")) {
            LivreForm formulaire = new LivreForm();
            formulaire.setCollectionId(livre.collectionId());
            formulaire.setTitre(livre.titre());
            formulaire.setSousTitre(livre.sousTitre());
            formulaire.setCouvertureUrl(livre.couvertureUrl());
            formulaire.setPitchCourt(livre.pitchCourt());
            formulaire.setResume(livre.resume());
            formulaire.setOrdre(livre.ordre());
            model.addAttribute("formulaire", formulaire);
        }
        if (!model.containsAttribute("formulairePublication")) {
            model.addAttribute("formulairePublication", new PublierLivreForm());
        }
        model.addAttribute("livre", livre);
        model.addAttribute("livreId", id);
        model.addAttribute("collections", listerToutesLesCollectionsUseCase.execute());
        return "backoffice/catalogue/livre-modifier";
    }

    @PostMapping("/{id}")
    public String modifier(@PathVariable UUID id, @Valid @ModelAttribute("formulaire") LivreForm formulaire,
                            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return formulaireModification(id, model);
        }

        modifierLivreUseCase.execute(new ModifierLivreCommand(
                id, formulaire.getCollectionId(), formulaire.getTitre(), formulaire.getSousTitre(),
                formulaire.getCouvertureUrl(), formulaire.getPitchCourt(), formulaire.getResume(), formulaire.getOrdre()));

        return "redirect:/backoffice/livres";
    }

    @PostMapping("/{id}/publier")
    public String publier(@PathVariable UUID id, @ModelAttribute("formulairePublication") PublierLivreForm formulaire) {
        publierLivreUseCase.execute(new PublierLivreCommand(id, formulaire.getUrl(), formulaire.getLibelleMarchand()));
        return "redirect:/backoffice/livres";
    }

    @PostMapping("/{id}/retirer-lien")
    public String retirerLien(@PathVariable UUID id) {
        retirerLienAchatUseCase.execute(id);
        return "redirect:/backoffice/livres";
    }

    @PostMapping("/{id}/derniere-parution")
    public String definirDerniereParution(@PathVariable UUID id) {
        definirDerniereParutionUseCase.execute(new DefinirDerniereParutionCommand(id));
        return "redirect:/backoffice/livres";
    }
}
