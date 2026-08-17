package fr.lacassinauteur.site.identity.infrastructure.security;

import fr.lacassinauteur.site.identity.domain.exception.JetonReinitialisationInvalideException;
import fr.lacassinauteur.site.identity.domain.port.JetonReinitialisationMotDePassePort;
import fr.lacassinauteur.site.identity.infrastructure.security.config.JwtReinitialisationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Jeton de réinitialisation de mot de passe sous forme de JWT signé HS256 (cf.
 * ADR-0018) : le sujet du jeton est l'id du compte, sa propre expiration (15 min
 * par défaut) est portée dans le claim standard {@code exp} — aucune table de
 * jetons en base, la vérification de signature + expiration suffit.
 */
@Component
@EnableConfigurationProperties(JwtReinitialisationProperties.class)
public class JwtJetonReinitialisationAdapter implements JetonReinitialisationMotDePassePort {

    private final SecretKey cle;
    private final Duration duree;

    public JwtJetonReinitialisationAdapter(JwtReinitialisationProperties proprietes) {
        this.cle = Keys.hmacShaKeyFor(proprietes.getSecret().getBytes(StandardCharsets.UTF_8));
        this.duree = Duration.ofMinutes(proprietes.getDureeValiditeMinutes());
    }

    @Override
    public String genererJeton(UUID utilisateurId) {
        Instant maintenant = Instant.now();
        return Jwts.builder()
                .subject(utilisateurId.toString())
                .issuedAt(Date.from(maintenant))
                .expiration(Date.from(maintenant.plus(duree)))
                .signWith(cle)
                .compact();
    }

    @Override
    public UUID validerEtExtraireUtilisateurId(String jeton) {
        try {
            Claims claims = Jwts.parser().verifyWith(cle).build().parseSignedClaims(jeton).getPayload();
            return UUID.fromString(claims.getSubject());
        } catch (JwtException | IllegalArgumentException exception) {
            throw new JetonReinitialisationInvalideException("Jeton de réinitialisation invalide ou expiré");
        }
    }
}
