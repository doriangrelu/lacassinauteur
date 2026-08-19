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
- [x] Layout public minimal (`layout-public.html`) — présent et utilisé depuis la
      Phase 2 (oubli de case à cocher, pas un manque fonctionnel)
- [x] Écran de connexion back-office
- [x] Écran back-office de gestion des comptes (liste, création, changement de rôle,
      désactivation, réactivation)

## Phase 2 — Domaine `catalogue`

- [x] Modèle domaine (`Univers`, `Collection`, `Livre`) + ports + persistance JPA +
      migration Flyway (V3)
- [x] Use cases CRUD univers/collection/livre — réordonnancement par
      glisser-déposer natif dans les tableaux back-office (le champ `ordre`
      numérique manuel a été retiré des formulaires, cf. backlog v2 ci-dessous,
      fait)
- [x] Use case « définir la dernière parution » (bloc accueil), transactionnel
- [x] Pages publiques : accueil, univers, collection, livre — layout public
      (Cormorant Garamond/Inter), vérifiées dans le navigateur avec le vrai contenu
- [x] Back-office : gestion univers/collections/livres (CRUD complet), upload réel
      des photos/couvertures (stockage local, chemin configurable — cf.
      [ADR-0010](architecture/decisions/0010-upload-images-stockage-local.md))
- [x] Page professionnelle (route non listée `/livres/{slug}/pro`, `noindex`) —
      fiche technique (ISBN, format, pagination, prix, lieux de distribution,
      pitch/synopsis éditeur) éditable en back-office dans le formulaire de
      modification du livre, affichée uniquement si le livre est publié et la
      fiche renseignée (404 sinon)
- [x] Use cases avis lecteurs (soumission publique sur la page livre + modération
      back-office `/backoffice/avis`, statuts en attente/publié/rejeté),
      protection anti-spam (honeypot, réutilise `HoneypotAntiSpam` déjà utilisé par
      newsletter/contact) — migration Flyway V8, fiche pro en V9
- [x] Import des 7 livres et 4 collections existants depuis
      `docs/business/source/` (textes, synopsis, couvertures) via un seeder
      idempotent (`CatalogueInitialContentSeeder`)
- [x] URLs publiques en slugs SEO-friendly (univers/collection/livre), générés une
      fois à la création et jamais modifiés ; balises `<meta description>`/Open
      Graph par page, `robots.txt` — cf.
      [ADR-0011](architecture/decisions/0011-slugs-seo-pages-erreur.md)
- [x] Pages d'erreur personnalisées (404/générique), déclinées par espace
      (public/back-office) — cf.
      [ADR-0011](architecture/decisions/0011-slugs-seo-pages-erreur.md)
- [x] Modales natives (`<dialog>`) pour les formulaires de création back-office
      (univers, collections, livres, comptes), déclenchées par un bouton
      « Ajouter », sans dépendance JS supplémentaire

## Phase 3 — Domaine `actualite`

- [x] Modèle domaine (`Actualite`) + port + persistance JPA + migration Flyway (V5) —
      le type (`EVENEMENT_A_VENIR`/`ACTUALITE_PASSEE`) est **dérivé de la date**, pas
      stocké, sauf archivage manuel (`archiveeManuellement`) qui le force ; une
      « mise en avant » (`misEnAvant`) distincte contrôle quelles actualités passées
      apparaissent en public (sélection manuelle, pas tout l'historique)
- [x] Use cases CRUD (création/modification/suppression réelle, contrairement au
      catalogue) + listing (événements à venir triés du plus proche, actualités
      passées mises en avant)
- [x] Page publique Actualités (`/actualites`), lien ajouté au footer public
- [x] Back-office gestion des actualités (`/backoffice/actualites`), même patron de
      modale native que le catalogue, lien ajouté à la sidebar
- [x] Vérifié dans le navigateur (création/modification/suppression réelles) — a
      révélé un bug réel : un champ texte optionnel laissé vide (`lienBilletterie`)
      est stocké comme chaîne vide, que Thymeleaf `th:if` traite comme « présent »,
      affichant un lien mort sur la page publique. Corrigé en normalisant les champs
      optionnels vides en `null` dans le constructeur du domaine (`Actualite`), pas
      seulement dans les use cases, pour que l'invariant tienne quel que soit le
      point d'entrée (formulaire, mapper de persistance, tests).

## Phase 4 — Domaine `newsletter`

- [x] Modèle domaine (`AbonneNewsletter`, `StatutAbonnement`) + persistance JPA +
      migration Flyway (V5) — jeton unique réutilisé confirmation/désinscription,
      cf. [ADR-0013](architecture/decisions/0013-newsletter-double-opt-in-brevo.md)
- [x] Use cases inscription (double opt-in, idempotente), confirmation,
      désinscription — testés unitairement avec repository fake
- [x] Adaptateur `infrastructure.email` vers l'ESP : `BrevoEmailAdapter` (par défaut/
      prod) + `LogEmailAdapter` (profil `dev`, parcours testable en local sans
      compte externe) — cf. [ADR-0013](architecture/decisions/0013-newsletter-double-opt-in-brevo.md)
