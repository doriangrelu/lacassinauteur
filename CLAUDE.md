# CLAUDE.md

Contexte pour toute session Claude Code travaillant sur ce dépôt.

## Langue

**Toujours répondre en français** à l'utilisateur, quel que soit le sujet.

## Projet

Site auteur + back-office pour Thierry Lacassin (écrivain), développé pour son
gendre (l'utilisateur de ce dépôt) qui agit comme développeur principal. Objectif :
que Thierry gère lui-même son catalogue (univers/collections/livres), ses actualités
et sa newsletter via un back-office, en autonomie complète. Déploiement cible : VPS
OVHcloud unique via Docker Compose.

**Avant de faire quoi que ce soit d'architectural, lire `/docs`** — c'est la source
de vérité vivante, à tenir à jour à chaque évolution (pas une passe séparée) :

- [`docs/business/brief.md`](docs/business/brief.md) — besoin fonctionnel
- [`docs/architecture/architecture.md`](docs/architecture/architecture.md) — règles Clean Architecture
- [`docs/architecture/domain-model.md`](docs/architecture/domain-model.md) — modèle de domaine
- [`docs/architecture/package-structure.md`](docs/architecture/package-structure.md) — arborescence de packages concrète
- [`docs/architecture/tech-stack.md`](docs/architecture/tech-stack.md) — stack technique détaillée
- [`docs/architecture/decisions/`](docs/architecture/decisions/) — ADRs (une décision structurante = un ADR numéroté)
- [`docs/roadmap.md`](docs/roadmap.md) — avancement phase par phase, à cocher au fil de l'eau

## Règles d'architecture non négociables

- **Clean Architecture** stricte, package racine `fr.lacassinauteur.site` :
  `domain` (pur, zéro dépendance framework) → `application` (use cases + services) →
  `presentation` (contrôleurs/viewmodels/forms, types Spring autorisés) ←
  `infrastructure` (adaptateurs des ports du domaine).
- **Un use case = une classe** (`CreerLivreUseCase`, pas de "service fourre-tout").
  Un **service applicatif** n'existe que si la logique est partagée par plusieurs use
  cases.
- **Sous-packaging strict** : jamais deux types d'objets différents dans un même
  package (`domain.model` / `domain.port` / `domain.exception`,
  `application.usecase.<sous-thème>` / `application.command` / `application.result`,
  etc.) — voir `package-structure.md` pour le détail exact, domaine par domaine.
- `presentation` est un module à part entière, **jamais imbriqué dans**
  `infrastructure`.
- `shared` est réservé aux capacités réellement transverses (ex. stockage de
  fichiers) — aucune logique métier d'un domaine spécifique ne doit s'y échapper. Un
  `@ControllerAdvice` qui traduit les exceptions d'un domaine précis vit dans la
  `presentation` de ce domaine, pas dans `shared` (sens de dépendance à respecter).
- Domaines métier explicites : `identity`, `catalogue`, `actualite`, `newsletter`,
  `contact`, `biographie`, `legal` — chacun suit exactement le même schéma de
  sous-packages. (`biographie` porte la page « Auteur » ; nommé ainsi et pas
  `auteur` pour ne pas entrer en collision avec le rôle `AUTEUR`, qui existait alors
  dans `identity` (supprimé depuis le passage à Keycloak, cf. ADR-0033) — cf.
  ADR-0028. `legal` porte les variables des pages légales, cf. ADR-0029. `identity`
  ne porte plus aucun compte local depuis ADR-0033 : uniquement l'intégration OIDC
  avec Keycloak.)
- Java moderne (lambdas/streams, records pour les value objects), principes SOLID.
- Séparation stricte des espaces front (public / back-office) : aucun layout ni
  fragment Thymeleaf partagé entre les deux, deux points d'entrée CSS Tailwind
  distincts (`frontend/public.css`, `frontend/backoffice.css`).

## Commandes utiles

```bash
# Build + tests
mvn test

# Lancer l'app en local (nécessite Postgres — voir docker compose ci-dessous)
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run

# Postgres local seul (dev : l'app tourne alors hors Docker)
docker compose up -d db

# Stack complète (app + db) en local — le docker-compose.yml de dev reste
# monolithique, contrairement à la production éclatée en trois projets (ADR-0030)
docker compose up -d

# Compilation Tailwind (watch, en dev — binaire standalone dans tools/, gitignoré)
tools/tailwindcss.exe -i frontend/public.css -o src/main/resources/static/css/public.css --watch
tools/tailwindcss.exe -i frontend/backoffice.css -o src/main/resources/static/css/backoffice.css --watch

# Sauvegarde / restauration : sur le VPS uniquement, dans le socle partagé
# (cf. ADR-0012 amendé par ADR-0030 et ADR-0034 — ces scripts ne sont plus dans
# ce dépôt ; ~/mybook/backups est un reste arrêté en août 2026, ne plus s'en servir)
~/infra/backup.sh </dev/null
~/infra/restore.sh <archive.tar.gz>
```

**Production** : trois projets Compose distincts sur le VPS (cf.
[ADR-0030](docs/architecture/decisions/0030-socle-vps-partage.md)) — `~/infra`
(Caddy + Postgres, partagés), `~/keycloak` (SSO, branche fixe `26.8`) et
`~/mybook` (l'application seule). Déployer le site ne touche donc ni la base,
ni le SSO. `~/infra` et `~/keycloak` sont versionnés dans le dépôt privé
**`vps-socle`** (cf. [ADR-0034](docs/architecture/decisions/0034-administration-vps-socle-versionne.md)) ;
`~/mybook` reste versionné ici.

## Intervenir sur le VPS de production (IA)

Le VPS (152.228.237.190) est **partagé** avec Life Manager
(`maison.dorian-grelu.fr`) et Keycloak (`iabilis.fr`) : casser le socle casse
tous les sites. La référence du serveur n'est pas ce dépôt mais
`/home/ubuntu/AGENTS.md` et `~/admin/` (`INVENTAIRE.md`, `PROCEDURES.md`,
`JOURNAL.md`), versionnés dans `vps-socle` (copie locale dans WSL :
`~/projets/vps-socle`). En cas de contradiction, c'est `vps-socle` qui a raison.

- **Connexion** : toujours `ssh ia@152.228.237.190` (clé `claude-deploy-mybook`),
  **jamais `ubuntu@`** — la clé n'y est plus acceptée. Les fichiers (dont
  `~/mybook`) appartiennent à `ubuntu` : travailler avec `sudo -iu ubuntu`, ou
  envoyer un script : `ssh ia@152.228.237.190 "sudo -iu ubuntu bash -s" < script.sh`.
  Les commandes `ssh ubuntu@…` de `docs/` sont destinées à Dorian, qui garde son
  propre accès.
- **Avant d'agir** : lire `/home/ubuntu/AGENTS.md`, lancer `~/admin/etat.sh`, lire
  la fin de `~/admin/JOURNAL.md`. Respecter les niveaux d'autonomie : lecture
  libre ; **déployer mybook = accord explicite de Dorian** ; Caddy, PostgreSQL,
  Keycloak, cron, SSH = accord explicite aussi.
- **Secrets** : ne jamais lire ni afficher `~/mybook/.env` ni aucun `.env*`,
  `~/.ssh/*`, `rclone.conf`, `~/admin/.healthchecks-url`. Pour savoir si une
  variable existe, chercher son **nom** seulement.
- **Après chaque intervention** : une entrée dans `~/admin/JOURNAL.md` (date UTC,
  qui, quoi, pourquoi, retour arrière, à vérifier) **et** un commit sur le
  serveur, en `ubuntu`, dans `/home/ubuntu` (dépôt `vps-socle`) — jamais de
  `git reset --hard`, `checkout -- .`, `stash` ni `clean` dedans. Le VPS n'a pas
  accès à GitHub : signaler à Dorian qu'un relais est à lancer depuis WSL
  (procédure 11 de `PROCEDURES.md`). Le code du site, lui, se commite ici et se
  déploie par `scripts/deploy.sh` depuis `~/mybook` (procédure 5).
- **Fail2ban** : regrouper les commandes en **une seule connexion SSH** par lot.
  Dans un script envoyé par `ssh … bash -s`, tout `docker exec` /
  `docker compose exec` / `~/infra/backup.sh` prend `</dev/null`, sinon il
  « avale » la suite du script.
- **Horaires** : serveur en UTC ; éviter d'intervenir entre 3 h et 5 h 30 UTC
  (sauvegarde 3 h, copie OneDrive 3 h 30, correctifs Keycloak 4 h 15, mises à
  jour système 5 h).
- **Supervision** : Dozzle (https://docker.dorian-grelu.fr) et Beszel
  (https://metrics.dorian-grelu.fr) sont réservés à Dorian ; une IA utilise
  `docker logs`, `docker stats`, `journalctl`. Portainer est abandonné.

Authentification back-office : Keycloak (OIDC, client confidentiel + PKCE, jetons
opaques + introspection — cf. [ADR-0033](docs/architecture/decisions/0033-sso-keycloak-backoffice.md)),
plus aucun compte local. En dev, un client Keycloak dédié suffixé `-local` sur un
royaume de test — jamais le client de prod (`KEYCLOAK_ISSUER_URI`/`CLIENT_ID`/
`CLIENT_SECRET` en variables d'environnement locales, cf. `.env.example`).

## Pièges connus (voir aussi `architecture.md §12.2`)

- **Thymeleaf Layout Dialect** : `layout:fragment="contenu"` remplace entièrement la
  balise du layout (attributs compris) par celle de la page décorée — le padding de
  page vit dans une classe (`.page-conteneur`) posée sur la racine du fragment de
  *chaque page*, jamais sur `<main>` dans le layout lui-même.
- **`th:if` + `th:replace` sur la même balise** : `th:replace` a une précédence plus
  haute, il remplace l'élément avant que `th:if` ne s'applique → toujours wrapper
  dans un élément englobant séparé.
- **Testcontainers sous Windows/Docker Desktop** : `JpaUtilisateurRepositoryTest`
  échoue localement (`Could not find a valid Docker environment`) à cause d'une
  incompatibilité connue avec le pipe nommé de Docker Desktop — sans impact en CI
  (runners Linux). Non-bloquant, ne pas chercher à corriger côté code.
- **Volume Docker d'images (`images-data`)** : monté uniquement dans le conteneur
  `app`, jamais accessible directement depuis l'hôte. Pour y accéder (scripts
  backup/restore), passer par un conteneur éphémère `docker compose run --rm
  --no-deps --entrypoint sh app ...`, qui fonctionne que `app` soit démarré ou non.
  Ne jamais faire `rm -rf` sur `/data/images` lui-même (c'est le point de montage,
  *Device or resource busy*) — vider son contenu avec `find /data/images -mindepth 1
  -delete`.
- **Slugs SEO** (univers/collections/livres) : générés une seule fois à la création,
  jamais recalculés lors d'une modification, pour ne pas casser un lien déjà
  indexé/partagé.
- **Bind mount d'un *fichier* unique** : le conteneur suit l'**inode** monté au
  démarrage, pas le chemin. `git pull` ne modifie pas le fichier en place, il le
  **remplace** par un nouvel inode → le conteneur sert l'ancienne version
  indéfiniment, et `docker compose up -d` ne le recrée pas (définition de service
  inchangée). Symptôme trompeur : `"config is unchanged"` dans les logs de Caddy
  alors que le fichier a bien changé sur l'hôte. Ce piège a coûté une demi-journée
  sur le `Caddyfile` ; depuis [ADR-0030](docs/architecture/decisions/0030-socle-vps-partage.md)
  on monte un **répertoire** (`conf.d/`) au lieu d'un fichier, ce qui l'élimine —
  mais la règle reste valable pour tout futur bind mount de fichier.
- **Bash tool lent sur cet environnement Windows** : préférer PowerShell pour les
  commandes shell (git, docker, mvn, manipulation de fichiers) quand la latence
  compte.
- **Transfert de scripts vers le VPS : utiliser `scp`, jamais un pipe PowerShell.**
  Un `Get-Content ... | ssh "cat > fichier.sh"` ajoute un **BOM UTF-8** en tête.
  Le noyau ne reconnaît alors plus la ligne shebang et exécute le script avec
  `sh` : les constructions bash échouent sur un `Bad substitution` très trompeur,
  et on voit passer un `#!/bin/sh: not found` facile à prendre pour du bruit.
  Vérification : `head -c 2 fichier.sh | od -An -tx1` doit donner `23 21`.
