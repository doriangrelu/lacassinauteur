package fr.lacassinauteur.site.identity.domain.port;

import java.util.UUID;

/**
 * Génération et validation cryptographique du jeton (JWT) de réinitialisation de
 * mot de passe — cf. ADR-0018/ADR-0020. Ce port ne connaît que la forme du jeton
 * (signature, expiration, contenu) ; le caractère « à usage unique » est assuré
 * séparément par {@link JetonReinitialisationRepository} (usage applicatif, pas
 * cryptographique).
 */
public interface JetonReinitialisationMotDePassePort {

    /** {@code jetonId} est le {@code jti} du JWT, choisi par l'appelant (clé de la table dédiée). */
    String genererJeton(UUID utilisateurId, UUID jetonId);

    /**
     * @throws fr.lacassinauteur.site.identity.domain.exception.JetonReinitialisationInvalideException
     *         si le jeton est malformé, expiré, ou de signature invalide.
     */
    JetonDecode validerEtExtraire(String jeton);

    record JetonDecode(UUID utilisateurId, UUID jetonId) {
    }
}
