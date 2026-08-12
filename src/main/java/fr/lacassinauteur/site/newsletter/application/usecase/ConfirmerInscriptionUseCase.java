package fr.lacassinauteur.site.newsletter.application.usecase;

import fr.lacassinauteur.site.newsletter.application.result.AbonneNewsletterResult;
import fr.lacassinauteur.site.newsletter.domain.exception.AbonneIntrouvableException;
import fr.lacassinauteur.site.newsletter.domain.model.AbonneNewsletter;
import fr.lacassinauteur.site.newsletter.domain.model.StatutAbonnement;
import fr.lacassinauteur.site.newsletter.domain.port.AbonneNewsletterRepository;
import fr.lacassinauteur.site.newsletter.domain.port.EnvoiEmailPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Confirmation de l'inscription via le lien reçu par email (deuxième étape du
 * double opt-in). Idempotent : cliquer deux fois sur le même lien ne réenvoie pas
 * l'email de bienvenue et ne fait pas planter la page.
 */
@Component
public class ConfirmerInscriptionUseCase {

    private final AbonneNewsletterRepository abonneNewsletterRepository;
    private final EnvoiEmailPort envoiEmailPort;
    private final String urlBase;

    public ConfirmerInscriptionUseCase(
            AbonneNewsletterRepository abonneNewsletterRepository,
            EnvoiEmailPort envoiEmailPort,
            @Value("${app.newsletter.url-base}") String urlBase) {
        this.abonneNewsletterRepository = abonneNewsletterRepository;
        this.envoiEmailPort = envoiEmailPort;
        this.urlBase = urlBase;
    }

    public AbonneNewsletterResult execute(UUID jeton) {
        AbonneNewsletter abonne = abonneNewsletterRepository.findByJeton(jeton)
                .orElseThrow(() -> new AbonneIntrouvableException(jeton));

        if (abonne.statut() == StatutAbonnement.EN_ATTENTE_CONFIRMATION) {
            abonne.confirmer();
            abonne = abonneNewsletterRepository.save(abonne);

            String lienDesinscription = urlBase + "/newsletter/desinscrire?jeton=" + abonne.jetonConfirmation();
            envoiEmailPort.envoyerEmailBienvenue(abonne.email(), abonne.prenom(), lienDesinscription);
        }

        return AbonneNewsletterResult.depuis(abonne);
    }
}
