package fr.lacassinauteur.site.catalogue.presentation.backoffice;

import fr.lacassinauteur.site.catalogue.application.command.CreerUniversCommand;
import fr.lacassinauteur.site.catalogue.application.command.ModifierUniversCommand;
import fr.lacassinauteur.site.catalogue.application.result.UniversResult;
import fr.lacassinauteur.site.catalogue.application.usecase.univers.ConsulterUniversUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.univers.CreerUniversUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.univers.ListerUniversUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.univers.ModifierUniversUseCase;
import fr.lacassinauteur.site.catalogue.presentation.form.UniversForm;
import fr.lacassinauteur.site.catalogue.presentation.mapper.UniversViewModelMapper;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/backoffice/univers")
public class BackofficeUniversController {

    private final ListerUniversUseCase listerUniversUseCase;
    private final ConsulterUniversUseCase consulterUniversUseCase;
    private final CreerUniversUseCase creerUniversUseCase;
    private final ModifierUniversUseCase modifierUniversUseCase;
    private final UniversViewModelMapper mapper;

    public BackofficeUniversController(
            ListerUniversUseCase listerUniversUseCase,
            ConsulterUniversUseCase consulterUniversUseCase,
            CreerUniversUseCase creerUniversUseCase,
            ModifierUniversUseCase modifierUniversUseCase,
            UniversViewModelMapper mapper) {
        this.listerUniversUseCase = listerUniversUseCase;
        this.consulterUniversUseCase = consulterUniversUseCase;
        this.creerUniversUseCase = creerUniversUseCase;
        this.modifierUniversUseCase = modifierUniversUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("universList", listerUniversUseCase.execute().stream().map(mapper::versViewModel).toList());
        if (!model.containsAttribute("formulaire")) {
            model.addAttribute("formulaire", new UniversForm());
        }
        return "backoffice/catalogue/univers-liste";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("formulaire") UniversForm formulaire, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return liste(model);
        }

        creerUniversUseCase.execute(new CreerUniversCommand(
                formulaire.getNom(), formulaire.getSousTitre(), formulaire.getTexte(), formulaire.getPhotoUrl(), formulaire.getOrdre()));

        return "redirect:/backoffice/univers";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable UUID id, Model model) {
        if (!model.containsAttribute("formulaire")) {
            UniversResult univers = consulterUniversUseCase.execute(id);
            UniversForm formulaire = new UniversForm();
            formulaire.setNom(univers.nom());
            formulaire.setSousTitre(univers.sousTitre());
            formulaire.setTexte(univers.texte());
            formulaire.setPhotoUrl(univers.photoUrl());
            formulaire.setOrdre(univers.ordre());
            model.addAttribute("formulaire", formulaire);
        }
        model.addAttribute("universId", id);
        return "backoffice/catalogue/univers-modifier";
    }

    @PostMapping("/{id}")
    public String modifier(@PathVariable UUID id, @Valid @ModelAttribute("formulaire") UniversForm formulaire,
                            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return formulaireModification(id, model);
        }

        modifierUniversUseCase.execute(new ModifierUniversCommand(
                id, formulaire.getNom(), formulaire.getSousTitre(), formulaire.getTexte(), formulaire.getPhotoUrl(), formulaire.getOrdre()));

        return "redirect:/backoffice/univers";
    }
}
