# ADR-0030 — Socle VPS partagé : Caddy et PostgreSQL sortis du dépôt du site

**Statut** : Acté — modifie [ADR-0016](0016-deploiement-caddy-prod.md) (stack unique),
[ADR-0012](0012-sauvegarde-restauration.md) (emplacement des sauvegardes) et
[ADR-0027](0027-keycloak-iam.md) (emplacement de Keycloak)
**Date** : 2026-08-20

## Contexte

Le VPS n'hébergeait qu'une seule stack Docker Compose, définie dans le dépôt du
site (`~/mybook`) : `app`, `db`, `caddy` et `keycloak`. Or deux de ces services
ne concernent pas le site :

- **Keycloak** est un SSO destiné à toute la machine (panneau de supervision
  prévu, puis le back-office du site lui-même).
- **Caddy** détient les ports 80/443 pour tous les domaines de la machine
  (`thierrylacassin-auteur.fr`, `leblogdethierry.fr`, `iabilis.fr`).

Conséquences constatées :

- `scripts/deploy.sh` du site faisait `up -d --build` sur toute la stack :
  corriger une coquille sur le site recréait le conteneur SSO — donc, demain,
  couperait l'authentification de toutes les applications.
- `scripts/backup.sh` du site sauvegardait la base `keycloak`.
- Une décision d'infrastructure SSO était documentée dans l'architecture d'un
  site de romans.

## Décision

Trois projets Compose distincts, réunis par un réseau Docker externe `socle` :

```
~/infra/       caddy + db (postgres:16)   ← socle partagé, non versionné
~/keycloak/    keycloak                   ← SSO, non versionné
~/mybook/      app                        ← ce dépôt
```

Compose publie le nom de chaque service comme alias réseau, y compris entre
projets : `db:5432` et `keycloak:8080` restent résolvables sans changer une
seule URL de connexion.

### Routage : un `Caddyfile` maigre et un dossier `conf.d/`

Le `Caddyfile` du socle ne porte que les réglages globaux et
`import /etc/caddy/conf.d/*.caddy`. **Chaque application versionne son propre
fragment** dans son dépôt (`caddy/mybook.caddy` ici) et son `deploy.sh` le copie
dans `~/infra/conf.d/` avant de recharger Caddy.

Le serveur appartient donc au socle, mais le routage reste la propriété de
l'application — un changement de domaine côté site reste tracé dans le dépôt du
site. Le déploiement du site touche encore Caddy, mais uniquement **son
fragment** plus un rechargement : couplage borné, sans commune mesure avec la
recréation du conteneur SSO.

**Effet de bord notable** : on monte désormais un **répertoire** au lieu d'un
fichier unique. Le piège de l'inode décrit dans `CLAUDE.md` — le conteneur suit
l'inode monté, que `git pull` remplace, d'où un `Caddyfile` obsolète servi
indéfiniment — **disparaît**, et le `up -d --force-recreate caddy` ajouté en
contournement devient inutile.

### Aucune donnée déplacée

Les données PostgreSQL étaient déjà en bind mount vers
`/home/ubuntu/volumes/db-data` (cf. [ADR-0023](0023-bind-mounts-volumes-visibles.md)) :
le compose du socle se re-pointe sur **le même chemin hôte**. Ni `pg_dump`, ni
copie, ni migration — c'est ce qui a rendu cette réorganisation peu risquée.

Garde-fous appliqués lors de la bascule :

1. **Jamais deux PostgreSQL sur le même répertoire** — seul scénario de
   corruption réelle. L'ancienne stack est arrêtée entièrement avant que le
   socle ne démarre.
2. **`postgres:16` impératif** : le répertoire porte un `PG_VERSION` à `16`, une
   image majeure différente refuserait de démarrer.
3. **Jamais `down -v`**.
4. **Les mots de passe des rôles vivent dans les données**, pas dans le `.env` :
   `POSTGRES_USER`/`POSTGRES_PASSWORD` ne s'appliquent qu'à un répertoire vide
   (initdb). Le `.env` du socle reprend donc exactement les valeurs existantes ;
   les modifier n'aurait pas changé les rôles, seulement empêché les
   applications de s'authentifier.

