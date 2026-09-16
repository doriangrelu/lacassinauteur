# ADR-0033 — Bascule du back-office vers Keycloak (SSO OIDC), suppression des comptes maison

**Statut** : Acté et implémenté (branche `feature/sso-keycloak`), vérifié de
bout en bout sur un royaume de test. Non encore déployé en production.
**Date** : 2026-09-16

## Contexte

`docs/roadmap.md` listait cette bascule comme une **décision en attente** :
remplacer l'authentification maison du domaine `identity` (comptes,
mots de passe, rôles `ADMIN`/`AUTEUR`) par Keycloak (SSO), déjà en place pour
`iabilis.fr` depuis [ADR-0027](0027-keycloak-iam.md), ou faire cohabiter les
deux. L'utilisateur a tranché : **débrancher entièrement** la gestion des
utilisateurs du site, Keycloak devient l'unique source de vérité pour
l'identité et les mots de passe.

## Décision

### Client OAuth2.1 confidentiel + PKCE, jamais l'endpoint userinfo

Le site devient un client OIDC (Authorization Code + PKCE) du royaume
`thierrylacassin-auteur`. Deux points imposés explicitement par
l'utilisateur, au-delà de la conformité OIDC de base :

- **PKCE (S256) même si le client est confidentiel.** Spring Security
  n'active PKCE automatiquement que pour un client public
  (`client-authentication-method: none`) ; il faut forcer
  `OAuth2AuthorizationRequestCustomizers.withPkce()` sur le resolver pour un
  client confidentiel. Le client reste confidentiel (secret gardé côté
  serveur, comme `BREVO_API_KEY`) — PKCE est une défense en profondeur
  supplémentaire contre le vol de code d'autorisation, conforme à OAuth 2.1
  qui l'exige pour tout type de client.
- **Jamais l'endpoint userinfo.** L'utilisateur a explicitement écarté cette
  voie, au profit de l'**introspection RFC 7662** du jeton d'accès comme
  seule source de vérité pour l'autorisation — y compris pour un jeton dont
  le format s'avère être un JWT signé en pratique (constaté en test) : le
  site ne le décode jamais lui-même, il interroge systématiquement
  Keycloak. `KeycloakIntrospectionOidcUserService` construit le principal à
  partir du seul jeton ID (toujours un JWT en OIDC), et détermine les
  autorités Spring Security via une requête d'introspection distincte sur le
  jeton d'accès.

### Rôle realm, pas rôle client — décision explicite de l'utilisateur

Proposition initiale (discutée avant implémentation) : un rôle **client**
dédié, plus proche du principe de moindre portée (un rôle qui n'a de sens que
pour cette application précise ne devrait pas être un concept realm-wide).
L'utilisateur a tranché autrement une fois le royaume de test en main : un
**rôle realm** `AUTEUR`, lu dans le claim `realm_access.roles` de la réponse
d'introspection — comportement par défaut de Keycloak (mapper "roles"
standard), sans mapper dédié à configurer. Confirmé explicitement pour la
production aussi (« On garde ce comportement pour la prod »), pas seulement
pour le royaume de test.

`/backoffice/**` exige donc `hasAuthority("AUTEUR")` — pas de distinction
`ADMIN`/`AUTEUR` côté site : Thierry n'étant pas administrateur Keycloak, la
gestion de rôles applicatifs n'a plus lieu d'être dans ce dépôt.

### Deux clients Keycloak, jamais un seul pour dev et prod

Signalé par l'utilisateur avant implémentation : enregistrer un Redirect URI
`localhost` sur le client qui sert aussi le site public serait une faille —
n'importe quel processus local de la machine visée pourrait intercepter un
code d'autorisation destiné à ce domaine. D'où deux clients Keycloak
distincts (`mybook-backoffice` en prod, `mybook-backoffice-local` en dev),
chacun avec son propre secret et son unique Redirect URI, jamais les deux
URIs sur le même client. Cf.
[mode-operatoire-deploiement.md §13](../../mode-operatoire-deploiement.md).

### Gestion du compte : entièrement déléguée à l'Account Console Keycloak

