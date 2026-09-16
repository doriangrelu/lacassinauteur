package fr.lacassinauteur.site.identity.presentation;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

/**
 * Expose l'URL de l'Account Console Keycloak à toutes les pages back-office (lien
 * "Mon compte" du topbar) — la gestion du compte (mot de passe, etc.) n'existe
 * plus dans ce dépôt, cf. ADR-0033. {@code referrer}/{@code referrer_uri}
 * permettent à Keycloak d'afficher un lien de retour vers le site ; Keycloak ne
 * l'affiche que si {@code referrer_uri} figure dans les Redirect URI valides du
 * client (cf. mode-operatoire-deploiement.md).
 *
 * <p>Non restreint par package (même patron que {@code GestionnaireErreursGlobal}) :
 * l'attribut n'a de sens que sur les pages back-office, d'où le filtrage sur le
 * chemin de la requête plutôt qu'une portée Spring plus fine.
 */
@ControllerAdvice
public class CompteKeycloakModelAttributeAdvice {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final String urlBase;

    public CompteKeycloakModelAttributeAdvice(
            ClientRegistrationRepository clientRegistrationRepository,
            @Value("${app.site.url-base}") String urlBase) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.urlBase = urlBase;
    }

    @ModelAttribute("urlCompteKeycloak")
    public String urlCompteKeycloak(HttpServletRequest requete) {
        if (!requete.getRequestURI().startsWith("/backoffice")) {
            return null;
        }

        ClientRegistration inscription = clientRegistrationRepository.findByRegistrationId("keycloak");
        String issuer = inscription.getProviderDetails().getIssuerUri();
        String retour = urlBase + "/backoffice/univers";

        return issuer + "/account?referrer=" + inscription.getClientId()
                + "&referrer_uri=" + URLEncoder.encode(retour, StandardCharsets.UTF_8);
    }
}
