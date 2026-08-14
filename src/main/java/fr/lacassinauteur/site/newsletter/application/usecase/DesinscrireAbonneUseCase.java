package fr.lacassinauteur.site.newsletter.application.usecase;

import fr.lacassinauteur.site.newsletter.application.result.AbonneNewsletterResult;
import fr.lacassinauteur.site.newsletter.domain.exception.AbonneIntrouvableException;
import fr.lacassinauteur.site.newsletter.domain.model.AbonneNewsletter;
import fr.lacassinauteur.site.newsletter.domain.model.StatutAbonnement;
import fr.lacassinauteur.site.newsletter.domain.port.AbonneNewsletterRepository;
import fr.lacassinauteur.site.newsletter.domain.port.SynchronisationEspPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Désinscription via le lien présent dans les envois (même jeton que la
 * confirmation, cf. {@code AbonneNewsletter}). Idempotent : redemander la
 * désinscription d'un abonné déjà désinscrit n'est pas une erreur.
 */
@Component
public class DesinscrireAbonneUseCase {

    private static final Logger LOG = LoggerFactory.getLogger(DesinscrireAbonneUseCase.class);

    private final AbonneNewsletterRepository abonneNewsletterRepository;
    private final SynchronisationEspPort synchronisationEspPort;

    public DesinscrireAbonneUseCase(AbonneNewsletterRepository abonneNewsletterRepository, SynchronisationEspPort synchronisationEspPort) {
        this.abonneNewsletterRepository = abonneNewsletterRepository;
        this.synchronisationEspPort = synchronisationEspPort;
    }

    public AbonneNewsletterResult execute(UUID jeton) {
        AbonneNewsletter abonne = abonneNewsletterRepository.findByJeton(jeton)
                .orElseThrow(() -> new AbonneIntrouvableException(jeton));

        if (abonne.statut() != StatutAbonnement.DESINSCRIT) {
            abonne.desinscrire();
            abonne = abonneNewsletterRepository.save(abonne);

            // Même raisonnement qu'à la confirmation : la désinscription est déjà
            // enregistrée, un échec Brevo ne doit pas empêcher le visiteur de se
            // désinscrire avec succès.
            try {
                synchronisationEspPort.retirer(abonne);
            } catch (RuntimeException exception) {
                LOG.warn("Échec du retrait Brevo pour l'abonné {}", abonne.id(), exception);
            }
        }

        return AbonneNewsletterResult.depuis(abonne);
    }
}
