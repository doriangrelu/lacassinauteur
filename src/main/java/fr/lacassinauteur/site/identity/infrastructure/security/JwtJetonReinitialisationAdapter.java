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
 * ADR-0018) : le sujet est l'id du compte, l'identifiant standard {@code jti}
 * porte l'id de la ligne {@code JetonReinitialisation} en base (cf. ADR-0020,
 * usage unique), l'expiration (15 min par défaut) est portée par le claim
 * standard {@code exp}.
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
    public String genererJeton(UUID utilisateurId, UUID jetonId) {
        Instant maintenant = Instant.now();
        return Jwts.builder()
                .subject(utilisateurId.toString())
                .id(jetonId.toString())
                .issuedAt(Date.from(maintenant))
                .expiration(Date.from(maintenant.plus(duree)))
                .signWith(cle)
                .compact();
    }

    @Override
    public JetonDecode validerEtExtraire(String jeton) {
        try {
            Claims claims = Jwts.parser().verifyWith(cle).build().parseSignedClaims(jeton).getPayload();
            return new JetonDecode(UUID.fromString(claims.getSubject()), UUID.fromString(claims.getId()));
        } catch (JwtException | IllegalArgumentException exception) {
            throw new JetonReinitialisationInvalideException("Jeton de réinitialisation invalide ou expiré");
        }
    }
}
