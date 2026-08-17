package fr.lacassinauteur.site.shared.infrastructure.captcha;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration reCAPTCHA v3 (cf. ADR-0019). {@code secretKey} n'est jamais
 * commise ; {@code siteKey} est publique par nature (exposée côté navigateur),
 * peut être commise sans risque mais reste fournie en variable d'environnement
 * pour rester alignée avec {@code secretKey} au même endroit.
 */
@ConfigurationProperties(prefix = "app.captcha")
public class RecaptchaProperties {

    private String siteKey = "";

    private String secretKey = "";

    private double seuilScore = 0.5;

    public String getSiteKey() {
        return siteKey;
    }

    public void setSiteKey(String siteKey) {
        this.siteKey = siteKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public double getSeuilScore() {
        return seuilScore;
    }

    public void setSeuilScore(double seuilScore) {
        this.seuilScore = seuilScore;
    }
}
