package fr.lacassinauteur.site.shared.infrastructure.captcha;

import fr.lacassinauteur.site.shared.domain.port.CaptchaPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Adaptateur de développement : accepte toujours la soumission, sans appeler
 * l'API Google — permet de tester les formulaires publics en local sans clés
 * reCAPTCHA (cf. ADR-0019, même principe que {@code LogEmailAdapter} pour les
 * emails).
 */
@Component
@Profile("dev")
public class NoopCaptchaAdapter implements CaptchaPort {

    @Override
    public boolean verifier(String jeton) {
        return true;
    }
}
