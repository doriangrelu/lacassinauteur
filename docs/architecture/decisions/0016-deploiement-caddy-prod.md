# ADR-0016 — Déploiement production : Docker Compose dédié, Caddy en reverse proxy, secrets via `.env`

**Statut** : Acté (configuration seulement — mise en ligne réelle non faite, cf. Conséquences)
**Date** : 2026-08-14

## Contexte

La Phase 7 de la roadmap (cf. [roadmap.md](../../roadmap.md)) prépare le déploiement
du site sur un unique VPS Hetzner via Docker Compose, cible déjà actée dans
[tech-stack.md §Déploiement — Hetzner](../tech-stack.md#déploiement--hetzner) : un
service `app` (le monolithe Spring Boot), un service `db` (PostgreSQL), et un reverse
proxy assurant le HTTPS pour `thierrylacassin-auteur.fr`.

Cette passe consiste à produire la configuration concrète (fichiers Compose, reverse
proxy, secrets, script de déploiement) — sans provisionnement réel du serveur ni achat
du domaine, qui restent des actions humaines hors périmètre de cet ADR (cf.
Conséquences).

## Décision

### Fichier Compose prod autonome, pas un override

`docker-compose.prod.yml` est un fichier **complet et autonome**, pas un
`docker-compose.override.yml` fusionné avec le `docker-compose.yml` de dev. Les deux
stacks divergent sur des points structurants (pas de port `5432` publié, service
`caddy` supplémentaire, `restart: unless-stopped` partout, variables sensibles
paramétrées au lieu de valeurs en dur) : une fusion de fichiers Compose ne sait pas
proprement **retirer** une clé déjà définie par le premier fichier (ex. supprimer la
publication du port `5432` du service `db`), seulement la remplacer ou la compléter,
ce qui aurait rendu la fusion fragile et peu lisible pour un opérateur qui doit
comprendre d'un coup d'œil ce qui tourne réellement en production. Les deux fichiers
partagent volontairement les mêmes noms de service (`app`, `db`) et de volumes
nommés (`db-data`, `images-data`) pour rester familiers.

### Caddy comme reverse proxy

Confirme le choix déjà fait dans `tech-stack.md` : **Caddy** (image officielle
`caddy:2`) plutôt que Nginx + Certbot. Un unique `Caddyfile` de quelques lignes suffit
— Caddy obtient et renouvelle automatiquement un certificat Let's Encrypt dès qu'il
démarre avec un nom de domaine réel pointé vers le serveur, sans configuration TLS
manuelle ni tâche cron de renouvellement à maintenir (contrairement à
Nginx + Certbot, qui demande deux outils distincts et un renouvellement planifié
séparément). Les certificats et l'état interne de Caddy vivent dans deux volumes
nommés (`caddy-data`, `caddy-config`) pour survivre aux redéploiements — sans ça,
chaque `up --build` redemanderait des certificats neufs et risquerait de heurter le
rate limit de Let's Encrypt.

Domaine canonique choisi : l'apex `thierrylacassin-auteur.fr` (redirection 301 depuis
`www.thierrylacassin-auteur.fr`) — choix arbitraire, les deux se valent en référencement
du moment qu'une redirection est en place ; l'apex est plus court à communiquer
(cartes de visite, dédicaces).

`reverse_proxy` transmet par défaut les en-têtes `X-Forwarded-For`/`-Proto`/`-Host` à
l'upstream, sans configuration additionnelle. `application-prod.yml` a déjà
`server.forward-headers-strategy: framework` pour les exploiter côté Spring
(restitution de la vraie IP client, utilisée par le rate limiting anti brute-force,
[ADR-0008](0008-anti-bruteforce-bucket4j.md)) — les deux bouts étaient déjà alignés,
cette passe ne fait que les connecter.

### `db` sans port publié en production

Le service `db` du `docker-compose.prod.yml` n'a **aucune** section `ports` (contre
`"5432:5432"` en dev, utile pour `psql` local). En production, seul le service `app`
a besoin d'atteindre PostgreSQL, via le réseau Compose interne (`db:5432`) — publier
ce port sur l'hôte n'apporterait rien et exposerait inutilement la base au réseau
public du VPS.

### Secrets via `.env`, jamais en dur

Toutes les valeurs sensibles (mot de passe PostgreSQL, clé API Brevo, identifiants
SMTP) sont référencées dans `docker-compose.prod.yml` via `${VARIABLE}` et
documentées — avec des valeurs placeholder, jamais réelles — dans `.env.example`
(committé). Le fichier `.env` réel (valeurs réelles) reste local au VPS, déjà
gitignoré (`.gitignore` avait déjà une règle `.env` depuis une passe antérieure,
confirmée, non dupliquée).

