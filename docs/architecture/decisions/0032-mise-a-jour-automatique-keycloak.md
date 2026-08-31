# ADR-0032 — Mise à jour automatique nocturne de Keycloak sur `latest`

**Statut** : Accepté et déployé.
**Date** : 2026-08-31

## Contexte

Keycloak était déployé sur un tag pinné (`26.7`, puis explicitement `26.7.2`
après vérification de [CVE-2026-18963](https://github.com/advisories/GHSA-4gv3-mc9p-5wqc),
cf. `roadmap.md`). Rester à jour manuellement suppose de surveiller les
sorties de version et d'intervenir soi-même — ce que l'utilisateur souhaite
éviter pour ce service.

L'utilisateur a demandé de suivre le tag `latest` et de retirer l'image
automatiquement chaque nuit.

## Risque signalé, et décision de l'utilisateur

Avant d'implémenter, le risque a été signalé explicitement : `latest` peut
sauter une version **majeure** sans revue humaine, avec un historique concret
dans ce même dépôt de changements cassants entre versions mineures de
Keycloak (renommage de variables d'environnement, changement du port exposant
les endpoints de santé — cf. `ADR-0030`, corrigé en `ADR-0031`). Une question
a été posée sur la stratégie (tag pinné avec relecture humaine vs `latest`
pur) et sur le périmètre des services concernés.

Réponses de l'utilisateur, explicites :

- **Stratégie** : `latest` pur, n'importe quelle version — l'option risquée
  a été choisie en connaissance de cause plutôt que l'alternative pinnée
  recommandée.
- **Périmètre** : Keycloak uniquement. Ni PostgreSQL (le socle partagé,
  `~/infra`, où une régression toucherait aussi le site) ni Caddy (le point
  d'entrée unique du VPS) ne sont concernés.

Conformément à la décision de l'utilisateur, prise après signalement du
risque, le filet de sécurité porte donc entièrement sur le **script**, pas
sur le choix du tag.

## Décision

### `quay.io/keycloak/keycloak:latest` dans le compose

Le tag pinné est remplacé par `latest` dans
`/home/ubuntu/keycloak/docker-compose.yml` (non versionné dans ce dépôt,
cf. `ADR-0030` — reconstructible via la sauvegarde nocturne de `~/infra`).

### `auto-update.sh` : le vrai filet de sécurité

Nouveau script `/home/ubuntu/keycloak/auto-update.sh`, exécuté chaque nuit
par cron à 4h15 (une heure après `~/infra/backup.sh` à 3h, pour ne jamais
sauvegarder pendant une bascule) :

```
15 4 * * * /home/ubuntu/keycloak/auto-update.sh >> /home/ubuntu/keycloak/auto-update.log 2>&1
```

Logique :

1. **Verrou `flock`** — n'exécute jamais deux mises à jour en parallèle (cron
   qui chevaucherait une exécution manuelle, par exemple).
2. **Détection par comparaison de digest** — `docker pull`, puis compare
   l'image du conteneur en service (`docker inspect --format '{{.Image}}'`)
   à l'image fraîchement tirée (`docker image inspect --format '{{.Id}}'`).
   Digest identique → sortie immédiate, rien touché.
3. **Anti-flapping** — un digest ayant déjà échoué est mémorisé dans un
   fichier sentinelle et n'est plus retenté automatiquement les nuits
   suivantes, pour éviter d'échouer en boucle sur une version connue comme
   cassée. Une revue manuelle est alors nécessaire.
4. **Vérification de santé réelle, pas seulement Docker** — après
   `--force-recreate`, une requête HTTPS publique authentique est envoyée à
   travers la pile complète (Caddy → Keycloak) sur
   `/realms/master/.well-known/openid-configuration`, avec un délai maximal
   de 3 minutes. Choix motivé directement par le bug de healthcheck trouvé
   plus tôt cette même session (`ADR-0031`) : le healthcheck Docker interne
   avait déjà menti une fois (`healthy` alors que le service répondait 503),
   donc s'y fier seul aurait été insuffisant.
5. **Retour arrière automatique** — en cas d'échec de la vérification, le
   script retague l'ancienne image (sauvegardée en mémoire avant la mise à
   jour) sur la référence du compose (`docker tag <ancienne> <ref>`) et
   recrée le conteneur, sans toucher au fichier compose. Le retour arrière
   est lui-même vérifié par la même requête de santé publique.
6. **Échec du retour arrière = alerte critique** — si même le retour arrière
   ne restaure pas le service, le script sort en code 2 et le log nocturne
   (`auto-update.log`) porte une intervention manuelle explicite à faire.

### Portée volontairement limitée à Keycloak

PostgreSQL et Caddy restent sur des tags pinnés, mis à jour manuellement.
Une régression sur l'un ou l'autre affecterait le site de Thierry en plus du
SSO, contrairement à Keycloak qui est isolé depuis `ADR-0030`.

## Vérification effectuée

- **Chemin « déjà à jour »** : testé en conditions réelles sur la
  production. Le script détecte un digest inchangé et sort proprement sans
  toucher au conteneur ; le service reste `healthy` et répond 200 après
  exécution.
- **Commande de retour arrière (`docker tag`)** : vérifiée de façon isolée
  (retag vers une référence de test, comparaison des digests, nettoyage) sans
  toucher au conteneur réel — confirme que la commande utilisée par le
  script pointe exactement où attendu.
- **Recréation + boucle d'attente de santé** : réutilise le même
  enchaînement (`docker compose up -d --force-recreate` puis sondage de
  santé) déjà exécuté à plusieurs reprises manuellement dans cette session
  (déploiement du thème, correctif du healthcheck, passage à `26.7.2`) —
  non re-testé isolément dans ce script précis.
- **Chemin complet « nouvelle version détectée → échec → retour arrière »**
  n'a **pas** été déclenché de bout en bout : cela supposerait de casser
  volontairement la production. À surveiller sur les premières exécutions
  nocturnes réelles via `auto-update.log`.

## Conséquences

- Keycloak reste à jour sans intervention manuelle, y compris pour de
  futurs correctifs de sécurité comme CVE-2026-18963.
- Risque assumé : une mise à jour majeure de Keycloak peut être appliquée
  sans revue humaine préalable si elle passe la vérification de santé
  superficielle (le service répond, sans garantie que tout comportement fin
  soit intact) — compensé par le retour arrière automatique en cas d'échec
  franc, pas par une revue de changelog.
- `~/keycloak/auto-update.log` est la première chose à consulter en cas de
  doute sur l'état de Keycloak après une nuit.
- Le script et son état (verrou, dernier échec) sont capturés automatiquement
  par `~/infra/backup.sh`, qui archive déjà `~/keycloak` dans son intégralité
  — aucune action supplémentaire nécessaire côté sauvegarde.