- [x] Page publique Newsletter (`/newsletter`) : formulaire d'inscription protégé par
      honeypot, liens de confirmation/désinscription (`/newsletter/confirmer`,
      `/newsletter/desinscrire`)
- [x] Back-office : liste des abonnés (`/backoffice/abonnes`) — lecture seule en v1,
      pas d'ajout manuel (les abonnés s'inscrivent eux-mêmes)
- [x] Synchronisation des abonnés confirmés vers une liste de contacts Brevo (ajout
      à la confirmation, retrait à la désinscription, en temps réel) — Thierry
      compose et envoie ses newsletters directement depuis l'interface Brevo,
      **pas d'éditeur de campagnes maison** (décision explicite de l'utilisateur,
      cf. [ADR-0017](architecture/decisions/0017-synchronisation-brevo-campagnes.md))
- [x] **Compte Brevo réel + clé API + liste de contacts** (`BREVO_API_KEY`,
      `BREVO_LISTE_ID`) — renseignés en production (vérifié dans le `.env` du VPS
      le 2026-08-19). `BREVO_LISTE_ID` manquait initialement et a été corrigé.
      Reste à faire une **inscription réelle de bout en bout** (inscription →
      email de confirmation reçu → clic → apparition dans la liste Brevo) pour
      valider la chaîne complète : elle n'a jamais été exercée avec un vrai
      destinataire.

## Phase 5 — Domaine `contact`

- [x] Modèle domaine (`MessageContact`) + port + persistance JPA + migration Flyway
      (V7) — statuts `NOUVEAU`/`LU`/`TRAITE`, `LU` posé automatiquement à la
      consultation (cf. [ADR-0014](architecture/decisions/0014-contact-smtp-generique.md))
- [x] Use case envoi de message (enregistrement + notification email à l'auteur),
      protection honeypot (même approche que la newsletter)
- [x] Adaptateur `infrastructure.email` : SMTP générique (host/port/identifiants
      configurables, pas de fournisseur verrouillé) + adaptateur de log en dev —
      cf. [ADR-0014](architecture/decisions/0014-contact-smtp-generique.md)
- [x] Page publique Contact (`/contact`)
- [x] Back-office : liste des messages (`/backoffice/messages`), détail (marque lu),
      marquage traité
- [ ] ⚠️ **Identifiants SMTP réels** (`CONTACT_SMTP_HOST`/`CONTACT_SMTP_USERNAME`/
      `CONTACT_SMTP_PASSWORD`) — **toujours vides en production** (vérifié le
      2026-08-19). Conséquence concrète : un visiteur qui envoie un message via
      `/contact` le voit bien enregistré en base et consultable en back-office,
      mais **Thierry ne reçoit aucune notification par email** — l'envoi échoue
      silencieusement. Recommandation : réutiliser le relais SMTP Brevo
      (`smtp-relay.brevo.com:587`), la même clé SMTP que celle créée pour
      Keycloak peut servir (cf. mode-operatoire-deploiement.md §12), ce qui évite
      un troisième fournisseur.

## Phase 6 — Intégration graphique

- [x] Layout Thymeleaf commun (header, footer avec réseaux sociaux) — présent depuis
      la Phase 2 ; palette blanc/noir/gris respectée.
- [x] Polices auto-hébergées : Cormorant Garamond (titres) + Inter en remplacement
      d'Aptos, non librement redistribuable — cf.
      [ADR-0015](architecture/decisions/0015-polices-auto-hebergees.md)
- [ ] Intégration des maquettes/exemples de ton pour l'ambiance visuelle (nécessite
      une relecture des .docx fournis, non faite dans cette passe)
