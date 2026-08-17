package fr.lacassinauteur.site.contact.presentation.web;

import fr.lacassinauteur.site.contact.application.command.EnvoyerMessageContactCommand;
import fr.lacassinauteur.site.contact.application.usecase.EnvoyerMessageContactUseCase;
import fr.lacassinauteur.site.contact.presentation.form.MessageContactForm;
import fr.lacassinauteur.site.shared.domain.port.CaptchaPort;
import fr.lacassinauteur.site.shared.web.HoneypotAntiSpam;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Page publique de contact : formulaire protégé par honeypot et reCAPTCHA v3
 * (cf. {@link MessageContactForm}, ADR-0019), même approche que le formulaire
 * newsletter.
 */
@Controller
@RequestMapping("/contact")
public class ContactController {

    private final EnvoyerMessageContactUseCase envoyerMessageContactUseCase;
    private final CaptchaPort captchaPort;

    @Value("${app.captcha.site-key}")
    private String captchaSiteKey;

    @ModelAttribute("captchaSiteKey")
    public String captchaSiteKey() {
        return captchaSiteKey;
    }

    public ContactController(EnvoyerMessageContactUseCase envoyerMessageContactUseCase, CaptchaPort captchaPort) {
        this.envoyerMessageContactUseCase = envoyerMessageContactUseCase;
        this.captchaPort = captchaPort;
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
        if (HoneypotAntiSpam.estRempli(formulaire.getSiteWeb())) {
            // Honeypot rempli : comportement identique à un envoi réussi, sans rien
            // traiter ni révéler le mécanisme au bot.
            redirectAttributes.addFlashAttribute("messageEnvoye", true);
            return "redirect:/contact";
        }

        if (bindingResult.hasErrors()) {
            return afficher(model);
        }

        if (!captchaPort.verifier(formulaire.getCaptchaToken())) {
            // Même traitement que le honeypot (cf. ADR-0019).
            redirectAttributes.addFlashAttribute("messageEnvoye", true);
            return "redirect:/contact";
        }

        envoyerMessageContactUseCase.execute(new EnvoyerMessageContactCommand(
                formulaire.getNom(), formulaire.getEmail(), formulaire.getObjet(), formulaire.getMessage()));

        redirectAttributes.addFlashAttribute("messageEnvoye", true);
        return "redirect:/contact";
    }
}
