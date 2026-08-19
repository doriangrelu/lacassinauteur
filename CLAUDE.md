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
  `auteur` pour ne pas entrer en collision avec le rôle `AUTEUR` d'`identity`, cf.
  ADR-0028. `legal` porte les variables des pages légales, cf. ADR-0029.)
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

# Stack complète (app + db), comme en production
docker compose up -d

# Compilation Tailwind (watch, en dev — binaire standalone dans tools/, gitignoré)
tools/tailwindcss.exe -i frontend/public.css -o src/main/resources/static/css/public.css --watch
tools/tailwindcss.exe -i frontend/backoffice.css -o src/main/resources/static/css/backoffice.css --watch

# Sauvegarde / restauration (DB + images, cf. ADR-0012)
scripts/backup.sh
scripts/restore.sh <archive.tar.gz>
```

Identifiants de dev (profil `dev` uniquement, jamais en prod) :
`admin@lacassinauteur.local` / `admin123` (créés par `DevUtilisateurSeeder`).

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
- **`Caddyfile` en bind mount** : le conteneur suit l'**inode** monté au démarrage,
  pas le chemin. `git pull` ne modifie pas le fichier en place, il le **remplace**
  par un nouvel inode → le conteneur sert l'ancienne version indéfiniment, et
  `docker compose up -d` ne le recrée pas (définition de service inchangée). Un
  `caddy reload` ne sauve pas non plus : il recharge fidèlement l'ancien fichier
  (symptôme trompeur : `"config is unchanged"` dans les logs alors que le fichier
  a bien changé sur l'hôte). `scripts/deploy.sh` force donc désormais
  `up -d --force-recreate caddy` à chaque déploiement — si tu modifies le
  `Caddyfile` hors de ce script, pense à recréer le conteneur toi-même.
- **Bash tool lent sur cet environnement Windows** : préférer PowerShell pour les
  commandes shell (git, docker, mvn, manipulation de fichiers) quand la latence
  compte.
