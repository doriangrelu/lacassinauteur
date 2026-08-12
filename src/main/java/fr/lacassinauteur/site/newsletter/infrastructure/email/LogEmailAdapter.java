package fr.lacassinauteur.site.newsletter.infrastructure.email;

import fr.lacassinauteur.site.newsletter.domain.model.Email;
import fr.lacassinauteur.site.newsletter.domain.port.EnvoiEmailPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Adaptateur de développement : n'envoie aucun email réel, se contente de logguer le
 * contenu (sujet + lien). Permet de tester le parcours complet inscription →
 * confirmation → désinscription en local sans compte Brevo ni clé API, cf.
 * ADR-0013.
 */
@Component
@Profile("dev")
public class LogEmailAdapter implements EnvoiEmailPort {

    private static final Logger LOG = LoggerFactory.getLogger(LogEmailAdapter.class);

    @Override
    public void envoyerEmailConfirmation(Email destinataire, String prenom, String lienConfirmation) {
        LOG.info("""
                [newsletter/dev] Email de confirmation à {} <{}>
                Sujet : {}
                Lien de confirmation : {}""",
                prenom, destinataire.valeur(), ContenuEmailNewsletter.SUJET_CONFIRMATION, lienConfirmation);
    }

    @Override
    public void envoyerEmailBienvenue(Email destinataire, String prenom, String lienDesinscription) {
        LOG.info("""
                [newsletter/dev] Email de bienvenue à {} <{}>
                Sujet : {}
                Lien de désinscription : {}""",
                prenom, destinataire.valeur(), ContenuEmailNewsletter.SUJET_BIENVENUE, lienDesinscription);
    }
}
