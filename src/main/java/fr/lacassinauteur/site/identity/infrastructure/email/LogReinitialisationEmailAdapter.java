package fr.lacassinauteur.site.identity.infrastructure.email;

import fr.lacassinauteur.site.identity.domain.model.Email;
import fr.lacassinauteur.site.identity.domain.port.EnvoiEmailIdentityPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Adaptateur de développement : n'envoie aucun email réel, se contente de logguer
 * le lien de réinitialisation — permet de tester le parcours complet en local
 * sans compte Brevo, cf. ADR-0018.
 */
@Component
@Profile("dev")
public class LogReinitialisationEmailAdapter implements EnvoiEmailIdentityPort {

    private static final Logger LOG = LoggerFactory.getLogger(LogReinitialisationEmailAdapter.class);

    @Override
    public void envoyerLienReinitialisation(Email destinataire, String lienReinitialisation) {
        LOG.info("""
                [identity/dev] Email de réinitialisation de mot de passe à {}
                Sujet : {}
                Lien de réinitialisation : {}""",
                destinataire.valeur(), ContenuEmailIdentity.SUJET_REINITIALISATION, lienReinitialisation);
    }
}
