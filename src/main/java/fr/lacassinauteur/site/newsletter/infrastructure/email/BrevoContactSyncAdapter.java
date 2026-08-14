package fr.lacassinauteur.site.newsletter.infrastructure.email;

import fr.lacassinauteur.site.newsletter.domain.model.AbonneNewsletter;
import fr.lacassinauteur.site.newsletter.domain.port.SynchronisationEspPort;
import fr.lacassinauteur.site.newsletter.infrastructure.email.config.BrevoNewsletterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

/**
 * Synchronise les abonnés confirmés vers une liste de contacts Brevo (cf.
 * ADR-0017) via l'API Contacts (
 * {@code POST /v3/contacts} pour ajouter/mettre à jour,
 * {@code PUT /v3/contacts/{email}} pour retirer de la liste). Thierry compose et
 * envoie ensuite ses newsletters directement depuis l'interface Brevo — ce projet
 * ne réimplémente pas d'éditeur de campagnes.
 *
 * <p><strong>Non testé contre l'API réelle</strong>, même situation que
 * {@link BrevoEmailAdapter} (cf. ADR-0013) : aucun compte Brevo disponible au
 * moment de l'implémentation. À vérifier manuellement dès que
 * {@code app.newsletter.brevo.liste-id} (variable d'environnement
 * {@code BREVO_LISTE_ID}) est renseignée avec l'identifiant réel de la liste.</p>
 */
@Component
@Profile("!dev")
public class BrevoContactSyncAdapter implements SynchronisationEspPort {

    private static final Logger LOG = LoggerFactory.getLogger(BrevoContactSyncAdapter.class);
    private static final String URI_CONTACTS = "/contacts";
    private static final String URI_CONTACT = "/contacts/{email}";

    private final RestClient brevoRestClient;
    private final BrevoNewsletterProperties proprietes;

    public BrevoContactSyncAdapter(RestClient brevoRestClient, BrevoNewsletterProperties proprietes) {
        this.brevoRestClient = brevoRestClient;
        this.proprietes = proprietes;
    }

    @Override
    public void ajouterOuMettreAJour(AbonneNewsletter abonne) {
        if (proprietes.getListeId() <= 0) {
            LOG.warn("Synchronisation Brevo ignorée pour {} : app.newsletter.brevo.liste-id non configurée",
                    abonne.email().valeur());
            return;
        }

        RequeteContact requete = new RequeteContact(
                abonne.email().valeur(), Map.of("PRENOM", abonne.prenom()), List.of(proprietes.getListeId()), true);

        try {
            brevoRestClient.post()
                    .uri(URI_CONTACTS)
                    .body(requete)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            LOG.error("Échec de la synchronisation Brevo (ajout) pour {} : {}",
                    abonne.email().valeur(), exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public void retirer(AbonneNewsletter abonne) {
        if (proprietes.getListeId() <= 0) {
            LOG.warn("Retrait Brevo ignoré pour {} : app.newsletter.brevo.liste-id non configurée", abonne.email().valeur());
            return;
        }

        RequeteRetraitListe requete = new RequeteRetraitListe(List.of(proprietes.getListeId()));

        try {
            brevoRestClient.put()
                    .uri(URI_CONTACT, abonne.email().valeur())
                    .body(requete)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            LOG.error("Échec de la synchronisation Brevo (retrait) pour {} : {}",
                    abonne.email().valeur(), exception.getMessage(), exception);
            throw exception;
        }
    }

    private record RequeteContact(String email, Map<String, String> attributes, List<Long> listIds, boolean updateEnabled) {
    }

    private record RequeteRetraitListe(List<Long> unlinkListIds) {
    }
}
