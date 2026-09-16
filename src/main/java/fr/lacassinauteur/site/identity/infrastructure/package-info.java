/**
 * Intégration OIDC avec Keycloak : plus aucune gestion de comptes maison, ce
 * domaine se limite à traduire un utilisateur authentifié par Keycloak en
 * principal Spring Security, autorité d'accès au back-office comprise (rôle
 * client, lu par introspection du jeton d'accès opaque, jamais via userinfo).
 * Voir docs/architecture/decisions/0033-sso-keycloak-backoffice.md.
 */
package fr.lacassinauteur.site.identity.infrastructure;
