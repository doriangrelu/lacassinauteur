# Roadmap & backlog

> Suivi vivant de l'avancement. Cocher au fil de l'eau, ajouter des sous-tâches si un
> lot se révèle plus gros que prévu. Mettre à jour ce fichier fait partie de chaque
> tâche, pas d'une passe séparée.

## Phase 0 — Socle technique

- [x] Scaffolding projet Maven (Spring Boot 4.1 / Java 25 — mis à jour depuis la
      cible initiale 3.x/21, cf. [tech-stack.md](architecture/tech-stack.md)),
      arborescence de packages conforme à [package-structure.md](architecture/package-structure.md)
- [x] Configuration `application.yml` (profils `dev`/`prod`), connexion PostgreSQL
- [x] Docker Compose local (app + PostgreSQL) pour le développement
- [x] Mise en place Flyway + première migration (schéma vide)
- [x] CI minimale (build + tests sur push)
- [x] Squelette Spring Security (login back-office, rôles `ADMIN`/`AUTEUR`, aucun
      écran métier encore protégé)

## Phase 1 — Domaine `identity`

- [x] Entité `Utilisateur`, port `UtilisateurRepository`, persistance JPA
- [x] Authentification effective (`UserDetailsService`, hachage BCrypt), suppression
      de l'utilisateur en mémoire temporaire (`application-dev.yml`)
- [x] Anti brute-force Bucket4j sur `/backoffice/connexion`
      ([ADR-0008](architecture/decisions/0008-anti-bruteforce-bucket4j.md)) — vérifié
      manuellement (429 après quelques tentatives ratées)
- [x] Use case création de compte back-office (réservé `ADMIN`)
- [x] Mise en place Tailwind CLI standalone (`frontend/public.css`,
      `frontend/backoffice.css`, script de build local documenté) —
      [ADR-0006](architecture/decisions/0006-tailwind-cli-standalone.md)
- [x] Layout back-office (`layout-backoffice.html`) + design system Tailwind natif +
      icônes SVG inline (Material Web essayé puis abandonné pour fiabilité, cf.
      [ADR-0009](architecture/decisions/0009-abandon-material-web.md))
- [ ] Layout public minimal (`layout-public.html`), même s'il n'est pas encore
      rempli de contenu (préparé pour la Phase 2)
- [x] Écran de connexion back-office
- [x] Écran back-office de gestion des comptes (liste, création, changement de rôle,
      désactivation, réactivation)

## Phase 2 — Domaine `catalogue`

- [ ] Modèle domaine (`Univers`, `Collection`, `Livre`, `AvisLecteur`) + ports +
      persistance JPA + migrations Flyway
- [ ] Use cases CRUD univers/collection/livre + réordonnancement
- [ ] Use case « définir la dernière parution » (bloc accueil)
- [ ] Pages publiques : accueil, univers (×2), collection (×4), livre (×7 initial)
- [ ] Back-office : gestion univers/collections/livres, upload des couvertures
- [ ] Page professionnelle (route non listée, accessible par QR code) — fiche
      technique des livres publiés
- [ ] Use cases avis lecteurs (soumission publique + modération back-office),
      protection anti-spam (honeypot)
- [ ] Import des 7 livres et 4 collections existants depuis
      `docs/business/source/` (textes, synopsis, couvertures)

## Phase 3 — Domaine `actualite`

- [ ] Modèle domaine + persistance + migrations
- [ ] Use cases CRUD + listing (à venir / passées)
- [ ] Page publique Actualités
- [ ] Back-office gestion des actualités

## Phase 4 — Domaine `newsletter`

- [ ] Choix définitif de l'ESP (Brevo par défaut, cf.
      [ADR-0002](architecture/decisions/0002-fournisseur-emailing.md)), création du
      compte, clé API
- [ ] Modèle domaine (`AbonneNewsletter`) + persistance + migrations
- [ ] Use cases inscription (double opt-in), confirmation, désinscription
- [ ] Adaptateur `infrastructure.email` vers l'ESP
- [ ] Page publique Newsletter
- [ ] Back-office : liste des abonnés, déclenchement/consultation des campagnes

## Phase 5 — Domaine `contact`

- [ ] Modèle domaine (`MessageContact`) + persistance + migrations
- [ ] Use case envoi de message (email transactionnel + enregistrement)
- [ ] Page publique Contact
- [ ] Back-office : liste des messages reçus, marquage traité

## Phase 6 — Intégration graphique

- [ ] Layout Thymeleaf commun (header, footer avec réseaux sociaux), respect de la
      charte (Cormorant Garamond / Aptos, palette blanc/noir/gris) — voir
      [brief §8](business/brief.md#8-identité-graphique-contraintes-pour-lintégration)
- [ ] Intégration des maquettes/exemples de ton pour l'ambiance visuelle
- [ ] Responsive (mobile-first, le site sera consulté en majorité sur mobile pour un
      public de lecteurs)
- [ ] Vérification navigation « pas de cul-de-sac » sur toutes les pages

## Phase 7 — Déploiement Hetzner

- [ ] Provisionnement VPS Hetzner
- [ ] Achat + configuration DNS de `thierrylacassin-auteur.fr`
- [ ] Docker Compose prod (app + PostgreSQL + reverse proxy Caddy/HTTPS)
- [ ] Sauvegardes automatiques de la base (dump planifié + rétention)
- [ ] Mise en ligne, vérification SEO de base (sitemap, meta descriptions, balises OG)

## Phase 8 — Recette avec l'auteur

- [ ] Formation rapide de Thierry à l'usage du back-office
- [ ] Recette fonctionnelle complète (parcours visiteur + parcours auteur)
- [ ] Ajustements retours

## v2 (backlog, non détaillé)

- [ ] Personnalisation avancée de la mise en page/du thème par l'auteur (cf. brief
      §9) — à cadrer une fois le socle v1 en production et utilisé.

## Décisions en attente

- Fournisseur d'email transactionnel pour le formulaire de contact (Gmail existant vs
  SMTP transactionnel dédié) — à trancher en Phase 5.
- Nom exact du prestataire ESP newsletter (Brevo vs Mailjet) — à confirmer en Phase 4.
