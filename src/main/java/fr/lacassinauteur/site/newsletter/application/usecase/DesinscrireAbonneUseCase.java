package fr.lacassinauteur.site.newsletter.application.usecase;

import fr.lacassinauteur.site.newsletter.application.result.AbonneNewsletterResult;
import fr.lacassinauteur.site.newsletter.domain.exception.AbonneIntrouvableException;
import fr.lacassinauteur.site.newsletter.domain.model.AbonneNewsletter;
import fr.lacassinauteur.site.newsletter.domain.model.StatutAbonnement;
import fr.lacassinauteur.site.newsletter.domain.port.AbonneNewsletterRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Désinscription via le lien présent dans les envois (même jeton que la
 * confirmation, cf. {@code AbonneNewsletter}). Idempotent : redemander la
 * désinscription d'un abonné déjà désinscrit n'est pas une erreur.
 */
@Component
public class DesinscrireAbonneUseCase {

    private final AbonneNewsletterRepository abonneNewsletterRepository;

    public DesinscrireAbonneUseCase(AbonneNewsletterRepository abonneNewsletterRepository) {
        this.abonneNewsletterRepository = abonneNewsletterRepository;
    }

    public AbonneNewsletterResult execute(UUID jeton) {
        AbonneNewsletter abonne = abonneNewsletterRepository.findByJeton(jeton)
                .orElseThrow(() -> new AbonneIntrouvableException(jeton));

        if (abonne.statut() != StatutAbonnement.DESINSCRIT) {
            abonne.desinscrire();
            abonne = abonneNewsletterRepository.save(abonne);
        }

        return AbonneNewsletterResult.depuis(abonne);
    }
}
