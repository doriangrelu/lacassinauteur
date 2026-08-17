package fr.lacassinauteur.site.identity.application.usecase;

import fr.lacassinauteur.site.identity.application.command.DemanderReinitialisationMotDePasseCommand;
import fr.lacassinauteur.site.identity.domain.model.Email;
import fr.lacassinauteur.site.identity.domain.model.Utilisateur;
import fr.lacassinauteur.site.identity.domain.port.EnvoiEmailIdentityPort;
import fr.lacassinauteur.site.identity.domain.port.JetonReinitialisationMotDePassePort;
import fr.lacassinauteur.site.identity.domain.port.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Demande de réinitialisation de mot de passe (« mot de passe oublié »). Ne
 * révèle jamais si l'adresse correspond à un compte existant ou actif : le
 * visiteur voit toujours le même message de confirmation côté contrôleur, que
 * l'email parte réellement ou non — évite l'énumération de comptes.
 */
@Component
public class DemanderReinitialisationMotDePasseUseCase {

    private static final Logger LOG = LoggerFactory.getLogger(DemanderReinitialisationMotDePasseUseCase.class);

    private final UtilisateurRepository utilisateurRepository;
    private final JetonReinitialisationMotDePassePort jetonPort;
    private final EnvoiEmailIdentityPort envoiEmailIdentityPort;
    private final String urlBase;

    public DemanderReinitialisationMotDePasseUseCase(
            UtilisateurRepository utilisateurRepository,
            JetonReinitialisationMotDePassePort jetonPort,
            EnvoiEmailIdentityPort envoiEmailIdentityPort,
            @Value("${app.identity.url-base}") String urlBase) {
        this.utilisateurRepository = utilisateurRepository;
        this.jetonPort = jetonPort;
        this.envoiEmailIdentityPort = envoiEmailIdentityPort;
        this.urlBase = urlBase;
    }

    public void execute(DemanderReinitialisationMotDePasseCommand command) {
        Email email = new Email(command.email());
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email).orElse(null);

        if (utilisateur == null || !utilisateur.actif()) {
            return;
        }

        String jeton = jetonPort.genererJeton(utilisateur.id());
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
}
