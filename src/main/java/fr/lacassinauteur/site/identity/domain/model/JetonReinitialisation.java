package fr.lacassinauteur.site.identity.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Trace en base d'un jeton de réinitialisation de mot de passe émis (cf.
 * ADR-0020) : permet d'imposer un seul jeton actif à la fois par compte, et de
 * dé-référencer (supprimer) le jeton dès qu'il est utilisé — un JWT seul,
 * stateless, ne peut pas être « brûlé » avant sa propre expiration.
 */
public record JetonReinitialisation(UUID id, UUID utilisateurId, String jeton, Instant dateExpiration) {

    public boolean expire() {
        return dateExpiration.isBefore(Instant.now());
    }
}
