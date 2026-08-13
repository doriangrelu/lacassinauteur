package fr.lacassinauteur.site.contact.presentation.web;

import fr.lacassinauteur.site.contact.application.command.EnvoyerMessageContactCommand;
import fr.lacassinauteur.site.contact.application.usecase.EnvoyerMessageContactUseCase;
import fr.lacassinauteur.site.contact.presentation.form.MessageContactForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Page publique de contact : formulaire protégé par honeypot (cf.
 * {@link MessageContactForm}), même approche que le formulaire newsletter.
 */
@Controller
@RequestMapping("/contact")
public class ContactController {

    private final EnvoyerMessageContactUseCase envoyerMessageContactUseCase;

    public ContactController(EnvoyerMessageContactUseCase envoyerMessageContactUseCase) {
        this.envoyerMessageContactUseCase = envoyerMessageContactUseCase;
    }

    @GetMapping
    public String afficher(Model model) {
        if (!model.containsAttribute("formulaire")) {
            model.addAttribute("formulaire", new MessageContactForm());
        }
        return "public/contact";
    }

    @PostMapping("/envoi")
    public String envoyer(@Valid @ModelAttribute("formulaire") MessageContactForm formulaire,
                           BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (StringUtils.hasText(formulaire.getSiteWeb())) {
            // Honeypot rempli : comportement identique à un envoi réussi, sans rien
            // traiter ni révéler le mécanisme au bot.
            redirectAttributes.addFlashAttribute("messageEnvoye", true);
            return "redirect:/contact";
        }

        if (bindingResult.hasErrors()) {
            return afficher(model);
        }

        envoyerMessageContactUseCase.execute(new EnvoyerMessageContactCommand(
                formulaire.getNom(), formulaire.getEmail(), formulaire.getObjet(), formulaire.getMessage()));

        redirectAttributes.addFlashAttribute("messageEnvoye", true);
        return "redirect:/contact";
    }
}
