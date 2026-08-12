package fr.lacassinauteur.site.newsletter.application.usecase;

import fr.lacassinauteur.site.newsletter.application.command.InscrireAbonneCommand;
import fr.lacassinauteur.site.newsletter.application.result.AbonneNewsletterResult;
import fr.lacassinauteur.site.newsletter.domain.model.AbonneNewsletter;
import fr.lacassinauteur.site.newsletter.domain.model.Email;
import fr.lacassinauteur.site.newsletter.domain.model.StatutAbonnement;
import fr.lacassinauteur.site.newsletter.domain.port.AbonneNewsletterRepository;
import fr.lacassinauteur.site.newsletter.domain.port.EnvoiEmailPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Inscription à la newsletter (première étape du double opt-in). Volontairement
 * idempotente plutôt qu'en échec bruyant sur une ré-inscription (cf. ADR-0013) :
 * <ul>
 *     <li>email déjà {@code CONFIRME} : aucun ré-envoi, retour silencieux — évite
 *     de spammer un abonné déjà actif si le formulaire est resoumis ;</li>
 *     <li>email déjà {@code EN_ATTENTE_CONFIRMATION} : relance l'email de
 *     confirmation (nouveau jeton) — utile si le premier email s'est perdu ;</li>
 *     <li>email {@code DESINSCRIT} : relance une inscription complète (nouveau
 *     jeton), traité comme un retour volontaire ;</li>
 *     <li>email inconnu : création normale.</li>
 * </ul>
 */
@Component
public class InscrireAbonneUseCase {

    private final AbonneNewsletterRepository abonneNewsletterRepository;
    private final EnvoiEmailPort envoiEmailPort;
    private final String urlBase;

    public InscrireAbonneUseCase(
            AbonneNewsletterRepository abonneNewsletterRepository,
            EnvoiEmailPort envoiEmailPort,
            @Value("${app.newsletter.url-base}") String urlBase) {
        this.abonneNewsletterRepository = abonneNewsletterRepository;
        this.envoiEmailPort = envoiEmailPort;
        this.urlBase = urlBase;
    }

    public AbonneNewsletterResult execute(InscrireAbonneCommand command) {
        Email email = new Email(command.email());

        AbonneNewsletter abonne = abonneNewsletterRepository.findByEmail(email).orElse(null);

        if (abonne == null) {
            abonne = AbonneNewsletter.creer(command.prenom(), email);
        } else if (abonne.statut() == StatutAbonnement.CONFIRME) {
            return AbonneNewsletterResult.depuis(abonne);
        } else {
            abonne.relancerInscription();
        }

        abonne = abonneNewsletterRepository.save(abonne);

        String lienConfirmation = urlBase + "/newsletter/confirmer?jeton=" + abonne.jetonConfirmation();
        envoiEmailPort.envoyerEmailConfirmation(email, abonne.prenom(), lienConfirmation);

        return AbonneNewsletterResult.depuis(abonne);
    }
}