- [x] Responsive : vérifié en viewport mobile (375px) sur toutes les pages publiques
      (accueil, univers, actualités, newsletter, contact) — a révélé et corrigé un
      débordement horizontal réel causé par la nav ajoutée le jour même dans le
      header/footer public (liens Actualités/Newsletter/Contact ne repliaient pas
      correctement sur petit écran)
- [x] Vérification navigation « pas de cul-de-sac » : chaque page publique reste
      reliée à l'accueil (titre du header, lien de retour explicite sur les pages de
      contenu) — aucun cul-de-sac trouvé

## Phase 7 — Déploiement OVHcloud

- [x] Provisionnement VPS OVHcloud — **fait**, VPS-1 2027 (2 vCore / 4 Go RAM /
      40 Go NVMe) sous Ubuntu, accessible en SSH par clé dédiée. Pare-feu `ufw`
      activé (22/80/443 uniquement, cf. audit de sécurité).
- [x] Achat + configuration DNS de `thierrylacassin-auteur.fr` — **fait**, plus
      `leblogdethierry.fr` (redirection 301) et `iabilis.fr` (Keycloak, cf.
      [ADR-0027](architecture/decisions/0027-keycloak-iam.md)).
- [x] Docker Compose prod (app + PostgreSQL + reverse proxy Caddy/HTTPS) —
      `docker-compose.prod.yml` + `Caddyfile` + `.env.example` + `scripts/deploy.sh`,
      cf. [ADR-0016](architecture/decisions/0016-deploiement-caddy-prod.md).
      **En production et exécuté régulièrement.** Un quatrième service
      (`keycloak`) s'y est ajouté depuis, cf.
      [ADR-0027](architecture/decisions/0027-keycloak-iam.md).
- [x] Sauvegarde/restauration : `scripts/backup.sh` (`pg_dump` + archive du volume
      Docker `images-data`, via un conteneur éphémère `docker compose run`) et
      `scripts/restore.sh` (restauration gardée par une confirmation explicite,
      volontairement absente du back-office) — cf.
      [ADR-0012](architecture/decisions/0012-sauvegarde-restauration.md).
      **Planifié en cron** (`0 3 * * *`, journalisé dans `~/mybook-backup.log`) —
      24 archives présentes au 2026-08-19. Les scripts couvrent aussi la base
      Keycloak depuis ADR-0027.
- [x] Mise en ligne — **faite**, le site est en production sous HTTPS.
- [x] SEO : meta descriptions, balises Open Graph, `robots.txt`, slugs — faits
      (cf. Phase 2). Balise de vérification Google Search Console posée dans le
      layout public le 2026-08-19.
- [ ] ⚠️ **`sitemap.xml` absent** (vérifié : HTTP 404 en production). `robots.txt`
      répond bien mais ne référence donc aucun sitemap. À générer dynamiquement
      depuis le catalogue (univers/collections/livres publiés + pages fixes) —
      d'autant plus utile maintenant que l'indexation Google vient d'être
      déclenchée.

## Phase 8 — Recette avec l'auteur

