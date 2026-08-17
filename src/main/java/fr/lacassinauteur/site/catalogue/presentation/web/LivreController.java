package fr.lacassinauteur.site.catalogue.presentation.web;

import fr.lacassinauteur.site.catalogue.application.command.SoumettreAvisLecteurCommand;
import fr.lacassinauteur.site.catalogue.application.result.CollectionResult;
import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.application.result.UniversResult;
import fr.lacassinauteur.site.catalogue.application.usecase.avis.ListerAvisLecteurPublieParLivreUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.avis.SoumettreAvisLecteurUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.collection.ConsulterCollectionUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.livre.ConsulterLivreParSlugUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.univers.ConsulterUniversUseCase;
import fr.lacassinauteur.site.catalogue.presentation.form.AvisLecteurForm;
import fr.lacassinauteur.site.shared.domain.port.CaptchaPort;
import fr.lacassinauteur.site.shared.web.HoneypotAntiSpam;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LivreController {

    private final ConsulterLivreParSlugUseCase consulterLivreParSlugUseCase;
    private final ConsulterCollectionUseCase consulterCollectionUseCase;
    private final ConsulterUniversUseCase consulterUniversUseCase;
    private final ListerAvisLecteurPublieParLivreUseCase listerAvisLecteurPublieParLivreUseCase;
    private final SoumettreAvisLecteurUseCase soumettreAvisLecteurUseCase;
    private final CaptchaPort captchaPort;

    @Value("${app.captcha.site-key}")
    private String captchaSiteKey;

    @ModelAttribute("captchaSiteKey")
    public String captchaSiteKey() {
        return captchaSiteKey;
    }

    public LivreController(
            ConsulterLivreParSlugUseCase consulterLivreParSlugUseCase,
            ConsulterCollectionUseCase consulterCollectionUseCase,
            ConsulterUniversUseCase consulterUniversUseCase,
            ListerAvisLecteurPublieParLivreUseCase listerAvisLecteurPublieParLivreUseCase,
            SoumettreAvisLecteurUseCase soumettreAvisLecteurUseCase,
            CaptchaPort captchaPort) {
        this.consulterLivreParSlugUseCase = consulterLivreParSlugUseCase;
        this.consulterCollectionUseCase = consulterCollectionUseCase;
        this.consulterUniversUseCase = consulterUniversUseCase;
        this.listerAvisLecteurPublieParLivreUseCase = listerAvisLecteurPublieParLivreUseCase;
        this.soumettreAvisLecteurUseCase = soumettreAvisLecteurUseCase;
        this.captchaPort = captchaPort;
    }

    @GetMapping("/livres/{slug}")
    public String afficher(@PathVariable String slug, Model model) {
        LivreResult livre = consulterLivreParSlugUseCase.execute(slug);
        CollectionResult collection = consulterCollectionUseCase.execute(livre.collectionId());
        UniversResult univers = consulterUniversUseCase.execute(collection.universId());

        model.addAttribute("livre", livre);
        model.addAttribute("collection", collection);
        model.addAttribute("univers", univers);
        model.addAttribute("metaDescription", tronquer(
                livre.pitchCourt() != null ? livre.pitchCourt() : livre.resume(), 155));
        model.addAttribute("avisLecteurs", listerAvisLecteurPublieParLivreUseCase.execute(livre.id()));
        if (!model.containsAttribute("formulaireAvis")) {
            model.addAttribute("formulaireAvis", new AvisLecteurForm());
        }
        return "public/livre";
    }

    @PostMapping("/livres/{slug}/avis")
    public String soumettreAvis(@PathVariable String slug, @Valid @ModelAttribute("formulaireAvis") AvisLecteurForm formulaire,
                                 BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        LivreResult livre = consulterLivreParSlugUseCase.execute(slug);

        if (HoneypotAntiSpam.estRempli(formulaire.getSiteWeb())) {
            // Honeypot rempli : comportement identique à un envoi réussi, sans rien
            // traiter ni révéler le mécanisme au bot.
            redirectAttributes.addFlashAttribute("avisEnvoye", true);
            return "redirect:/livres/" + slug;
        }

        if (bindingResult.hasErrors()) {
            return afficher(slug, model);
        }

        if (!captchaPort.verifier(formulaire.getCaptchaToken())) {
            // Même traitement que le honeypot (cf. ADR-0019).
            redirectAttributes.addFlashAttribute("avisEnvoye", true);
            return "redirect:/livres/" + slug;
        }

        soumettreAvisLecteurUseCase.execute(new SoumettreAvisLecteurCommand(
                livre.id(), formulaire.getNomAuteurAvis(), formulaire.getTexte(), formulaire.getNote()));

        redirectAttributes.addFlashAttribute("avisEnvoye", true);
        return "redirect:/livres/" + slug;
    }

    private String tronquer(String texte, int longueurMax) {
        if (texte == null) {
            return "";
        }
        String nettoye = texte.strip();
        return nettoye.length() <= longueurMax ? nettoye : nettoye.substring(0, longueurMax - 1).strip() + "…";
    }
}
