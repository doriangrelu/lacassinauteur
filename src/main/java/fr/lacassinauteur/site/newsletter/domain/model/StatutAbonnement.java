package fr.lacassinauteur.site.newsletter.domain.model;

/**
 * Statut de consentement d'un abonné à la newsletter (double opt-in, cf.
 * ADR-0013) :
 * <ul>
 *     <li>{@link #EN_ATTENTE_CONFIRMATION} : inscription initiée, email de
 *     confirmation envoyé, lien pas encore cliqué.</li>
 *     <li>{@link #CONFIRME} : lien de confirmation cliqué, l'abonné reçoit
 *     effectivement la newsletter.</li>
 *     <li>{@link #DESINSCRIT} : désinscription volontaire via le lien présent
 *     dans les envois.</li>
 * </ul>
 */
public enum StatutAbonnement {
    EN_ATTENTE_CONFIRMATION,
    CONFIRME,
    DESINSCRIT
}
