# ADR-0027 — Keycloak 26 comme IAM, instance Postgres partagée, thème personnalisé

**Statut** : Acté
**Date** : 2026-08-19

## Contexte

L'utilisateur veut un IAM (Keycloak 26) sur le VPS unique existant, pour un
second domaine (`iabilis.fr`, déjà pointé vers le serveur), dans le but de
brancher plus tard le back-office du site dessus (hors périmètre de cet ADR)
et un futur petit panneau de supervision (hors périmètre également, à
concevoir séparément une fois Keycloak en ligne).

Contraintes fortes : le VPS reste un unique VPS-1 OVHcloud (2 vCore / 4 Go
RAM), déjà occupé par `app` + `db` + `caddy` (limites cumulées 2432 Mo / 4096
Mo, cf. [ADR-0022](0022-limites-ressources-conteneurs.md)) — l'utilisateur veut
explicitement limiter le nombre de services plutôt qu'en ajouter plusieurs.

## Décision

### Instance Postgres partagée, base et rôle dédiés

Keycloak utilise le conteneur `db` existant (pas de conteneur PostgreSQL
supplémentaire), mais avec un **rôle et une base dédiés** (`keycloak`/`keycloak`)
plutôt que de réutiliser le rôle applicatif `site` — moindre privilège, cohérent
avec le sous-packaging strict déjà pratiqué partout ailleurs dans ce projet (cf.
`CLAUDE.md`). Comme `POSTGRES_DB`/`POSTGRES_USER` du conteneur `db` ne
provisionnent qu'une base au tout premier démarrage (déjà passé depuis
longtemps, la base `site` existe), la base/rôle Keycloak s'ajoutent via une
commande SQL **manuelle unique**, documentée dans
[mode-operatoire-deploiement.md §11](../../mode-operatoire-deploiement.md) —
pas un script automatisé, même logique que l'activation d'ufw en son temps :
une action ponctuelle, pas un mécanisme à maintenir.

**Conséquence sur les sauvegardes** : `scripts/backup.sh`/`restore.sh` faisaient
un `pg_dump`/`pg_restore` ciblé sur `POSTGRES_DB` uniquement (pas un
`pg_dumpall`) — la base `keycloak`, bien que dans la même instance, n'aurait
donc pas été sauvegardée sans modification. Les deux scripts dument désormais
un second dump/restore, actif seulement si `KEYCLOAK_DB` est définie dans
`.env` (donc sans impact sur un environnement sans Keycloak), dans la même
archive.

### Configuration Keycloak : proxy, hostname, admin

- Image `quay.io/keycloak/keycloak:26.7` (dernière version stable de la
  branche 26 au moment de cette décision).
- `start` (mode production, pas `start-dev`).
- `KC_PROXY_HEADERS=xforwarded` — nom d'option Keycloak 26 courant (remplace
  l'ancien `KC_PROXY=edge` des versions antérieures) pour faire confiance aux
  en-têtes `X-Forwarded-*` envoyés par Caddy — sans ça, Keycloak croirait être
  accédé en HTTP direct et générerait des liens/redirections incorrects.
- `KC_HTTP_ENABLED=true`, pas de TLS côté Keycloak : Caddy termine le HTTPS,
  même partage de responsabilité que pour `app` (cf. ADR-0016).
- `KC_HOSTNAME=https://iabilis.fr`.
- `KC_BOOTSTRAP_ADMIN_USERNAME`/`KC_BOOTSTRAP_ADMIN_PASSWORD` (noms actuels en
  Keycloak 26, remplacent `KEYCLOAK_ADMIN`/`KEYCLOAK_ADMIN_PASSWORD` des
  versions antérieures) — n'ont d'effet qu'au tout premier démarrage.
- `KC_HEALTH_ENABLED=true` pour un healthcheck Docker cohérent avec `db`.
- Pas de `ports` publiés sur l'hôte, seulement `expose: ["8080"]` — joignable
  uniquement via le réseau Compose interne (`keycloak:8080`), même pattern que
  `app`.
- Limites de ressources : `cpus: "0.75"`, `memory: 768M`. Un premier essai à
  `0.5`/`512M` s'est révélé insuffisant dès le premier démarrage — la phase de
  build/augmentation Quarkus de Keycloak (recompilation interne de sa
  configuration, spécifique au tout premier boot après un changement de
  config) a saturé les deux plafonds simultanément (`docker stats` : ~97 %
  mémoire, CPU au maximum du quota), bloquant durablement le démarrage.
  Reste dans le budget du VPS : mémoire cumulée `db` (768M) + `app` (1536M) +
  `caddy` (128M) + `keycloak` (768M) = 3200 Mo sur 4096 Mo disponibles. Toujours
  à ajuster via `docker stats` en régime de croisière si besoin, même logique
  que ADR-0022.
