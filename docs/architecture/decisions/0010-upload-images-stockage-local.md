# ADR-0010 — Upload d'images avec stockage local, chemin configurable

**Statut** : Acté
**Date** : 2026-08-12

## Contexte

Les photos d'univers et couvertures de livres étaient saisies comme un simple chemin
texte dans le formulaire back-office. Thierry doit pouvoir **téléverser** ses images
directement, sans manipuler de chemin de fichier.

Le site est un « petit monolithe » déployé sur un unique VPS Hetzner via Docker
Compose (cf. [tech-stack.md](../tech-stack.md)) : pas de besoin, à ce stade, d'un
stockage objet externe (S3-compatible) — un disque local suffit, à condition qu'il
survive aux redéploiements de l'application.

## Décision

- **Port applicatif** `shared.domain.port.StockageFichierPort`
  (`enregistrer(contenu, nomOriginal, sousDossier)` / `supprimerSiGere(url)`),
  implémenté par `shared.infrastructure.stockage.StockageFichierLocal` qui écrit sur
  disque, à un **chemin configurable** (`app.stockage.images.chemin`, variable
  d'environnement `STOCKAGE_IMAGES_CHEMIN`) — par défaut `./data/images` en local,
  `/data/images` en conteneur.
- Les fichiers sont servis via un préfixe d'URL dédié et lui aussi configurable
  (`app.stockage.images.prefixe-url`, par défaut `/media`), mappé sur ce dossier via
  `WebMvcConfigurer.addResourceHandlers` (`config.WebConfig`) — **distinct** de
  `/images/**`, qui reste réservé aux visuels fournis avec l'application (contenu du
  seed initial, packagés dans le jar, cf.
  [`CatalogueInitialContentSeeder`](../../business/brief.md)).
- **Docker Compose** : volume nommé `images-data` monté sur `/data/images` dans le
  service `app`, pour que les images téléversées survivent aux redéploiements
  (`docker compose up` avec une nouvelle image ne perd pas le contenu).
- Le nom de fichier stocké est **toujours régénéré** (UUID + extension validée contre
  une liste blanche : jpg/jpeg/png/webp/gif) — jamais le nom fourni par
  l'utilisateur, pour éviter tout risque de traversée de chemin ou de collision.
- Taille max par fichier : 5 Mo (`spring.servlet.multipart.max-file-size`), avec un
  message d'erreur convivial (`shared.web.GestionnaireErreursGlobal`) plutôt qu'une
  page d'erreur brute.
- Remplacer une image (modifier univers/livre avec un nouveau fichier) supprime
  l'ancienne **si et seulement si** elle est gérée par ce stockage
  (`supprimerSiGere` ignore silencieusement les chemins `/images/**` du seed initial).

## Alternatives envisagées

- **Stockage objet S3-compatible** (ex. Hetzner Object Storage) : écarté pour l'instant
  — complexité et coût superflus pour le volume d'images d'un site auteur ; le port
  `StockageFichierPort` permet d'introduire cette implémentation plus tard sans
  toucher aux use cases ni à la présentation, si le besoin apparaît (montée en charge,
  multi-instance).
- **Stocker les images en base (BYTEA/BLOB)** : écarté — alourdit la base et ses
  sauvegardes pour un gain nul ; le filesystem (ou un stockage objet plus tard) est
  l'outil adapté à des fichiers binaires servis tels quels.

## Conséquences

- `CreerUniversCommand`/`ModifierUniversCommand` et `CreerLivreCommand`/
  `ModifierLivreCommand` transportent désormais des `byte[]` (contenu du fichier) au
  lieu d'une URL texte ; c'est l'use case qui orchestre l'appel au port et la
  résolution de l'URL finale (cf.
  [architecture.md §7](../architecture.md#7-principes-solid-appliqués), DIP).
- Les formulaires back-office (`UniversForm`, `LivreForm`) exposent des champs
  `MultipartFile` — seule la couche présentation connaît ce type Spring, jamais
  l'application ni le domaine.
- Le dossier de stockage local (`./data/`) est gitignoré ; en production, ce dossier
  devra être inclus dans la même stratégie de sauvegarde que la base de données
  (export/réimport, en cours de réflexion — cf. [roadmap.md](../../roadmap.md)).
