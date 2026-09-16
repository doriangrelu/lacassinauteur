-- Bascule de l'authentification back-office vers Keycloak (OIDC) : la gestion de
-- comptes maison n'a plus de raison d'être, Keycloak est désormais l'unique source
-- de vérité pour l'identité et les mots de passe. Cf. docs/architecture/decisions/
-- 0033-sso-keycloak-backoffice.md (à rédiger).
--
-- jeton_reinitialisation référence utilisateur (FK) : suppression dans cet ordre.
DROP TABLE jeton_reinitialisation;
DROP TABLE utilisateur;
