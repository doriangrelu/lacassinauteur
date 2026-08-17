package fr.lacassinauteur.site.identity.domain.port;

import java.util.UUID;

/**
 * Génération et validation du jeton (JWT) de réinitialisation de mot de passe —
 * cf. ADR-0018. Stateless : aucune table dédiée, le jeton porte lui-même
 * l'identifiant du compte et sa propre expiration.
 */
public interface JetonReinitialisationMotDePassePort {

    String genererJeton(UUID utilisateurId);

    /**
     * @throws fr.lacassinauteur.site.identity.domain.exception.JetonReinitialisationInvalideException
     *         si le jeton est malformé, expiré, ou de signature invalide.
     */
    UUID validerEtExtraireUtilisateurId(String jeton);
}
