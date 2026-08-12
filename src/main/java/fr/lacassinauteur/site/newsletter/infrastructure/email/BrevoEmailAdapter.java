package fr.lacassinauteur.site.newsletter.infrastructure.email;

import fr.lacassinauteur.site.newsletter.domain.model.Email;
import fr.lacassinauteur.site.newsletter.domain.port.EnvoiEmailPort;
import fr.lacassinauteur.site.newsletter.infrastructure.email.config.BrevoNewsletterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * Adaptateur d'envoi d'emails transactionnels via l'API Brevo (cf. ADR-0002,
 * ADR-0013), actif par défaut (tous profils sauf {@code dev}).
 *
 * <p><strong>Non testé contre l'API réelle</strong> : implémenté d'après la
 * documentation publique de l'API transactionnelle Brevo
 * ({@code POST https://api.brevo.com/v3/smtp/email}), mais aucun compte Brevo ni clé
 * API n'était disponible au moment de l'implémentation (création de compte externe
 * hors périmètre d'un agent automatisé). À vérifier manuellement dès que
 * {@code app.newsletter.brevo.api-key} (variable d'environnement
 * {@code BREVO_API_KEY}) est renseignée avec une vraie clé — cf. ADR-0013 et
 * docs/roadmap.md (Phase 4).</p>
 */
@Component
@Profile("!dev")
public class BrevoEmailAdapter implements EnvoiEmailPort {

    private static final Logger LOG = LoggerFactory.getLogger(BrevoEmailAdapter.class);
    private static final String URI_ENVOI = "/smtp/email";

    private final RestClient brevoRestClient;
    private final BrevoNewsletterProperties proprietes;

    public BrevoEmailAdapter(RestClient brevoRestClient, BrevoNewsletterProperties proprietes) {
        this.brevoRestClient = brevoRestClient;
        this.proprietes = proprietes;
    }

    @Override
    public void envoyerEmailConfirmation(Email destinataire, String prenom, String lienConfirmation) {
        envoyer(destinataire, prenom, ContenuEmailNewsletter.SUJET_CONFIRMATION,
                ContenuEmailNewsletter.confirmation(prenom, lienConfirmation));
    }

    @Override
    public void envoyerEmailBienvenue(Email destinataire, String prenom, String lienDesinscription) {
        envoyer(destinataire, prenom, ContenuEmailNewsletter.SUJET_BIENVENUE,
                ContenuEmailNewsletter.bienvenue(prenom, lienDesinscription));
    }

    private void envoyer(Email destinataire, String prenom, String sujet, String contenuHtml) {
        Contact expediteur = new Contact(proprietes.getExpediteurEmail(), proprietes.getExpediteurNom());
        RequeteEnvoiEmail requete = new RequeteEnvoiEmail(
                expediteur, List.of(new Contact(destinataire.valeur(), prenom)), sujet, contenuHtml);

        try {
            brevoRestClient.post()
                    .uri(URI_ENVOI)
                    .body(requete)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            LOG.error("Échec de l'envoi d'email transactionnel Brevo à {} : {}",
                    destinataire.valeur(), exception.getMessage(), exception);
            throw exception;
        }
    }

    private record Contact(String email, String name) {
    }

    private record RequeteEnvoiEmail(Contact sender, List<Contact> to, String subject, String htmlContent) {
    }
}
