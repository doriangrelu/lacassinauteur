package fr.lacassinauteur.site.identity.application.usecase;

import fr.lacassinauteur.site.identity.application.command.DemanderReinitialisationMotDePasseCommand;
import fr.lacassinauteur.site.identity.domain.model.Email;
import fr.lacassinauteur.site.identity.domain.model.JetonReinitialisation;
import fr.lacassinauteur.site.identity.domain.model.Utilisateur;
import fr.lacassinauteur.site.identity.domain.port.EnvoiEmailIdentityPort;
import fr.lacassinauteur.site.identity.domain.port.JetonReinitialisationMotDePassePort;
import fr.lacassinauteur.site.identity.domain.port.JetonReinitialisationRepository;
import fr.lacassinauteur.site.identity.domain.port.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Demande de réinitialisation de mot de passe (« mot de passe oublié »). Ne
 * révèle jamais si l'adresse correspond à un compte existant ou actif. Un seul
 * jeton actif à la fois par compte (cf. ADR-0020) : si une demande précédente
 * est encore valide, son lien est renvoyé tel quel plutôt que d'en émettre un
 * second.
 */
@Component
public class DemanderReinitialisationMotDePasseUseCase {

    private static final Logger LOG = LoggerFactory.getLogger(DemanderReinitialisationMotDePasseUseCase.class);

    private final UtilisateurRepository utilisateurRepository;
    private final JetonReinitialisationRepository jetonReinitialisationRepository;
    private final JetonReinitialisationMotDePassePort jetonPort;
    private final EnvoiEmailIdentityPort envoiEmailIdentityPort;
    private final String urlBase;
    private final Duration dureeValidite;

    public DemanderReinitialisationMotDePasseUseCase(
            UtilisateurRepository utilisateurRepository,
            JetonReinitialisationRepository jetonReinitialisationRepository,
            JetonReinitialisationMotDePassePort jetonPort,
            EnvoiEmailIdentityPort envoiEmailIdentityPort,
            @Value("${app.identity.url-base}") String urlBase,
            @Value("${app.identity.jwt.duree-validite-minutes}") long dureeValiditeMinutes) {
        this.utilisateurRepository = utilisateurRepository;
        this.jetonReinitialisationRepository = jetonReinitialisationRepository;
        this.jetonPort = jetonPort;
        this.envoiEmailIdentityPort = envoiEmailIdentityPort;
        this.urlBase = urlBase;
        this.dureeValidite = Duration.ofMinutes(dureeValiditeMinutes);
    }

    public void execute(DemanderReinitialisationMotDePasseCommand command) {
        Email email = new Email(command.email());
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email).orElse(null);

        if (utilisateur == null || !utilisateur.actif()) {
            return;
        }

        String jeton = jetonReinitialisationRepository.findValidePourUtilisateur(utilisateur.id())
                .map(JetonReinitialisation::jeton)
                .orElseGet(() -> genererEtEnregistrerNouveauJeton(utilisateur.id()));

        String lien = urlBase + "/backoffice/reinitialiser-mot-de-passe?jeton=" + jeton;

        // Un compte existe et est actif : un échec d'envoi (ESP indisponible...) ne
        // doit pas faire planter la demande cote visiteur, meme principe que
        // newsletter/contact.
        try {
            envoiEmailIdentityPort.envoyerLienReinitialisation(email, lien);
        } catch (RuntimeException exception) {
            LOG.warn("Échec de l'envoi de l'email de réinitialisation pour l'utilisateur {}", utilisateur.id(), exception);
        }
    }

    private String genererEtEnregistrerNouveauJeton(UUID utilisateurId) {
        jetonReinitialisationRepository.deleteParUtilisateur(utilisateurId);

        UUID jetonId = UUID.randomUUID();
        String jeton = jetonPort.genererJeton(utilisateurId, jetonId);
        jetonReinitialisationRepository.save(
                new JetonReinitialisation(jetonId, utilisateurId, jeton, Instant.now().plus(dureeValidite)));
        return jeton;
    }
}
