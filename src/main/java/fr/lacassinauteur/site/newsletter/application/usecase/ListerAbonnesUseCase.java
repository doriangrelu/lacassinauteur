package fr.lacassinauteur.site.newsletter.application.usecase;

import fr.lacassinauteur.site.newsletter.application.result.AbonneNewsletterResult;
import fr.lacassinauteur.site.newsletter.domain.port.AbonneNewsletterRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/** Liste complète des abonnés (tous statuts confondus), pour l'écran back-office. */
@Component
public class ListerAbonnesUseCase {

    private final AbonneNewsletterRepository abonneNewsletterRepository;

    public ListerAbonnesUseCase(AbonneNewsletterRepository abonneNewsletterRepository) {
        this.abonneNewsletterRepository = abonneNewsletterRepository;
    }

    public List<AbonneNewsletterResult> execute() {
        return abonneNewsletterRepository.findAllOrderByDateInscriptionDesc().stream()
                .map(AbonneNewsletterResult::depuis)
                .toList();
    }
}
