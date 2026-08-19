# Stack technique

## Langage & framework

- **Java 25** (LTS) — records, pattern matching, virtual threads disponibles si besoin
  futur de charge. Correspond au JDK installé sur la machine de développement et à la
  base de référence de Spring Boot 4.
- **Spring Boot 4.1.x** (Spring Framework 7 / Jakarta EE 11 — tous les imports
  `jakarta.*`, pas de `javax.*`)
  - `spring-boot-starter-web` (MVC)
  - `spring-boot-starter-thymeleaf`
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-security`
  - `spring-boot-starter-validation`
  - `spring-boot-starter-mail` (envoi transactionnel simple si non couvert par l'ESP,
    ex. email de contact)
- **Build** : Maven.

> Spring Boot 4 active CSRF plus strictement par défaut dès qu'une
> `SecurityFilterChain` explicite est déclarée (ce qui est notre cas, cf. §Sécurité) —
> cohérent avec notre exigence CSRF activé partout, mais à garder en tête pour les
> futurs appels `htmx` en écriture (jeton CSRF à transmettre).

## Frontend (dans le monolithe)

- **Thymeleaf** + **Thymeleaf Layout Dialect** pour le rendu serveur de toutes les
  pages, avec un layout et des fragments dédiés par espace (site public / back-office)
  — voir [architecture.md §12](architecture.md#12-conventions-front-end).
- **JS vanilla minimal** pour l'interactivité ciblée du back-office (modales,
  réordonnancement par glisser-déposer) — htmx, envisagé initialement
  (ADR-0001), n'a jamais été utilisé en pratique et a été retiré
  ([ADR-0025](decisions/0025-abandon-htmx.md)).
- **Tailwind CSS v4** (CLI standalone, pas de npm — cf.
  [ADR-0006](decisions/0006-tailwind-cli-standalone.md)), deux points d'entrée séparés
  (`frontend/public.css`, `frontend/backoffice.css`) compilés vers
  `static/css/public.css` / `static/css/backoffice.css`.
  - **Développement local** : télécharger une fois le binaire standalone pour son OS
    depuis les releases GitHub de Tailwind, le placer dans `tools/` (gitignoré), puis
    lancer le watcher dans un terminal séparé pendant le développement :
    ```bash
    tools/tailwindcss.exe -i frontend/public.css -o src/main/resources/static/css/public.css --watch
    tools/tailwindcss.exe -i frontend/backoffice.css -o src/main/resources/static/css/backoffice.css --watch
    ```
  - **Build Docker/CI** : le `Dockerfile` télécharge le binaire Linux et compile les
    deux CSS (`--minify`) avant `mvn package`.
- **Icônes SVG inline** (Heroicons, MIT) via des fragments Thymeleaf dans
  `templates/<espace>/fragments/icons.html` — pas de police d'icônes ni de librairie
  JS (cf. [ADR-0009](decisions/0009-abandon-material-web.md), qui documente
  l'abandon de Material Web après deux problèmes de fiabilité/cohérence constatés en
  test réel).
- Typographie du site public : Cormorant Garamond / Aptos, palette blanc/noir/gris,
  cf. [brief fonctionnel §8](../business/brief.md#8-identité-graphique-contraintes-pour-lintégration),
  déclarée en tokens de thème Tailwind (`@theme` dans `frontend/public.css`).
  Auto-hébergement des polices recommandé (licences le permettant) plutôt que Google
  Fonts, pour la performance et la confidentialité (pas de tiers appelé au
  chargement). Le back-office utilise un empilement de polices système harmonisé
  (`--font-sans` dans `frontend/backoffice.css`), volontairement distinct de
  l'identité éditoriale du site public.
- Pas de framework JS ni de bundler (React/Vue/webpack/vite), et **aucune dépendance
  JS externe** dans le back-office : du JS vanilla minimal suffit au périmètre v1,
  cohérent avec le choix « petit monolithe » (cf. ADR-0025). Le CLI Tailwind
  standalone n'introduit aucune dépendance Node/npm dans le projet (cf. ADR-0006).

## Base de données

- **PostgreSQL** (version stable récente, ex. 16).
- **Flyway** pour les migrations de schéma versionnées (`src/main/resources/db/migration`),
  une migration par évolution de schéma, jamais de modification manuelle en prod.
- Spring Data JPA + Hibernate comme couche d'accès aux données, encapsulée dans
  `infrastructure.persistence` de chaque domaine (jamais exposée en dehors).

## Stockage de fichiers (images)

- Upload d'images (photos d'univers, couvertures de livres) via
  `shared.domain.port.StockageFichierPort`, implémenté par un adaptateur **disque
  local** (`StockageFichierLocal`) — chemin configurable via
  `app.stockage.images.chemin` (variable d'environnement `STOCKAGE_IMAGES_CHEMIN`),
  cf. [ADR-0010](decisions/0010-upload-images-stockage-local.md).
- Servi via un préfixe d'URL dédié (`/media/**` par défaut,
  `app.stockage.images.prefixe-url`), distinct des visuels du seed initial
  (`/images/**`, packagés dans le jar).
- Taille max 5 Mo par fichier, formats acceptés : jpg/jpeg/png/webp/gif.
- **Conversion WebP** systématique à l'upload, et à la volée (avec cache disque)
  pour les visuels du seed servis via `/images/**` — cf.
  [ADR-0024](decisions/0024-conversion-webp-images.md).
- **Docker Compose** : bind mount (`~/volumes/images-data`, cf.
  [ADR-0023](decisions/0023-bind-mounts-volumes-visibles.md)) monté sur ce chemin
  dans le service `app`, pour survivre aux redéploiements.

## Sécurité

- **Spring Security** pour le back-office (`/backoffice/**`), formulaire de connexion
  classique (pas de SSO nécessaire pour 1-2 utilisateurs).
- Rôles `ADMIN` et `AUTEUR` (cf. [ADR-0003](decisions/0003-roles-multiples.md)).
- Mots de passe hachés avec `BCryptPasswordEncoder`.
- CSRF activé sur tous les formulaires (public et back-office) — comportement par
  défaut Spring Security à conserver, jamais désactivé.
- CORS jamais configuré de façon permissive (pas de `*`) — le monolithe SSR n'a pas
  de besoin cross-origin par conception.
- **Bucket4j** pour la limitation anti brute-force sur l'authentification back-office
  (cf. [ADR-0008](decisions/0008-anti-bruteforce-bucket4j.md)).
- Protection anti-spam des formulaires publics (newsletter, contact, avis lecteur) :
  honeypot + limitation de fréquence par IP en v1 ; CAPTCHA (ex. Cloudflare Turnstile,
  respectueux de la vie privée) en option si le spam devient un problème réel.

## Emailing

- **Newsletter et emails transactionnels liés (confirmation d'inscription,
  désinscription)** : fournisseur tiers (ESP) — cf.
  [ADR-0002](decisions/0002-fournisseur-emailing.md) pour le choix définitif
  (Brevo pressenti : offre gratuite généreuse, RGPD/hébergement UE, double opt-in
  natif).
- **Email de contact** (formulaire → boîte de l'auteur) : `spring-boot-starter-mail`
  via SMTP simple (le compte Gmail existant de l'auteur, ou un SMTP transactionnel bon
  marché type Brevo également, pour ne pas dépendre de la disponibilité d'un Gmail
  personnel) — décision précise à prendre au moment de l'implémentation du domaine
  `contact`.

## Déploiement — OVHcloud

- **Cible v1** : un unique VPS OVHcloud (gamme VPS, ex. VPS-1 — 2 vCore/4 Go RAM/
  40 Go NVMe — suffit largement pour ce trafic), avec :
  - **Docker** + **Docker Compose** : un service `app` (image du monolithe Spring
    Boot), un service `db` (PostgreSQL, volume persistant), un reverse proxy
    (**Caddy** recommandé pour son HTTPS/Let's Encrypt automatique et sa configuration
    minimale — alternative : Nginx + Certbot).
  - Nom de domaine `thierrylacassin-auteur.fr` pointé vers l'IP du VPS.
  - Sauvegardes régulières de la base (dump PostgreSQL planifié, cf. roadmap).
  - **Keycloak** (IAM, cf. [ADR-0027](decisions/0027-keycloak-iam.md)) : service
    supplémentaire sur la même instance Postgres (base dédiée), exposé via Caddy
    sur un second domaine (`iabilis.fr`), thème de connexion personnalisé.
- Pas de Kubernetes, pas de multi-nœud : hors de proportion avec le besoin (« petit
  monolithe », faible trafic attendu).
- CI/CD v1 minimal envisageable : build + tests sur push, déploiement manuel ou via un
  script SSH/`docker compose pull && up -d` déclenché à la demande — à détailler dans
  la roadmap une fois le socle applicatif posé.

## Observabilité (v1, minimal)

- Logs applicatifs structurés (Logback, format standard Spring Boot) — suffisant à ce
  stade, pas de stack ELK/Grafana pour un site de cette taille.
- `spring-boot-starter-actuator` pour un endpoint de santé (`/actuator/health`) utile
  au reverse proxy / à la supervision basique.

## Tests

- JUnit 5, AssertJ, Mockito (mocks uniquement aux frontières, pas en interne du
  domaine).
- **Testcontainers** (PostgreSQL) pour les tests d'intégration de persistance.
- `@WebMvcTest` pour les contrôleurs, `@SpringBootTest` réservé aux tests bout-en-bout
  peu nombreux.

> Note d'environnement local (Windows + Docker Desktop) : les tests Testcontainers
> peuvent échouer avec `Could not find a valid Docker environment` malgré un Docker
> Desktop fonctionnel, à cause d'une incompatibilité connue entre le client
> Testcontainers et le pipe nommé de Docker Desktop sur certaines versions Windows.
> Sans impact en CI (runners Linux). En local, si besoin : activer « Expose daemon on
> tcp://localhost:2375 without TLS » dans Docker Desktop, ou lancer les tests dans un
> environnement Linux (WSL2).
