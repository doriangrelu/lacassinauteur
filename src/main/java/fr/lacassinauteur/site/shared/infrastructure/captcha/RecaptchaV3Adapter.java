package fr.lacassinauteur.site.shared.infrastructure.captcha;

import fr.lacassinauteur.site.shared.domain.port.CaptchaPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Vérifie un jeton reCAPTCHA v3 auprès de l'API Google
 * ({@code POST https://www.google.com/recaptcha/api/siteverify}). En cas de
 * jeton absent, d'échec API ou de score sous le seuil configuré, la vérification
 * échoue (fermé par défaut) — le contrôleur appelant traite alors la soumission
 * comme du spam, même comportement que le honeypot (cf. {@code HoneypotAntiSpam}).
 */
@Component
@Profile("!dev")
@EnableConfigurationProperties(RecaptchaProperties.class)
public class RecaptchaV3Adapter implements CaptchaPort {

    private static final Logger LOG = LoggerFactory.getLogger(RecaptchaV3Adapter.class);
    private static final String URI_VERIFICATION = "https://www.google.com/recaptcha/api/siteverify";

    private final RestClient restClient;
    private final RecaptchaProperties proprietes;

    public RecaptchaV3Adapter(RecaptchaProperties proprietes) {
        this.proprietes = proprietes;
        this.restClient = RestClient.create();
    }

    @Override
    public boolean verifier(String jeton) {
        if (!StringUtils.hasText(jeton) || !StringUtils.hasText(proprietes.getSecretKey())) {
            return false;
        }

        MultiValueMap<String, String> corps = new LinkedMultiValueMap<>();
        corps.add("secret", proprietes.getSecretKey());
        corps.add("response", jeton);

        try {
            ReponseVerification reponse = restClient.post()
                    .uri(URI_VERIFICATION)
                    .body(corps)
                    .retrieve()
                    .body(ReponseVerification.class);

            return reponse != null && reponse.success() && reponse.score() >= proprietes.getSeuilScore();
        } catch (RestClientException exception) {
            LOG.warn("Échec de la vérification reCAPTCHA (API indisponible) : {}", exception.getMessage());
            return false;
        }
    }

    private record ReponseVerification(boolean success, double score, String action) {
    }
}