- [x] Recette technique automatisée (5 agents en parallèle, HTTP + navigateur) sur
      les fonctionnalités récentes (drag-and-drop, avis lecteurs, fiche pro) —
      aucun bug fonctionnel réel trouvé. Deux faux positifs identifiés et écartés
      après vérification du code : lien de confirmation newsletter pointant vers
      le port 8080 (c'est le port par défaut du profil dev, l'instance de test
      tournait volontairement sur 8090 pour ne pas entrer en conflit) et blocages
      anti-bruteforce répétés (plusieurs agents de test partageaient la même IP et
      le même compte admin, épuisant le même compartiment Bucket4j — la protection
      fonctionne comme prévu, cf. [ADR-0008](architecture/decisions/0008-anti-bruteforce-bucket4j.md)).
      Couverture partielle : plusieurs scénarios (réordonnancement avec
      persistance vérifiée, honeypot avis lecteurs, CRUD actualité, contact) n'ont
      pas pu être testés dans le temps imparti à cause de ce même effet de bord —
      à refaire en session dédiée si une recette plus poussée est souhaitée avant
      la mise en ligne.
- [ ] Formation rapide de Thierry à l'usage du back-office
- [ ] Recette fonctionnelle complète (parcours visiteur + parcours auteur)
- [ ] Ajustements retours

## Phase 9 — Keycloak (IAM) — cf. [ADR-0027](architecture/decisions/0027-keycloak-iam.md)

- [x] Service `keycloak` 26.7 dans `docker-compose.prod.yml` (même instance
      Postgres, base/rôle dédiés), exposé par Caddy sur `iabilis.fr` avec
      en-têtes de sécurité, en production et `healthy`.
- [x] Thème de connexion `lacassin-boat` (logo bateau SVG, masque toute mention
      de Keycloak) versionné et monté dans le conteneur.
- [x] `backup.sh`/`restore.sh` étendus à la base `keycloak`.
- [ ] **Sélectionner le thème** dans la console (Realm Settings → Themes → Login
      theme) — action manuelle, non automatisable sans import de royaume.
- [ ] **Vérifier le rendu du thème** dans un navigateur et ajuster les sélecteurs
      CSS si besoin : ils ont été écrits d'après le thème `keycloak.v2` sans
      avoir pu inspecter la page réelle (limite connue de l'ADR-0027).
- [ ] **Configurer l'envoi d'email** (authentification DNS du domaine chez Brevo
      + clé SMTP + réglages Keycloak) — cf. mode-operatoire-deploiement.md §12.
- [ ] Changer le mot de passe du compte admin bootstrap et lui renseigner une
      adresse email (prérequis du bouton « Test connection »).
- [ ] Panneau web de supervision du serveur, authentifié via Keycloak — **à
      cadrer**, rien n'est encore décidé (périmètre, métriques, techno).
- [ ] Rattacher le back-office du site à Keycloak (SSO OIDC) — remplacerait
      l'authentification maison du domaine `identity`. Report explicite de
      l'utilisateur, à cadrer.

## Phase 10 — Page Auteur et QR codes — cf. [ADR-0028](architecture/decisions/0028-domaine-biographie-et-qr-code-fiche-pro.md)

Deux besoins du brief jamais livrés, retrouvés en confrontant le brief au code.

- [x] **US-1 — Page publique `/auteur`** (photo + texte), nouveau domaine
      `biographie`, contenu initial repris de `docs/business/source/Texte.docx`
      (§ « Page auteur ») et de la photo fournie. Indexable, ajoutée au header et
      au footer.
- [x] **US-2 — Édition en back-office** (`/backoffice/auteur`) : texte et photo,
      réutilise le pipeline d'upload existant (stockage local + WebP +
      redimensionnement). Ni création ni suppression : enregistrement unique.
- [x] **US-3 — QR code des fiches professionnelles** : affiché et téléchargeable
      (SVG imprimable) dans le formulaire de modification du livre, uniquement si
      la page pro existera réellement. La page pro reste `noindex` et hors menus —
      c'est l'auteur qui diffuse le QR code.
- [ ] Recette utilisateur en local, puis déploiement.

## Phase 11 — Conformité légale et référencement

### US-4 — Informations légales éditables

