package fr.lacassinauteur.site.shared.web;

import org.springframework.util.StringUtils;

/**
 * Vérifie le champ honeypot anti-spam commun aux formulaires publics (newsletter,
 * contact) : un champ masqué en CSS, jamais rempli par un visiteur humain. Capacité
 * technique transverse (pas de logique métier), donc légitime dans `shared`.
 */
public final class HoneypotAntiSpam {

    private HoneypotAntiSpam() {
    }

    public static boolean estRempli(String valeurChampPiege) {
        return StringUtils.hasText(valeurChampPiege);
    }
}
