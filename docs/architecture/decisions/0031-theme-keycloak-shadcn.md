# ADR-0031 — Thème Keycloak : `shadcn-theme` (tiers maintenu) plutôt qu'un thème maison

**Statut** : Acté — remplace la section « thème » d'[ADR-0027](0027-keycloak-iam.md)
**Date** : 2026-08-20

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

## Conséquences

- `~/keycloak/providers/shadcn-theme.jar` + montage `providers` dans le compose.
- Les deux royaumes utilisent `shadcn-theme` comme thème de connexion.
- L'ancien thème `lacassin-boat` est supprimé de `~/keycloak/themes/`.
- **Dépendance à un tiers en version 0.9.x**, dont le dépôt prévient que « les
  API peuvent changer sans préavis » : la version est donc **épinglée**
  (v0.9.1), à ne pas suivre aveuglément lors des mises à jour.
- **Retour arrière** en une commande, sans console d'administration :
  `UPDATE realm SET login_theme = NULL WHERE name = '<royaume>';` puis
  redémarrage du conteneur.

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
