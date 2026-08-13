package fr.lacassinauteur.site.newsletter.presentation.web;

import fr.lacassinauteur.site.newsletter.application.command.InscrireAbonneCommand;
import fr.lacassinauteur.site.newsletter.application.usecase.ConfirmerInscriptionUseCase;
import fr.lacassinauteur.site.newsletter.application.usecase.DesinscrireAbonneUseCase;
import fr.lacassinauteur.site.newsletter.application.usecase.InscrireAbonneUseCase;
import fr.lacassinauteur.site.newsletter.domain.exception.AbonneIntrouvableException;
import fr.lacassinauteur.site.newsletter.presentation.form.InscriptionNewsletterForm;
import fr.lacassinauteur.site.shared.web.HoneypotAntiSpam;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/**
 * Page publique newsletter : formulaire d'inscription (protégé par honeypot, cf.
 * {@link InscriptionNewsletterForm}) et les deux liens publics du double opt-in
 * (confirmation, désinscription). Un jeton invalide/expiré ne renvoie pas une 404
 * brute : la page se réaffiche avec un message adapté (cf. ADR-0013).
 */
@Controller
@RequestMapping("/newsletter")
public class NewsletterController {

    private final InscrireAbonneUseCase inscrireAbonneUseCase;
    private final ConfirmerInscriptionUseCase confirmerInscriptionUseCase;
    private final DesinscrireAbonneUseCase desinscrireAbonneUseCase;

    public NewsletterController(
            InscrireAbonneUseCase inscrireAbonneUseCase,
            ConfirmerInscriptionUseCase confirmerInscriptionUseCase,
            DesinscrireAbonneUseCase desinscrireAbonneUseCase) {
        this.inscrireAbonneUseCase = inscrireAbonneUseCase;
        this.confirmerInscriptionUseCase = confirmerInscriptionUseCase;
        this.desinscrireAbonneUseCase = desinscrireAbonneUseCase;
    }

    @GetMapping
    public String afficher(Model model) {
        if (!model.containsAttribute("formulaire")) {
            model.addAttribute("formulaire", new InscriptionNewsletterForm());
        }
        return "public/newsletter";
    }

    @PostMapping("/inscription")
    public String inscrire(@Valid @ModelAttribute("formulaire") InscriptionNewsletterForm formulaire,
                            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (HoneypotAntiSpam.estRempli(formulaire.getSiteWeb())) {
            // Honeypot rempli : on se comporte comme si tout s'était bien passé, sans
            // rien traiter ni révéler le mécanisme au bot.
            redirectAttributes.addFlashAttribute("messageInscription", "merci");
            return "redirect:/newsletter";
        }

        if (bindingResult.hasErrors()) {
            return afficher(model);
        }

        inscrireAbonneUseCase.execute(new InscrireAbonneCommand(formulaire.getPrenom(), formulaire.getEmail()));
        redirectAttributes.addFlashAttribute("messageInscription", "merci");
        return "redirect:/newsletter";
    }

    @GetMapping("/confirmer")
    public String confirmer(@RequestParam UUID jeton, Model model) {
        try {
            confirmerInscriptionUseCase.execute(jeton);
            model.addAttribute("messageStatut", "confirme");
        } catch (AbonneIntrouvableException exception) {
            model.addAttribute("messageStatut", "lienInvalide");
        }
        return afficher(model);
    }

    @GetMapping("/desinscrire")
    public String desinscrire(@RequestParam UUID jeton, Model model) {
        try {
            desinscrireAbonneUseCase.execute(jeton);
            model.addAttribute("messageStatut", "desinscrit");
        } catch (AbonneIntrouvableException exception) {
            model.addAttribute("messageStatut", "lienInvalide");
        }
        return afficher(model);
    }
}
