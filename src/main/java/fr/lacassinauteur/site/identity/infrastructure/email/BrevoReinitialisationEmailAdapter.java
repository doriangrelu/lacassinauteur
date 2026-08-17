package fr.lacassinauteur.site.identity.infrastructure.email;

import fr.lacassinauteur.site.identity.domain.model.Email;
import fr.lacassinauteur.site.identity.domain.port.EnvoiEmailIdentityPort;
import fr.lacassinauteur.site.identity.infrastructure.email.config.BrevoIdentityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * Adaptateur d'envoi d'emails transactionnels du domaine identity via l'API
 * Brevo (cf. ADR-0018), actif par défaut (tous profils sauf {@code dev}).
 */
@Component
@Profile("!dev")
public class BrevoReinitialisationEmailAdapter implements EnvoiEmailIdentityPort {

    private static final Logger LOG = LoggerFactory.getLogger(BrevoReinitialisationEmailAdapter.class);
    private static final String URI_ENVOI = "/smtp/email";

    private final RestClient brevoIdentityRestClient;
    private final BrevoIdentityProperties proprietes;

    public BrevoReinitialisationEmailAdapter(RestClient brevoIdentityRestClient, BrevoIdentityProperties proprietes) {
        this.brevoIdentityRestClient = brevoIdentityRestClient;
        this.proprietes = proprietes;
    }

    @Override
    public void envoyerLienReinitialisation(Email destinataire, String lienReinitialisation) {
        Contact expediteur = new Contact(proprietes.getExpediteurEmail(), proprietes.getExpediteurNom());
        RequeteEnvoiEmail requete = new RequeteEnvoiEmail(
                expediteur, List.of(new Contact(destinataire.valeur(), null)),
                ContenuEmailIdentity.SUJET_REINITIALISATION, ContenuEmailIdentity.reinitialisation(lienReinitialisation));

        try {
            brevoIdentityRestClient.post()
                    .uri(URI_ENVOI)
                    .body(requete)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            LOG.error("Échec de l'envoi de l'email de réinitialisation à {} : {}",
                    destinataire.valeur(), exception.getMessage(), exception);
            throw exception;
        }
    }

    private record Contact(String email, String name) {
    }

    private record RequeteEnvoiEmail(Contact sender, List<Contact> to, String subject, String htmlContent) {
    }
}
