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

- **Thymeleaf** pour le rendu serveur de toutes les pages (public + back-office).
- **htmx** pour l'interactivité ciblée (formulaires sans rechargement complet,
  actions du back-office comme réordonner/publier/modérer sans page dédiée), sans
  sortir du monolithe ni introduire de build JS séparé.
- CSS : simple, sans framework lourd imposé — respecter la charte graphique (typo
  Cormorant Garamond / Aptos, palette blanc/noir/gris, cf.
  [brief fonctionnel §8](../business/brief.md#8-identité-graphique-contraintes-pour-lintégration)).
  Auto-hébergement des polices recommandé (licences le permettant) plutôt que Google
  Fonts, pour la performance et la confidentialité (pas de tiers tiers appelé au
  chargement).
- Pas de build JS (npm/webpack/vite) : htmx + JS vanilla minimal suffisent au périmètre
  v1, cohérent avec le choix « petit monolithe ».

## Base de données

- **PostgreSQL** (version stable récente, ex. 16).
- **Flyway** pour les migrations de schéma versionnées (`src/main/resources/db/migration`),
  une migration par évolution de schéma, jamais de modification manuelle en prod.
- Spring Data JPA + Hibernate comme couche d'accès aux données, encapsulée dans
  `infrastructure.persistence` de chaque domaine (jamais exposée en dehors).

## Sécurité

- **Spring Security** pour le back-office (`/backoffice/**`), formulaire de connexion
  classique (pas de SSO nécessaire pour 1-2 utilisateurs).
- Rôles `ADMIN` et `AUTEUR` (cf. [ADR-0003](decisions/0003-roles-multiples.md)).
- Mots de passe hachés avec `BCryptPasswordEncoder`.
- CSRF activé sur tous les formulaires (public et back-office) — comportement par
  défaut Spring Security à conserver.
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

## Déploiement — Hetzner

- **Cible v1** : un unique VPS Hetzner (CX22 ou équivalent suffit largement pour ce
  trafic), avec :
  - **Docker** + **Docker Compose** : un service `app` (image du monolithe Spring
    Boot), un service `db` (PostgreSQL, volume persistant), un reverse proxy
    (**Caddy** recommandé pour son HTTPS/Let's Encrypt automatique et sa configuration
    minimale — alternative : Nginx + Certbot).
  - Nom de domaine `thierrylacassin-auteur.fr` pointé vers l'IP du VPS.
  - Sauvegardes régulières de la base (dump PostgreSQL planifié, cf. roadmap).
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
