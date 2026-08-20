# ADR-0031 — Thème Keycloak : tentative `shadcn-theme`, puis retour au thème natif

**Statut** : **Annulé** — `shadcn-theme` a été déployé puis retiré le jour même.
Les deux royaumes utilisent désormais le thème natif `keycloak.v2`. Remplace la
section « thème » d'[ADR-0027](0027-keycloak-iam.md).
**Date** : 2026-08-20

> **Conclusion en une ligne** : aucun thème personnalisé n'est en place. Le
> chemin parcouru est conservé ci-dessous parce qu'il documente *pourquoi*, et
> surtout un critère de choix qu'on n'avait pas vu venir : la couverture
> fonctionnelle des parcours d'authentification.

## Contexte

Le thème maison `lacassin-boat` (ADR-0027) — un simple `parent=keycloak.v2` avec
un logo bateau en SVG et quelques règles CSS — n'a pas convaincu l'utilisateur,
qui a demandé un thème « plus moderne, simple et joli », en citant **Keywind**.

## Décision

### Keywind écarté malgré la demande explicite

Keywind est un thème Tailwind + Alpine.js réputé, mais sa **dernière version
date d'avril 2024**, pour un serveur en Keycloak 26.7. Keywind embarque ses
propres gabarits FreeMarker, dont l'API a sensiblement changé entre Keycloak 23
et 26 : le risque de casser la page de connexion était réel et non vérifiable
autrement qu'en le déployant.

Le point a été signalé à l'utilisateur **avant** toute installation, qui a
décidé de renoncer. Le JAR téléchargé a été supprimé sans être installé.

### `shadcn-theme` retenu

[keycloak-shadcn-theme](https://github.com/ThilinaTLM/keycloak-shadcn-theme),
même registre esthétique (Tailwind, sobre) mais :

| | Keywind | shadcn-theme |
|---|---|---|
| Dernière version | avril 2024 | **novembre 2025** |
| Compatible Keycloak 26 | non annoncé | **oui**, construit avec Keycloakify v11 |
| Licence | Apache-2.0 | MIT |

Le critère décisif est **Keycloakify v11**, dont la documentation confirme
explicitement la prise en charge de Keycloak 26 : c'est précisément la garantie
qui manquait à Keywind.

Distribué en JAR, déposé dans `~/keycloak/providers/` et monté dans le
conteneur. Keycloak ré-augmente automatiquement son image au démarrage quand ce
dossier change — aucun `kc.sh build` manuel.

### Déploiement progressif, royaume `master` en dernier

Le thème a d'abord été activé **uniquement sur le royaume
`thierrylacassin-auteur`**, en laissant `master` sur le thème d'origine : si le
rendu avait cassé, la console d'administration serait restée accessible pour
revenir en arrière. Ce n'est qu'après validation visuelle par l'utilisateur que
`master` a été basculé à son tour, à sa demande — la page de connexion de la
console d'administration ne révèle donc plus Keycloak non plus.

**Portée réelle** : `shadcn-theme` ne fournit qu'un thème de type `login`. Il
habille la *page de connexion* de la console d'administration, pas l'interface
de la console elle-même, qui reste celle de Keycloak.

L'activation s'est faite **en base** (`realm.login_theme`) plutôt que via
`kcadm`, l'utilisateur ayant entre-temps changé le mot de passe administrateur
— ce qui était la bonne pratique à suivre. Un redémarrage vide le cache de
configuration et rend le changement effectif.

## Alternatives envisagées

- **Keywind** : écarté, cf. ci-dessus.
- **Garder `lacassin-boat`** : écarté, l'utilisateur n'en voulait pas.
- **Thème par défaut de Keycloak** : écarté — il affiche la marque Keycloak, à
  rebours de l'objectif initial (ADR-0027).
- **Construire un thème avec Keycloakify nous-mêmes** : écarté — imposerait une
  chaîne Node/React à maintenir sur le VPS pour un résultat équivalent à un
  thème tiers déjà packagé.

## Conséquences (état final)

- **Aucun thème personnalisé** : les deux royaumes sont sur `keycloak.v2`, le
  thème natif de Keycloak 26. Passkeys fonctionnels sur `master` (10 marqueurs).
- `~/keycloak/providers/` et le JAR sont supprimés, le montage retiré du compose.
- L'ancien thème maison `lacassin-boat` est supprimé lui aussi : personne ne
  l'utilisait et l'utilisateur n'en voulait pas.
- `~/keycloak/themes/` est désormais vide, le montage est conservé pour un futur
  thème.
- Le retour arrière s'est fait **en base** (`realm.login_theme`) plus
  redémarrage, sans passer par la console — utile à savoir si un thème casse un
  jour la page de connexion au point de rendre l'administration inaccessible.

## Annulation : `shadcn-theme` supprime les passkeys

Après déploiement, l'utilisateur a constaté que **l'option passkey avait disparu**
de la page de connexion. Mesure comparative sur la même page :

| Thème | Marqueurs `passkey` / `webauthn` dans la page |
|---|---|
| `keycloak.v2` (natif) | **10** |
| `shadcn-theme` | **0** |

Zéro — pas même dans le contexte Keycloakify sérialisé. Le thème ne stylise donc
pas mal ce parcours : **il ne l'implémente pas du tout**. C'est la conséquence
directe du modèle Keycloakify, où chaque écran est réimplémenté en React : tout
parcours que l'auteur n'a pas traité disparaît silencieusement, sans erreur ni
avertissement.

Le thème annonçait pourtant WebAuthn dans ses fonctionnalités. **La leçon** :
pour un thème d'authentification, la maintenance et la compatibilité de version
ne suffisent pas comme critères — il faut vérifier la **couverture réelle des
parcours** avant de déployer. Le contrôle « la page de connexion s'affiche » ne
dit rien des chemins alternatifs (passkey, OTP, fédération d'identité).

Retour au thème natif sur les deux royaumes, JAR et montage `providers`
supprimés.

**Conséquence acceptée** : la marque Keycloak redevient visible sur les pages de
connexion, à rebours de l'objectif initial d'ADR-0027. Un parcours
d'authentification complet vaut mieux qu'une page anonyme mais amputée. Si
l'habillage redevient une priorité, le critère d'entrée est désormais explicite :
couverture des passkeys vérifiée **avant** déploiement.

### Précision utile pour la suite

Le royaume du site n'affiche aucun marqueur passkey même sous le thème natif :
ses deux exécutions `webauthn-authenticator` sont au niveau *DISABLED*, alors
que `master` en a au niveau *ALTERNATIVE*. C'est donc une question de
**configuration du royaume**, sans rapport avec le thème — à activer si les
passkeys sont voulus côté site.

## Correctif associé : le healthcheck mentait

La bascule a révélé un défaut du healthcheck écrit en ADR-0030. Il testait la
présence de `"status": "UP"` dans le corps de `/health/ready` ; or pendant
l'amorçage, la réponse vaut `{"status": "DOWN"}` **mais contient un
`"status": "UP"` imbriqué** (pour « Graceful Shutdown »). Docker déclarait donc
le conteneur sain alors que Keycloak répondait encore 503 aux vraies requêtes.

Le healthcheck teste désormais le **code HTTP** (`200` si prêt, `503` sinon),
signal non ambigu. Vérifié après correction : le conteneur passe `healthy` au
moment précis où le service répond publiquement en 200.

C'est le genre de défaut qu'un healthcheck trop permissif rend invisible — il
n'échoue jamais, il ment.