Le topbar back-office expose un lien « Mon compte » vers
`{issuer}/account?referrer={client_id}&referrer_uri={retour}`
(`CompteKeycloakModelAttributeAdvice`). Keycloak n'affiche un lien de retour
vers le site que si `referrer_uri` figure dans les Redirect URIs valides du
client — vérifié en test (lien « Back to backend-for-frontend » présent,
retour exact sur `/backoffice/univers`). Conséquence : `reset_password_allowed`
doit repasser à `true` sur le royaume de prod (actuellement `false`, posé en
défense en profondeur lors de l'audit CVE-2026-18963, cf. `roadmap.md`), sans
quoi le changement de mot de passe en self-service ne fonctionne pas.

### Déconnexion RP-Initiated

`OidcClientInitiatedLogoutSuccessHandler` : la déconnexion ferme aussi la
session Keycloak (pas seulement la session Spring), pour qu'une reconnexion
immédiate après « déconnexion » redemande bien des identifiants.

## Suppression complète du domaine `identity` maison

63 fichiers de production et 15 tests supprimés : comptes (`Utilisateur`,
`Role`, écran `/backoffice/comptes` réservé `ADMIN`), réinitialisation de mot
de passe (JWT + email Brevo, ADR-0018), rate-limiting anti brute-force
Bucket4j (ADR-0008 — Keycloak a sa propre protection, à vérifier/activer côté
royaume). `identity` ne porte plus aucune logique métier : uniquement
l'intégration OIDC (`infrastructure.security`) et l'exposition du lien
Account Console (`presentation`). Migration Flyway `V13` : `DROP TABLE
jeton_reinitialisation, utilisateur`.

Dépendances retirées du `pom.xml` : `jjwt-*` (plus d'usage après suppression
du JWT de réinitialisation), `bucket4j_jdk17-core`. Ajoutée :
`spring-boot-starter-oauth2-client`.

## Bug rencontré en vérification : audience manquante

Le premier test réel a échoué : jeton d'accès valide et non expiré (vérifié
en le décodant), mais l'introspection répondait invariablement
`{"active": false}` — reproduit hors application (curl direct avec les
identifiants du client, deux méthodes d'authentification testées) pour
écarter un bug côté code avant de creuser côté royaume. Cause : Keycloak
inclut par défaut `"aud": "account"` sur le jeton d'accès, jamais
l'identifiant du client, tant qu'aucun mapper d'audience n'est configuré —
l'introspection considère alors le jeton comme n'appartenant pas au client
qui interroge. Corrigé par l'utilisateur en ajoutant un mapper d'audience
sur le client (cf. mode-operatoire-deploiement.md §13c). **Leçon retenue** :
documenté explicitement dans le mode opératoire pour ne pas reperdre le temps
de diagnostic au prochain environnement.

## Découverte incidente : migration Jackson 2 → 3 dans Spring Boot 4.1

Sans rapport avec Keycloak, notée en marge du diagnostic : `com.fasterxml.
jackson.databind.ObjectMapper` n'est plus sur le classpath de ce projet.
Spring Boot 4.1 embarque désormais Jackson 3 sous le nouveau namespace
`tools.jackson.databind` (confirmé via `mvn dependency:tree` :
`tools.jackson.core:jackson-databind:3.1.4`, `jackson-annotations` restant
seul sous l'ancien `com.fasterxml.jackson.core` pour compatibilité). Pas
d'usage direct de Jackson dans ce dépôt aujourd'hui — à garder en tête si un
jour un accès manuel à `ObjectMapper` devient nécessaire.

## Alternatives envisagées

- **Rôle client plutôt que rôle realm** : ma recommandation initiale,
  écartée par l'utilisateur en connaissance de cause (cf. ci-dessus) —
  confirmée pour prod également, pas seulement pour le test.
- **Client public + PKCE seul (sans secret)** : écarté, le back-office est
  une application serveur qui peut garder un secret en sécurité ; renoncer
  au secret aurait été un abandon de garantie sans bénéfice réel.
- **Un seul client Keycloak pour dev et prod** : écarté pour la raison de
  sécurité détaillée plus haut (Redirect URI `localhost` sur un client de
  prod).
- **UserInfo endpoint pour les claims/rôles** : écarté explicitement par
  l'utilisateur au profit de l'introspection systématique du jeton d'accès.
- **Garder une distinction `ADMIN`/`AUTEUR` côté site** : écartée, Thierry
  n'étant pas administrateur Keycloak, cette granularité n'a plus d'usage
  dans ce dépôt.

## Conséquences

- `pom.xml`, `SecurityConfig`, nouveau `identity.infrastructure.security`
  (`KeycloakIntrospectionOidcUserService`, `KeycloakIntrospectionClientConfig`),
  nouveau `identity.presentation` (`CompteKeycloakModelAttributeAdvice`).
- `application.yml`/`.env.example` : `KEYCLOAK_ISSUER_URI`/`CLIENT_ID`/
  `CLIENT_SECRET`, plus plus aucune variable `JWT_RESET_SECRET`.
- Templates back-office (`topbar.html`, `sidebar.html`,
  `layout-backoffice.html`) : email via `sec:authentication`, lien « Mon
  compte », suppression du menu « Comptes ».
- `CLAUDE.md`, `docs/architecture/tech-stack.md`,
  `docs/architecture/domain-model.md`,
  `docs/architecture/package-structure.md`, `docs/roadmap.md` mis à jour en
  cohérence (cf. commits associés).
- **Vérifié en test** (royaume `thierrylacassin-auteur-qua`, client
  `backend-for-frontend`) : connexion, PKCE, introspection avec la bonne
  audience, accès `/backoffice/univers`, aller-retour Account Console.
- **Non vérifié** : le clic sur « Se déconnecter » depuis l'outil de test
  automatisé échoue systématiquement à compléter la redirection
  (`ERR_ABORTED` côté navigateur piloté) sans invalider la session — la
  déconnexion RP-Initiated fonctionne en revanche parfaitement en déclenchant
  l'URL Keycloak directement (testé manuellement). Probable artefact de
  l'automatisation face à une redirection cross-origin déclenchée par un
  POST, plutôt qu'un bug applicatif, mais à confirmer avec un clic réel dans
  un navigateur avant de considérer le parcours complet.
- **Non fait dans cette passe** : création du client Keycloak de production
  (`mybook-backoffice`), passage de `reset_password_allowed` à `true` sur le
  royaume de prod, vérification/activation de la protection brute-force
  Keycloak (remplace Bucket4j), déploiement, tests unitaires/intégration du
  nouveau service d'authentification, commit du travail (encore local sur la
  branche `feature/sso-keycloak`).
