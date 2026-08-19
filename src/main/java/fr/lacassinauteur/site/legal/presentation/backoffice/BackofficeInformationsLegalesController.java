package fr.lacassinauteur.site.legal.presentation.backoffice;

import fr.lacassinauteur.site.legal.application.command.ModifierInformationsLegalesCommand;
import fr.lacassinauteur.site.legal.application.result.InformationsLegalesResult;
import fr.lacassinauteur.site.legal.application.usecase.ConsulterInformationsLegalesUseCase;
import fr.lacassinauteur.site.legal.application.usecase.ModifierInformationsLegalesUseCase;
import fr.lacassinauteur.site.legal.presentation.form.InformationsLegalesForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Édition des variables des pages légales. Enregistrement unique (cf. ADR-0029) :
 * ni création ni suppression, uniquement la modification.
 */
@Controller
@RequestMapping("/backoffice/informations-legales")
public class BackofficeInformationsLegalesController {

    private final ConsulterInformationsLegalesUseCase consulterInformationsLegalesUseCase;
    private final ModifierInformationsLegalesUseCase modifierInformationsLegalesUseCase;

    public BackofficeInformationsLegalesController(
            ConsulterInformationsLegalesUseCase consulterInformationsLegalesUseCase,
            ModifierInformationsLegalesUseCase modifierInformationsLegalesUseCase) {
        this.consulterInformationsLegalesUseCase = consulterInformationsLegalesUseCase;
        this.modifierInformationsLegalesUseCase = modifierInformationsLegalesUseCase;
    }

    @GetMapping
    public String formulaire(Model model) {
        InformationsLegalesResult informations = consulterInformationsLegalesUseCase.execute();

        if (!model.containsAttribute("formulaire")) {
            InformationsLegalesForm formulaire = new InformationsLegalesForm();
            formulaire.setEditeurNom(informations.editeurNom());
            formulaire.setEditeurStatut(informations.editeurStatut());
            formulaire.setEditeurAdresse(informations.editeurAdresse());
            formulaire.setEditeurEmail(informations.editeurEmail());
            formulaire.setDirecteurPublication(informations.directeurPublication());
            formulaire.setHebergeurNom(informations.hebergeurNom());
            formulaire.setHebergeurAdresse(informations.hebergeurAdresse());
            formulaire.setConservationNewsletterMois(informations.conservationNewsletterMois());
            formulaire.setConservationContactMois(informations.conservationContactMois());
            model.addAttribute("formulaire", formulaire);
        }
        model.addAttribute("informations", informations);
        return "backoffice/informations-legales";
    }

    @PostMapping
    public String modifier(@Valid @ModelAttribute("formulaire") InformationsLegalesForm formulaire,
                            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return formulaire(model);
        }

        modifierInformationsLegalesUseCase.execute(new ModifierInformationsLegalesCommand(
                formulaire.getEditeurNom(), formulaire.getEditeurStatut(), formulaire.getEditeurAdresse(),
                formulaire.getEditeurEmail(), formulaire.getDirecteurPublication(), formulaire.getHebergeurNom(),
                formulaire.getHebergeurAdresse(), formulaire.getConservationNewsletterMois(),
                formulaire.getConservationContactMois()));

        return "redirect:/backoffice/informations-legales?enregistre";
    }
}
