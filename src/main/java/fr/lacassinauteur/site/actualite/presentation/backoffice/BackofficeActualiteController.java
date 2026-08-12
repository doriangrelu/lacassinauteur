package fr.lacassinauteur.site.actualite.presentation.backoffice;

import fr.lacassinauteur.site.actualite.application.command.CreerActualiteCommand;
import fr.lacassinauteur.site.actualite.application.command.ModifierActualiteCommand;
import fr.lacassinauteur.site.actualite.application.result.ActualiteResult;
import fr.lacassinauteur.site.actualite.application.usecase.ConsulterActualiteUseCase;
import fr.lacassinauteur.site.actualite.application.usecase.CreerActualiteUseCase;
import fr.lacassinauteur.site.actualite.application.usecase.ListerActualitesUseCase;
import fr.lacassinauteur.site.actualite.application.usecase.ModifierActualiteUseCase;
import fr.lacassinauteur.site.actualite.application.usecase.SupprimerActualiteUseCase;
import fr.lacassinauteur.site.actualite.presentation.form.ActualiteForm;
import fr.lacassinauteur.site.actualite.presentation.mapper.ActualiteViewModelMapper;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.UUID;

@Controller
@RequestMapping("/backoffice/actualites")
public class BackofficeActualiteController {

    private final ListerActualitesUseCase listerActualitesUseCase;
    private final ConsulterActualiteUseCase consulterActualiteUseCase;
    private final CreerActualiteUseCase creerActualiteUseCase;
    private final ModifierActualiteUseCase modifierActualiteUseCase;
    private final SupprimerActualiteUseCase supprimerActualiteUseCase;
    private final ActualiteViewModelMapper mapper;

    public BackofficeActualiteController(
            ListerActualitesUseCase listerActualitesUseCase,
            ConsulterActualiteUseCase consulterActualiteUseCase,
            CreerActualiteUseCase creerActualiteUseCase,
            ModifierActualiteUseCase modifierActualiteUseCase,
            SupprimerActualiteUseCase supprimerActualiteUseCase,
            ActualiteViewModelMapper mapper) {
        this.listerActualitesUseCase = listerActualitesUseCase;
        this.consulterActualiteUseCase = consulterActualiteUseCase;
        this.creerActualiteUseCase = creerActualiteUseCase;
        this.modifierActualiteUseCase = modifierActualiteUseCase;
        this.supprimerActualiteUseCase = supprimerActualiteUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("actualites", listerActualitesUseCase.execute().stream().map(mapper::versViewModel).toList());
        if (!model.containsAttribute("formulaire")) {
            model.addAttribute("formulaire", new ActualiteForm());
        }
        return "backoffice/actualite/actualite-liste";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("formulaire") ActualiteForm formulaire, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return liste(model);
        }

        creerActualiteUseCase.execute(new CreerActualiteCommand(
                formulaire.getTitre(), formulaire.getTexte(), formulaire.getDate(), formulaire.getLieu(),
                formulaire.getLienBilletterie(), octets(formulaire.getImage()), nomOriginal(formulaire.getImage()),
                formulaire.isArchiveeManuellement(), formulaire.isMisEnAvant()));

        return "redirect:/backoffice/actualites";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable UUID id, Model model) {
        ActualiteResult actualite = consulterActualiteUseCase.execute(id);

        if (!model.containsAttribute("formulaire")) {
            ActualiteForm formulaire = new ActualiteForm();
            formulaire.setTitre(actualite.titre());
            formulaire.setTexte(actualite.texte());
            formulaire.setDate(actualite.date());
            formulaire.setLieu(actualite.lieu());
            formulaire.setLienBilletterie(actualite.lienBilletterie());
            formulaire.setArchiveeManuellement(actualite.archiveeManuellement());
            formulaire.setMisEnAvant(actualite.misEnAvant());
            model.addAttribute("formulaire", formulaire);
        }
        model.addAttribute("actualite", actualite);
        model.addAttribute("actualiteId", id);
        return "backoffice/actualite/actualite-modifier";
    }

    @PostMapping("/{id}")
    public String modifier(@PathVariable UUID id, @Valid @ModelAttribute("formulaire") ActualiteForm formulaire,
                            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return formulaireModification(id, model);
        }

        modifierActualiteUseCase.execute(new ModifierActualiteCommand(
                id, formulaire.getTitre(), formulaire.getTexte(), formulaire.getDate(), formulaire.getLieu(),
                formulaire.getLienBilletterie(), octets(formulaire.getImage()), nomOriginal(formulaire.getImage()),
                formulaire.isArchiveeManuellement(), formulaire.isMisEnAvant()));

        return "redirect:/backoffice/actualites";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable UUID id) {
        supprimerActualiteUseCase.execute(id);
        return "redirect:/backoffice/actualites";
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
