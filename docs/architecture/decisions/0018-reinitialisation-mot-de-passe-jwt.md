# ADR-0018 — Réinitialisation de mot de passe : jeton JWT stateless, 15 minutes

**Statut** : Acté
**Date** : 2026-08-17

## Contexte

Le back-office n'avait jusqu'ici aucun moyen de récupérer l'accès à un compte en
cas de mot de passe oublié — seul un ADMIN déjà connecté peut créer des comptes
(`CompteController`), ce qui ne couvre pas le cas où l'unique ADMIN lui-même perd
son mot de passe. L'utilisateur (développeur principal) a explicitement demandé
un parcours « mot de passe oublié » avec lien envoyé par email, portant un jeton
JWT valide 15 minutes contenant l'identifiant du compte.

## Décision

### Jeton JWT stateless, pas de table dédiée

Le jeton de réinitialisation est un JWT signé HS256 (bibliothèque
[jjwt](https://github.com/jwtk/jjwt) 0.12.x, ajoutée en dépendance) dont :
- le sujet (`sub`) est l'id du compte (`UUID`) ;
- l'expiration (`exp`) est portée par le claim standard, fixée à 15 minutes
  (`app.identity.jwt.duree-validite-minutes`).

Aucune table `jeton_reinitialisation` en base : la vérification de signature +
expiration suffit à garantir la validité, cohérent avec la philosophie « petit
monolithe » du projet (cf. tech-stack.md) — un jeton stateless évite une table
et son nettoyage périodique pour un besoin aussi simple.

### Secret de signature via variable d'environnement

`app.identity.jwt.secret` (variable `JWT_RESET_SECRET`) suit le même principe que
les autres secrets du projet (`POSTGRES_PASSWORD`, `BREVO_API_KEY`...) : valeur
par défaut utilisable uniquement en développement local, à générer et fournir
via `.env` en production (`openssl rand -base64 48` par exemple). Un secret trop
court ferait échouer `Keys.hmacShaKeyFor` (HS256 exige au moins 256 bits).

### Ne jamais révéler si un compte existe

`DemanderReinitialisationMotDePasseUseCase` renvoie toujours le même comportement
côté visiteur (même message de confirmation), que l'email corresponde à un
compte existant/actif ou non — évite l'énumération de comptes par un tiers.
Un compte désactivé (cf. `Utilisateur.actif`) ne peut pas non plus déclencher de
réinitialisation.

### Réutilisation du compte Brevo déjà configuré et vérifié

L'envoi du lien passe par un nouvel adaptateur Brevo propre au domaine
`identity` (`BrevoReinitialisationEmailAdapter`), dupliqué depuis celui de
`newsletter` plutôt que partagé (cf. package-structure.md, « dupliquer une
petite classe plutôt que coupler deux domaines ») — mais réutilisant les mêmes
variables d'environnement (`BREVO_API_KEY`, `BREVO_EXPEDITEUR_EMAIL`/`_NOM`) :
un seul compte/expéditeur Brevo à gérer, déjà authentifié en production
(cf. correctif du 2026-08-17 sur l'inscription newsletter). Adaptateur `dev`
(`LogReinitialisationEmailAdapter`) symétrique, même patron que les autres
domaines.

### Politique de mot de passe

Minimum 10 caractères, au moins une lettre, un chiffre et un caractère spécial —
appliquée via `@Pattern` Bean Validation (`ReinitialiserMotDePasseForm` et,
par cohérence, `CreerUtilisateurForm`), pas de règle dupliquée côté domaine.

## Alternatives envisagées

- **Jeton opaque en base** (UUID stocké avec expiration, comme
  `AbonneNewsletter.jetonConfirmation`) : plus simple à révoquer individuellement,
  mais demande une table + nettoyage des jetons expirés pour un gain marginal à
  cette échelle ; le JWT stateless suffit et correspond à la demande explicite.
- **Révocation anticipée du jeton** (invalidé dès qu'il est utilisé une fois) :
  non implémentée dans cette v1 — un jeton stateless ne peut être révoqué qu'en
  invalidant tous les jetons émis (ex. rotation du secret), disproportionné ici ;
  la fenêtre de 15 minutes limite déjà fortement le risque de réutilisation.
- **Limitation du débit sur `/backoffice/mot-de-passe-oublie`** : pas de
  rate-limiting dédié dans cette passe (contrairement à `/backoffice/connexion`,
  protégé par `LoginRateLimiter`) — à envisager en v2 si l'endpoint est exposé à
  un abus réel (mail-bombing d'une victime), risque jugé faible pour un unique
  compte ADMIN/AUTEUR sur un petit site.

## Conséquences

- Nouvelle dépendance : `io.jsonwebtoken:jjwt-api/impl/jackson` 0.12.6.
- Nouvelle variable d'environnement à fournir en production : `JWT_RESET_SECRET`.
- Aucune migration Flyway nécessaire (aucune nouvelle table).