`.env.example` inclut aussi `COMPOSE_FILE=docker-compose.prod.yml` : cette variable
spéciale, lue automatiquement par le CLI `docker compose` depuis le `.env` du
répertoire courant, fait que les commandes `docker compose` lancées **sans** `-f` —
c'est le cas de `scripts/backup.sh` et `scripts/restore.sh`, écrits pour être
agnostiques de l'environnement (cf. [ADR-0012](0012-sauvegarde-restauration.md)) —
ciblent automatiquement la stack de production plutôt que le `docker-compose.yml` de
dev, sans avoir à modifier ces deux scripts.

### Construction de l'image directement sur le VPS

Pas de registre d'images Docker (Docker Hub, GHCR...) dans cette v1 : `app` garde
`build: .` dans `docker-compose.prod.yml`, et `scripts/deploy.sh` enchaîne
`git pull` puis `docker compose -f docker-compose.prod.yml up -d --build`. L'image
est donc reconstruite **sur le serveur lui-même**, à partir des sources versionnées —
reproductible (même `Dockerfile`, mêmes sources) sans étape de publication d'image
intermédiaire à opérer ni à sécuriser (identifiants de registre, etc.), cohérent avec
un déploiement mono-serveur/mono-opérateur. Le cache de layers Docker reste sur le
VPS d'un déploiement à l'autre : seules les couches touchées par des sources modifiées
sont reconstruites, la couche de dépendances Maven ne l'est que si `pom.xml` change.

Le script `deploy.sh` lance systématiquement `scripts/backup.sh` avant de
redéployer — cohérent avec la recommandation déjà actée dans
[ADR-0012](0012-sauvegarde-restauration.md) (« avant tout déploiement ou migration
Flyway risquée, exécuter `scripts/backup.sh` devient le réflexe attendu ») : plutôt
que de compter sur la discipline de l'opérateur, le script l'automatise.

Les migrations Flyway s'exécutent automatiquement au démarrage de Spring Boot
(`spring.flyway.enabled: true`, aucun `FlywayMigrationStrategy` personnalisé dans le
code — vérifié, pas supposé) : redémarrer `app` via `up -d --build` suffit, aucune
commande de migration séparée n'est nécessaire dans `deploy.sh`.

## Alternatives envisagées

- **Nginx + Certbot** : alternative déjà citée dans `tech-stack.md`, écartée pour les
  mêmes raisons qu'à l'origine — deux outils à coordonner, renouvellement de
  certificat à planifier soi-même (cron), configuration Nginx plus verbeuse pour un
  gain nul sur ce périmètre (un seul domaine, un seul upstream).
- **`docker-compose.override.yml`** fusionné avec le fichier de dev : écarté, cf.
  ci-dessus (impossibilité propre de retirer une clé déjà définie, lisibilité).
- **Registre d'images (Docker Hub/GHCR) + CI de publication** : écarté pour cette
  passe — ajoute un pipeline de build/push à mettre en place et sécuriser
  (identifiants de registre) pour un bénéfice marginal à l'échelle d'un unique VPS
  redéployé par un seul opérateur ; réévaluable en v2 si le besoin de rollback rapide
  ou de déploiement depuis une machine tierce apparaît.
- **Secrets Docker (`docker secret`)** : nécessite Swarm, hors de proportion pour un
  unique nœud Compose ; les variables d'environnement via `.env` (permissions
  fichier restreintes sur le VPS) suffisent à ce périmètre.

## Conséquences

- Nouveaux fichiers versionnés : `docker-compose.prod.yml`, `Caddyfile`,
  `.env.example`, `scripts/deploy.sh`.
- `docker compose -f docker-compose.prod.yml config` a été vérifié valide (variables
  d'interpolation résolues) avec un `.env` de test à valeurs manifestement factices,
  jamais committé. `caddy validate` a été vérifié sur le `Caddyfile`.
- **Non fait dans cette passe**, nécessite une action humaine (comptes/paiement) :
  - Provisionnement effectif du VPS Hetzner (roadmap Phase 7).
  - Achat et configuration DNS du domaine `thierrylacassin-auteur.fr` (roadmap
    Phase 7).
  - Première mise en ligne réelle (première exécution de `scripts/deploy.sh` sur le
    VPS, premier certificat Let's Encrypt réellement émis).
  - Création du compte Brevo et fourniture de `BREVO_API_KEY` (déjà noté en attente
    en Phase 4 de la roadmap), et choix + configuration des identifiants SMTP réels
    pour le contact (Phase 5).
- Une fois le VPS provisionné, la planification cron de `scripts/backup.sh` (déjà
  identifiée comme restant à faire dans [ADR-0012](0012-sauvegarde-restauration.md))
  reste à mettre en place.
