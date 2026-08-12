# ADR-0006 — Tailwind CSS via le CLI standalone (pas de toolchain npm)

**Statut** : Acté
**Date** : 2026-08-12

## Contexte

Le site public et le back-office doivent tous deux utiliser Tailwind CSS. Tailwind
nécessite normalement une étape de compilation (scan des templates pour ne générer
que les classes utilisées). Le projet doit rester un monolithe simple à construire
avec `mvn package`/`docker build`, sans dépendance à un environnement Node.js/npm
installé et maintenu (cf. [ADR-0001](0001-rendu-hybride-thymeleaf-htmx.md), qui écarte
déjà tout build JS de type SPA).

Depuis Tailwind CSS v4, un **CLI standalone** est distribué en tant qu'exécutable
autonome (un binaire par OS/architecture), sans avoir besoin de Node.js ni npm.

## Décision

Utiliser le **CLI standalone Tailwind v4**, jamais npm/Node pour Tailwind :

- Deux points d'entrée CSS séparés, correspondant à la séparation stricte des deux
  espaces (cf. [architecture.md §12](../architecture.md#12-conventions-front-end)) :
  - `frontend/public.css` → compilé vers `src/main/resources/static/css/public.css`,
    scanne uniquement `templates/public/**`.
  - `frontend/backoffice.css` → compilé vers
    `src/main/resources/static/css/backoffice.css`, scanne uniquement
    `templates/backoffice/**`.
- **En développement local** : le binaire standalone (téléchargé une fois,
  `tools/tailwindcss.exe` sur Windows — dossier gitignoré, jamais commité) tourne en
  mode `--watch` dans un terminal séparé, pendant que l'application Spring Boot tourne
  dans un autre. Pas d'intégration dans le cycle de vie Maven en dev (plus simple, pas
  de plugin Maven exotique à maintenir).
- **En build Docker/CI** : le `Dockerfile` télécharge le binaire Linux du CLI standalone
  dans l'étape de build et compile les deux CSS (`--minify`) **avant** `mvn package`,
  pour qu'ils soient inclus comme ressources statiques normales dans le jar.

## Alternatives envisagées

- **npm + `@tailwindcss/cli` via un plugin Maven (`frontend-maven-plugin`)** : écarté —
  ajoute un sous-système Node/npm complet (registre, `node_modules`, résolution de
  dépendances JS) pour un unique besoin de compilation CSS ; le CLI standalone couvre
  exactement ce besoin sans cette lourdeur.
- **Tailwind Play CDN** (`<script src="https://cdn.tailwindcss.com">`) : écarté pour la
  production — compilation JIT côté navigateur à chaque chargement de page, non
  recommandée par Tailwind lui-même en production, et le site public doit être
  particulièrement soigné en performance/SEO.

## Conséquences

- `tools/` (binaire local) et le dossier `frontend/` (sources CSS + éventuel
  `tailwind.config.js` si des tokens de thème custom sont nécessaires) sont ajoutés au
  projet. `tools/` est gitignoré ; `frontend/` est versionné.
- Aucune dépendance `package.json`/`node_modules` dans le dépôt.
- Le README doit documenter la commande exacte pour lancer le watcher en local (voir
  [tech-stack.md](../tech-stack.md#frontend-dans-le-monolithe)).
