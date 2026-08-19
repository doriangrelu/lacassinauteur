package fr.lacassinauteur.site.shared.domain.port;

import java.util.List;

/**
 * Contribue des URL publiques au {@code sitemap.xml}.
 *
 * <p>Le sitemap doit connaître des URL issues de plusieurs domaines. Plutôt que de
 * faire dépendre {@code shared} du catalogue (interdit, cf. CLAUDE.md), chaque
 * domaine qui expose des pages <strong>pilotées par la donnée</strong> implémente
 * ce port : la dépendance va du domaine vers {@code shared}, dans le bon sens.
 *
 * <p>Les pages fixes (accueil, contact…) n'ont pas besoin de passer par ici : ce
 * sont de simples constantes de routage, sans logique métier.
 */
public interface FournisseurUrlsPubliquesPort {

    /**
     * Chemins relatifs commençant par « / », sans le nom de domaine. N'inclure que
     * des pages réellement indexables : surtout pas les pages en {@code noindex}
     * comme les fiches professionnelles (cf. ADR-0028).
     */
    List<String> urlsPubliques();
}
