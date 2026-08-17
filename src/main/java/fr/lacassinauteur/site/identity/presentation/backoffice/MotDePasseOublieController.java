package fr.lacassinauteur.site.identity.presentation.backoffice;

import fr.lacassinauteur.site.identity.application.command.DemanderReinitialisationMotDePasseCommand;
import fr.lacassinauteur.site.identity.application.command.ReinitialiserMotDePasseCommand;
import fr.lacassinauteur.site.identity.application.usecase.DemanderReinitialisationMotDePasseUseCase;
import fr.lacassinauteur.site.identity.application.usecase.ReinitialiserMotDePasseUseCase;
import fr.lacassinauteur.site.identity.domain.exception.JetonReinitialisationInvalideException;
import fr.lacassinauteur.site.identity.presentation.form.DemanderReinitialisationForm;
import fr.lacassinauteur.site.identity.presentation.form.ReinitialiserMotDePasseForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MotDePasseOublieController {

    private final DemanderReinitialisationMotDePasseUseCase demanderReinitialisationUseCase;
    private final ReinitialiserMotDePasseUseCase reinitialiserMotDePasseUseCase;

    public MotDePasseOublieController(
            DemanderReinitialisationMotDePasseUseCase demanderReinitialisationUseCase,
            ReinitialiserMotDePasseUseCase reinitialiserMotDePasseUseCase) {
        this.demanderReinitialisationUseCase = demanderReinitialisationUseCase;
        this.reinitialiserMotDePasseUseCase = reinitialiserMotDePasseUseCase;
    }

    @GetMapping("/backoffice/mot-de-passe-oublie")
    public String formulaireDemande(Model model) {
        if (!model.containsAttribute("formulaire")) {
            model.addAttribute("formulaire", new DemanderReinitialisationForm());
        }
        return "backoffice/mot-de-passe-oublie";
    }

    @PostMapping("/backoffice/mot-de-passe-oublie")
    public String demander(@Valid @ModelAttribute("formulaire") DemanderReinitialisationForm formulaire,
                            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return formulaireDemande(model);
        }

        demanderReinitialisationUseCase.execute(new DemanderReinitialisationMotDePasseCommand(formulaire.getEmail()));

        return "redirect:/backoffice/mot-de-passe-oublie?envoye";
    }

    @GetMapping("/backoffice/reinitialiser-mot-de-passe")
    public String formulaireReinitialisation(@RequestParam String jeton, Model model) {
        if (!model.containsAttribute("formulaire")) {
            ReinitialiserMotDePasseForm formulaire = new ReinitialiserMotDePasseForm();
            formulaire.setJeton(jeton);
            model.addAttribute("formulaire", formulaire);
        }
        return "backoffice/reinitialiser-mot-de-passe";
    }

    @PostMapping("/backoffice/reinitialiser-mot-de-passe")
    public String reinitialiser(@Valid @ModelAttribute("formulaire") ReinitialiserMotDePasseForm formulaire,
                                 BindingResult bindingResult, Model model) {
        if (!bindingResult.hasErrors()
                && !formulaire.getNouveauMotDePasse().equals(formulaire.getConfirmationMotDePasse())) {
            bindingResult.rejectValue("confirmationMotDePasse", "confirmation.differente",
                    "Les deux mots de passe ne correspondent pas");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("jeton", formulaire.getJeton());
            return "backoffice/reinitialiser-mot-de-passe";
        }

        try {
            reinitialiserMotDePasseUseCase.execute(
                    new ReinitialiserMotDePasseCommand(formulaire.getJeton(), formulaire.getNouveauMotDePasse()));
        } catch (JetonReinitialisationInvalideException exception) {
            model.addAttribute("lienExpire", true);
            return "backoffice/reinitialiser-mot-de-passe";
        }

        return "redirect:/backoffice/connexion?motDePasseReinitialise";
    }
}
