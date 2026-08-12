package fr.lacassinauteur.site.newsletter.presentation.backoffice;

import fr.lacassinauteur.site.newsletter.application.usecase.ListerAbonnesUseCase;
import fr.lacassinauteur.site.newsletter.presentation.mapper.AbonneNewsletterViewModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Liste des abonnés newsletter en back-office (lecture seule en v1 : les abonnés
 * s'inscrivent eux-mêmes côté public, pas de création manuelle depuis cet écran —
 * cf. décision documentée dans ADR-0013).
 */
@Controller
@RequestMapping("/backoffice/abonnes")
public class BackofficeAbonneNewsletterController {

    private final ListerAbonnesUseCase listerAbonnesUseCase;
    private final AbonneNewsletterViewModelMapper mapper;

    public BackofficeAbonneNewsletterController(ListerAbonnesUseCase listerAbonnesUseCase,
                                                 AbonneNewsletterViewModelMapper mapper) {
        this.listerAbonnesUseCase = listerAbonnesUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("abonnes", listerAbonnesUseCase.execute().stream().map(mapper::versViewModel).toList());
        return "backoffice/newsletter/abonnes-liste";
    }
}
