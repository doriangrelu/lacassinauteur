# ADR-0008 — Anti brute-force sur l'authentification avec Bucket4j

**Statut** : Acté
**Date** : 2026-08-12

## Contexte

Le back-office est exposé publiquement (`/backoffice/connexion`). Sans limitation,
rien n'empêche une attaque par force brute sur le mot de passe des comptes `ADMIN`/
`AUTEUR`.

## Décision

Utiliser **Bucket4j** (algorithme *token bucket*) pour limiter les tentatives de
connexion :

- Un bucket par **couple IP + identifiant tenté**, capacité 5 jetons, réapprovisionné
  à raison d'1 jeton toutes les 2 minutes (soit 5 tentatives, puis 1 nouvelle toutes
  les 2 minutes) — valeurs de départ raisonnables, ajustables sans changement
  d'architecture.
- Un bucket par **IP seule**, capacité plus large (ex. 20 jetons/heure), pour limiter
  le balayage de plusieurs identifiants depuis la même IP.
- Implémenté comme un filtre Spring Security (`AuthenticationFailureHandler` /
  filtre en amont de `UsernamePasswordAuthenticationFilter`) dans
  `identity.infrastructure.security`, en mémoire (`Bucket4j` + cache local) pour la v1
  — suffisant pour un seul serveur ; à revoir (backend Redis) seulement si le site
  passe un jour en multi-instance.
- Dépassement de quota → réponse HTTP 429 avec un message générique, journalisée.

## Alternatives envisagées

- **CAPTCHA systématique** : écarté en première intention — expérience utilisateur
  dégradée pour 1-2 comptes légitimes ; le rate limiting suffit à ce niveau de risque.
  Peut être ajouté plus tard si des attaques réelles sont constatées.
- **Verrouillage de compte après N échecs** (au niveau de l'entité `Utilisateur`) :
  écarté seul — permet un déni de service trivial (verrouiller le compte de l'auteur
  en tentant des mots de passe erronés). Le rate limiting par IP est complémentaire et
  plus sûr comme première ligne de défense.

## Conséquences

- Dépendance `com.bucket4j:bucket4j-core` ajoutée au `pom.xml`.
- Le filtre doit rester dans `identity.infrastructure.security`, jamais mêlé au reste
  de `identity.infrastructure.persistence` (règle de sous-packaging de
  [architecture.md](../architecture.md#6-règle-de-sous-packaging)).
- CSRF et CORS ne sont **pas désactivés** par cette protection ; elle s'ajoute au
  filtre chain existant, cf.
  [architecture.md §9](../architecture.md#9-sécurité-spring-security).