### La sauvegarde devient une responsabilité du socle

`scripts/backup.sh` et `scripts/restore.sh` sont **supprimés de ce dépôt** au
profit de `~/infra/backup.sh`, qui sauvegarde en une fois **toutes** les bases,
les données persistantes et la configuration du socle.

Deux améliorations au passage :

- **`pg_dumpall` remplace le `pg_dump` ciblé** : les **rôles et leurs mots de
  passe** sont désormais sauvegardés. L'ancien script, ciblé sur une seule base,
  ne les capturait pas — une machine détruite n'aurait pas été reconstructible
  sans recréer les rôles à la main.
- **Rétention de 14 archives** : l'ancien script n'en purgeait aucune, le disque
  se serait rempli en silence.
- La configuration de `~/infra` et `~/keycloak`, non versionnée dans Git, est
  incluse dans l'archive pour rester reconstructible.

## Alternatives envisagées

- **Rattacher Keycloak au réseau `mybook_default` en `external`** : écarté —
  séparation en trompe-l'œil, le SSO dépendrait d'un réseau nommé d'après le
  site, et un `compose down` sur le site poserait problème.
- **Donner à Keycloak son propre PostgreSQL** : écarté — deux moteurs à patcher
  et sauvegarder sur une machine unique, alors que la base et le rôle `keycloak`
  sont déjà isolés (ADR-0027). La RAM n'était pas le frein (PostgreSQL n'utilise
  que ~73 Mo réels), la maintenance récurrente si.
- **Laisser Caddy dans le dépôt du site** : écarté — le serveur détient les
  ports de toute la machine ; le laisser appartenir à une application était la
  cause du problème, pas une simple gêne esthétique.
- **Versionner `~/infra` et `~/keycloak` dans un dépôt Git dédié** : envisagé,
  reporté — la sauvegarde couvre le besoin immédiat de reconstructibilité. À
  reprendre si la configuration du socle se met à changer souvent.

## Conséquences

- `docker-compose.prod.yml` ne contient plus que `app` et rejoint le réseau
  `socle` ; `Caddyfile` devient `caddy/mybook.caddy` (fragment).
- `scripts/deploy.sh` réécrit ; `scripts/backup.sh` et `restore.sh` supprimés.
- `.env.example` : variables Keycloak et `CADDY_ACME_EMAIL` retirées, variables
  `POSTGRES_*` conservées (l'application s'en sert pour **se connecter**).
- Le cron de sauvegarde pointe vers `~/infra/backup.sh`.
- **Perte assumée** : `depends_on` n'a plus de portée entre projets Compose. Au
  démarrage de la machine, `app` et `keycloak` peuvent redémarrer une ou deux
  fois avant que PostgreSQL réponde — `restart: unless-stopped` absorbe ce cas,
  au prix de quelques secondes de latence au boot.
- Trois `docker compose` à connaître au lieu d'un : le mode opératoire est mis à
  jour en conséquence.

## Ce que la bascule a révélé

Trois défauts préexistants, invisibles jusqu'à ce que l'opération les expose :

1. **La base Keycloak n'a jamais été sauvegardée.** Le dump conditionnel ajouté
   la veille testait `${KEYCLOAK_DB:-}`, mais `scripts/backup.sh` ne sourçait pas
   le `.env` : la variable était toujours vide côté shell, la condition toujours
   fausse, et l'archive ne contenait que `base.dump`. Le passage à `pg_dumpall`
   supprime cette classe de bug — il n'y a plus de liste de bases à tenir à jour.
2. **Aucune purge des archives.** L'ancien script empilait les sauvegardes sans
   limite ; le disque se serait rempli en silence. Rétention à 14 archives.
3. **Les rôles n'étaient pas sauvegardés**, cf. ci-dessus.

Un piège d'exécution mérite aussi d'être noté : les scripts transférés via un
pipe PowerShell arrivaient avec un **BOM UTF-8**, qui casse la ligne shebang —
le noyau ne la reconnaît plus et retombe sur `sh`, où les constructions bash
(`${BASH_SOURCE[0]}`) échouent avec un `Bad substitution` trompeur. Utiliser
`scp` plutôt qu'un pipe règle le problème (cf. CLAUDE.md).
