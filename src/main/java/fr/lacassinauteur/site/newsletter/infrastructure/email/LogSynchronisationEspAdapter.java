package fr.lacassinauteur.site.newsletter.infrastructure.email;

import fr.lacassinauteur.site.newsletter.domain.model.AbonneNewsletter;
import fr.lacassinauteur.site.newsletter.domain.port.SynchronisationEspPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Adaptateur de développement : ne contacte pas l'API Brevo, se contente de
 * logguer la synchronisation. Évite de dépendre d'une clé API/liste Brevo réelle
 * pour tester le parcours d'inscription en local.
 */
@Component
@Profile("dev")
public class LogSynchronisationEspAdapter implements SynchronisationEspPort {

    private static final Logger LOG = LoggerFactory.getLogger(LogSynchronisationEspAdapter.class);

    @Override
    public void ajouterOuMettreAJour(AbonneNewsletter abonne) {
        LOG.info("[newsletter/dev] Synchronisation Brevo (ajout) : {} <{}>", abonne.prenom(), abonne.email().valeur());
    }

    @Override
    public void retirer(AbonneNewsletter abonne) {
        LOG.info("[newsletter/dev] Synchronisation Brevo (retrait) : {}", abonne.email().valeur());
    }
}
