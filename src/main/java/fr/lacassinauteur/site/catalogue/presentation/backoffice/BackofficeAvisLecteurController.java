package fr.lacassinauteur.site.catalogue.presentation.backoffice;

import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.application.usecase.avis.ApprouverAvisLecteurUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.avis.ListerAvisLecteurUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.avis.RejeterAvisLecteurUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.livre.ListerTousLesLivresUseCase;
import fr.lacassinauteur.site.catalogue.presentation.mapper.AvisLecteurViewModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Modération des avis lecteurs soumis publiquement : liste (tous statuts, les plus
 * récents en tête) et actions d'approbation / rejet.
 */
@Controller
@RequestMapping("/backoffice/avis")
public class BackofficeAvisLecteurController {

    private final ListerAvisLecteurUseCase listerAvisLecteurUseCase;
    private final ApprouverAvisLecteurUseCase approuverAvisLecteurUseCase;
    private final RejeterAvisLecteurUseCase rejeterAvisLecteurUseCase;
    private final ListerTousLesLivresUseCase listerTousLesLivresUseCase;
    private final AvisLecteurViewModelMapper mapper;

    public BackofficeAvisLecteurController(
            ListerAvisLecteurUseCase listerAvisLecteurUseCase,
            ApprouverAvisLecteurUseCase approuverAvisLecteurUseCase,
            RejeterAvisLecteurUseCase rejeterAvisLecteurUseCase,
            ListerTousLesLivresUseCase listerTousLesLivresUseCase,
            AvisLecteurViewModelMapper mapper) {
        this.listerAvisLecteurUseCase = listerAvisLecteurUseCase;
        this.approuverAvisLecteurUseCase = approuverAvisLecteurUseCase;
        this.rejeterAvisLecteurUseCase = rejeterAvisLecteurUseCase;
        this.listerTousLesLivresUseCase = listerTousLesLivresUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public String liste(Model model) {
        Map<UUID, String> titresLivres = listerTousLesLivresUseCase.execute().stream()
                .collect(Collectors.toMap(LivreResult::id, LivreResult::titre));

        model.addAttribute("avis", listerAvisLecteurUseCase.execute().stream()
                .map(result -> mapper.versViewModel(result, titresLivres.get(result.livreId())))
                .toList());
        return "backoffice/catalogue/avis-liste";
    }

    @PostMapping("/{id}/approuver")
    public String approuver(@PathVariable UUID id) {
        approuverAvisLecteurUseCase.execute(id);
        return "redirect:/backoffice/avis";
    }

    @PostMapping("/{id}/rejeter")
    public String rejeter(@PathVariable UUID id) {
        rejeterAvisLecteurUseCase.execute(id);
        return "redirect:/backoffice/avis";
    }
}
