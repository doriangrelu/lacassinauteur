package fr.lacassinauteur.site.biographie.presentation.backoffice;

import fr.lacassinauteur.site.biographie.application.command.ModifierBiographieCommand;
import fr.lacassinauteur.site.biographie.application.result.BiographieResult;
import fr.lacassinauteur.site.biographie.application.usecase.ConsulterBiographieUseCase;
import fr.lacassinauteur.site.biographie.application.usecase.ModifierBiographieUseCase;
import fr.lacassinauteur.site.biographie.presentation.form.BiographieForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Édition de la page « Auteur ». Pas de création ni de suppression : la biographie
 * est un enregistrement unique créé par le seeder (cf. ADR-0028), l'auteur ne peut
 * que la modifier — d'où l'absence de liste et de modale, contrairement au reste du
 * back-office.
 */
@Controller
@RequestMapping("/backoffice/auteur")
public class BackofficeBiographieController {

    private final ConsulterBiographieUseCase consulterBiographieUseCase;
    private final ModifierBiographieUseCase modifierBiographieUseCase;

    public BackofficeBiographieController(ConsulterBiographieUseCase consulterBiographieUseCase,
                                           ModifierBiographieUseCase modifierBiographieUseCase) {
        this.consulterBiographieUseCase = consulterBiographieUseCase;
        this.modifierBiographieUseCase = modifierBiographieUseCase;
    }

    @GetMapping
    public String formulaire(Model model) {
        BiographieResult biographie = consulterBiographieUseCase.execute();

        if (!model.containsAttribute("formulaire")) {
            BiographieForm formulaire = new BiographieForm();
            formulaire.setTexte(biographie.texte());
            model.addAttribute("formulaire", formulaire);
        }
        model.addAttribute("biographie", biographie);
        return "backoffice/biographie";
    }

    @PostMapping
    public String modifier(@Valid @ModelAttribute("formulaire") BiographieForm formulaire,
                            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return formulaire(model);
        }

        modifierBiographieUseCase.execute(new ModifierBiographieCommand(
                formulaire.getTexte(), octets(formulaire.getPhoto()), nomOriginal(formulaire.getPhoto())));

        return "redirect:/backoffice/auteur?enregistre";
    }

    private byte[] octets(MultipartFile fichier) {
        if (fichier == null || fichier.isEmpty()) {
            return null;
        }
        try {
            return fichier.getBytes();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private String nomOriginal(MultipartFile fichier) {
        return fichier == null ? null : fichier.getOriginalFilename();
    }
}