- Pas de cache/service supplémentaire : le cache Infinispan embarqué de
  Keycloak suffit pour un nœud unique, aucune configuration additionnelle.

### Caddy : nouveau domaine, en-têtes de sécurité

Nouveau bloc `iabilis.fr` dans le `Caddyfile`, même pattern que le bloc
`thierrylacassin-auteur.fr` existant : `encode gzip zstd`,
`reverse_proxy keycloak:8080`, et les en-têtes `Strict-Transport-Security`,
`X-Content-Type-Options: nosniff`, `Referrer-Policy:
strict-origin-when-cross-origin`.

**Pas** de `X-Frame-Options`/CSP forcés côté Caddy pour ce domaine : Keycloak
gère déjà ses propres en-têtes anti-clickjacking par royaume (Realm Settings →
Security Defenses, `SAMEORIGIN` par défaut) et en a besoin pour ses propres
mécanismes OIDC (iframe de vérification de session) — les forcer au niveau du
reverse proxy risquerait d'entrer en conflit avec ces réglages, surtout une
fois le back-office du site branché en cross-origin sur ce domaine (prévu
« plus tard » par l'utilisateur, pas dans le périmètre de cet ADR).

### Thème de connexion personnalisé ("lacassin-boat")

> **Remplacé** — cf. [ADR-0031](0031-theme-keycloak-shadcn.md) : ce thème maison
> n'a pas convaincu l'utilisateur et a été remplacé par `shadcn-theme`, un thème
> tiers maintenu. La section ci-dessous est conservée pour l'historique de la
> décision, elle ne décrit plus l'état du système.

L'utilisateur veut éviter qu'on devine que Keycloak est utilisé derrière ce
domaine. Dossier `lacassin-boat/login/`, monté en lecture seule dans le
conteneur (`/opt/keycloak/themes`) :

- `theme.properties` avec `parent=keycloak.v2` — hérite de tout le thème par
  défaut, pas besoin de dupliquer les templates FreeMarker.
- `resources/img/logo.svg` — icône bateau dessinée en SVG (aucun asset fourni).
- `resources/css/custom.css` — remplace le logo par défaut (`.kc-logo-text`) et
  masque le lien "powered by Keycloak" (`a[href*="keycloak.org"]`).

**Scope v1 volontairement limité au thème de connexion** (le plus visible) —
ni le thème "compte" (self-service, pas encore utile tant que rien n'est
branché dessus), ni le thème admin (usage interne uniquement) ne sont touchés.

Sélection du thème (Realm Settings → Themes → Login theme) : action manuelle
ponctuelle dans la console d'administration après le premier démarrage,
documentée dans `mode-operatoire-deploiement.md` — non automatisable sans un
import de royaume complet, hors périmètre ici.

**Limite connue** : les sélecteurs CSS (`.kc-logo-text`,
`a[href*="keycloak.org"]`) sont ceux du thème `keycloak.v2` au moment
d'écrire cet ADR — à revérifier/ajuster une fois Keycloak réellement démarré
et la page de connexion inspectée dans un navigateur, les thèmes Keycloak
pouvant évoluer entre versions.

### Email : recommandation actée

Réutilisation du relais SMTP de **Brevo**, déjà utilisé pour la newsletter du
site (`BREVO_API_KEY`, cf. [ADR-0002](0002-fournisseur-emailing.md)/[ADR-0013](0013-newsletter-double-opt-in-brevo.md))
plutôt que d'ajouter un troisième fournisseur d'email (Brevo pour la
newsletter, SMTP générique prévu pour le contact, cf.
[ADR-0014](0014-contact-smtp-generique.md)). Brevo expose un relais SMTP
classique (`smtp-relay.brevo.com:587`) avec des identifiants **différents** de
la clé API déjà utilisée (une "clé SMTP" à générer séparément dans le même
compte Brevo). Configuration réalisée directement dans la console Keycloak
(Realm Settings → Email) — pas de variable d'environnement Docker pour ça,
juste une action manuelle documentée dans `mode-operatoire-deploiement.md`.

**Adresse expéditrice : `no-reply@iabilis.fr`**, pas l'adresse newsletter
existante du site. Réutiliser `newsletter@thierrylacassin-auteur.fr` aurait
été gratuit (expéditeur déjà déclaré dans Brevo) mais aurait révélé le lien
entre les deux domaines à chaque email de réinitialisation — exactement ce que
le thème personnalisé ci-dessus cherche à éviter — en plus d'être déroutant
pour le destinataire. `iabilis.fr` dispose déjà de MX OVH, les réponses
éventuelles arrivent donc bien quelque part.

**Prérequis DNS, découvert à la configuration** : `iabilis.fr` n'avait aucune
authentification Brevo (vérifié : pas d'enregistrement DKIM `mail._domainkey`).
Envoyer depuis `@iabilis.fr` exige donc d'abord d'**authentifier le domaine**
dans Brevo (TXT de propriété `brevo-code:` + TXT DKIM à créer dans la zone OVH),
avec 15 min à 1 h de propagation — c'est l'étape la plus longue, et tant
qu'elle n'est pas au vert Brevo rejette les envois (`sender not valid`). Le SPF
existant (`v=spf1 include:mx.ovh.com -all`, en `-all` strict) n'a en revanche
pas besoin d'être modifié : Brevo signe avec son propre Return-Path, c'est le
DKIM qui assure l'alignement DMARC. Procédure détaillée dans
[mode-operatoire-deploiement.md §12](../../mode-operatoire-deploiement.md).

## Alternatives envisagées

- **Conteneur PostgreSQL dédié à Keycloak** : écarté explicitement par
  l'utilisateur (« je voudrais utiliser la même instance de Postgres ») — et de
  toute façon contraire à la volonté de limiter le nombre de services sur un
  VPS déjà serré.
- **Réutiliser le rôle Postgres `site` existant pour Keycloak** : écarté,
  violerait le moindre privilège déjà pratiqué partout ailleurs dans ce projet
  pour un gain nul (créer un second rôle ne coûte qu'une commande SQL de plus).
- **`pg_dumpall` à la place du `pg_dump` ciblé existant** : écarté — changerait
  le format de sauvegarde pour tout le monde (y compris les archives déjà
  produites) pour un problème qui ne concerne que l'ajout d'une base ; un
  second dump ciblé, conditionnel, est un changement strictement additif.
- **`X-Frame-Options: DENY` côté Caddy pour `iabilis.fr`** (cohérence avec le
  site principal) : écarté, casserait les mécanismes OIDC de Keycloak
  (iframe de vérification de session) et le futur rattachement cross-origin du
  back-office — Keycloak gère déjà cet en-tête lui-même, par royaume.
- **Thème complet (compte + admin + email en plus du login)** : écarté pour
  cette v1 — proportionnalité (cf. `CLAUDE.md`), rien n'est encore branché sur
  ces surfaces, à réévaluer une fois le panneau de supervision ou le SSO du
  back-office en place.
- **Nouveau fournisseur d'email dédié à Keycloak** : écarté — ajouterait un
  troisième compte/fournisseur à gérer alors que Brevo (déjà en place) expose
  un relais SMTP standard tout à fait utilisable par Keycloak.

## Conséquences

- `docker-compose.prod.yml` : nouveau service `keycloak`.
- `Caddyfile` : nouveau bloc `iabilis.fr`.
- `.env.example` : nouvelles variables `KEYCLOAK_DB`, `KEYCLOAK_DB_USER`,
  `KEYCLOAK_DB_PASSWORD`, `KC_BOOTSTRAP_ADMIN_USERNAME`,
  `KC_BOOTSTRAP_ADMIN_PASSWORD`.
- `scripts/backup.sh`/`scripts/restore.sh` : dump/restore conditionnel de la
  base `keycloak`.
- Nouveau dossier `lacassin-boat/login/` (déplacé hors de ce dépôt vers
  `~/keycloak/themes/` par ADR-0030).
- `docs/mode-operatoire-deploiement.md` : nouvelle section (bootstrap SQL,
  sélection du thème, configuration SMTP — actions manuelles ponctuelles).
- **Non fait dans cette passe**, nécessite une action humaine ou une décision
  ultérieure :
  - Exécution réelle du bootstrap SQL et premier déploiement sur le VPS.
  - Authentification du domaine `iabilis.fr` dans Brevo (enregistrements DNS
    chez OVH), génération de la clé SMTP dédiée, puis configuration dans la
    console Keycloak — cf. mode-operatoire-deploiement.md §12.
  - Vérification visuelle du thème une fois Keycloak démarré (sélecteurs CSS
    à confirmer, cf. limite connue ci-dessus).
  - Panneau de supervision web authentifié via Keycloak (à concevoir
    séparément).
  - Rattachement du back-office du site à Keycloak (SSO) — prévu « plus tard »
    par l'utilisateur.
