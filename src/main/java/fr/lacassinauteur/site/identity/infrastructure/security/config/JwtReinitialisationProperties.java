package fr.lacassinauteur.site.identity.infrastructure.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration du jeton JWT de réinitialisation de mot de passe (cf. ADR-0018).
 * {@code secret} est lu depuis la variable d'environnement
 * {@code JWT_RESET_SECRET} (jamais commitée) — valeur par défaut ci-dessous
 * utilisable uniquement en développement local, à toujours surcharger en
 * production.
 */
@ConfigurationProperties(prefix = "app.identity.jwt")
public class JwtReinitialisationProperties {

    private String secret = "dev-uniquement-a-remplacer-en-prod-min-32-caracteres-svp";

    private long dureeValiditeMinutes = 15;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getDureeValiditeMinutes() {
        return dureeValiditeMinutes;
    }

    public void setDureeValiditeMinutes(long dureeValiditeMinutes) {
        this.dureeValiditeMinutes = dureeValiditeMinutes;
    }
}