> **En tant que** Thierry, **je veux** modifier les informations qui figurent dans
> les pages légales (mon identité, mon adresse de contact, l'hébergeur),
> **afin de** les tenir à jour sans intervention technique.

- Le **texte** des pages légales est figé (dans les gabarits) : seules les
  **variables** sont éditables — éditeur, statut, adresse, email de contact,
  directeur de publication, hébergeur, durée de conservation des données.
- Écran `/backoffice/informations-legales`, enregistrement unique (ni création
  ni suppression), même patron que la page auteur.

### US-5 — Pages légales publiques

> **En tant que** visiteur, **je veux** savoir qui édite le site et ce qu'il fait
> de mes données, **afin de** pouvoir exercer mes droits.

- `/mentions-legales` — obligation LCEN : identité de l'éditeur, directeur de
  publication, hébergeur, contact.
- `/confidentialite` — obligation RGPD : données collectées (newsletter, contact,
  avis lecteurs), finalités, base légale, durées de conservation, destinataires
  (Brevo), droits et modalités d'exercice.
- Liens dans le pied de page, pages indexables.

### US-6 — Recueil du consentement RGPD

> **En tant que** visiteur, **je veux** consentir explicitement avant de laisser
> mes données, **afin de** garder la main sur leur usage.

- Case à cocher **obligatoire et non pré-cochée** sur les formulaires newsletter
  et contact, avec lien vers `/confidentialite`.
- Pas de colonne supplémentaire en base : le formulaire ne peut pas aboutir sans
  la case, donc les dates déjà enregistrées (`date_inscription`,
  `date_reception`) datent de fait le consentement — une colonne toujours vraie
  n'apporterait aucune preuve de plus.

### US-7 — `sitemap.xml`

> **En tant que** moteur de recherche, **je veux** un sitemap, **afin d'**indexer
> le site complètement et rapidement.

- `/sitemap.xml` généré depuis le catalogue (univers, collections, livres
  publiés) + pages fixes (accueil, auteur, actualités, newsletter, contact,
  pages légales).
- **Exclut** les fiches professionnelles `/livres/{slug}/pro` (`noindex`, cf.
  ADR-0028), le back-office et les pages d'erreur.
- Référencé depuis `robots.txt`.

## Sécurité — cf. dossier d'audit du 2026-08-18

- [x] C-2 : pare-feu `ufw` activé (22/80/443 uniquement).
- [x] C-3 : Content-Security-Policy stricte (a incidemment révélé et fait
      supprimer htmx, dépendance morte — cf.
      [ADR-0025](architecture/decisions/0025-abandon-htmx.md)).
- [x] C-4 : permissions des sauvegardes restreintes.
- [ ] **C-1 : authentification SSH par mot de passe encore active** — mise de
      côté explicitement par l'utilisateur, à reprendre plus tard (passage en
      clé uniquement + `PasswordAuthentication no`).
- [ ] C-5 à C-7 : durcissements de routine (COOP, `preload` HSTS…), faible
      priorité.

## v2 (backlog, non détaillé)

- [ ] Personnalisation avancée de la mise en page/du thème par l'auteur (cf. brief
      §9) — à cadrer une fois le socle v1 en production et utilisé.
- [x] Réordonnancement par glisser-déposer (drag-and-drop) dans les tableaux
      back-office (univers, collections, livres) à la place du champ `ordre`
      numérique — fait plus tôt que prévu (cf. Phase 2 ci-dessus).

## Décisions en attente

- **Périmètre du panneau de supervision** (Phase 9) : que veut-on y voir
  exactement (état des conteneurs, espace disque, dernières sauvegardes,
  santé applicative…) et jusqu'où va-t-on (lecture seule vs actions) ? Rien
  n'est tranché.
- **Bascule du back-office vers Keycloak** : remplacer l'authentification maison
  (`identity`) ou faire cohabiter les deux ? Impacte directement le sort du
  domaine `identity` (comptes, mot de passe oublié, anti brute-force Bucket4j),
  aujourd'hui pleinement fonctionnel — la bascule est donc un vrai arbitrage,
  pas une évidence.

*(Décisions closes : fournisseur email transactionnel → Brevo pour tout, cf.
[ADR-0027](architecture/decisions/0027-keycloak-iam.md) ; ESP newsletter →
Brevo, clé et liste renseignées en production.)*
