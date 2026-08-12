# ADR-0011 — Slugs SEO pour les URLs publiques, méta-données et pages d'erreur dédiées

**Statut** : Acté
**Date** : 2026-08-12

## Contexte

Deux défauts pénalisaient le référencement et l'expérience du site public :

- Les URLs publiques des univers/collections/livres exposaient l'identifiant
  technique (UUID) plutôt qu'un texte lisible, ce qui nuit au référencement et à la
  lisibilité des liens partagés.
- Les erreurs (404, 500) affichaient la page Whitelabel par défaut de Spring Boot,
  hors charte graphique, identique pour le site public et le back-office.

## Décision

### Slugs

- Nouveau value object `shared.domain.model.Slug` : `Slug.depuis(texte)` normalise
  (minuscule, suppression des accents/ponctuation, tirets) et
  `Slug.genererUnique(texte, existsPredicate)` ajoute un suffixe numérique en cas de
  collision.
- `Univers`, `Collection`, `Livre` portent désormais un champ `slug`, **généré une
  seule fois à la création** (`creer(...)`) et jamais modifié par `modifier(...)` —
  pour ne jamais casser un lien déjà indexé ou partagé, la stabilité du slug prime
  sur la cohérence avec un nom modifié après coup.
- Les routes publiques (`/univers/{slug}`, `/collections/{slug}`, `/livres/{slug}`)
  utilisent le slug ; les routes back-office (`/backoffice/.../{id}/modifier`)
  restent en UUID (non indexées, pas de bénéfice SEO, on limite le changement à ce
  qui est nécessaire).
- Nouveaux use cases `Consulter*ParSlugUseCase`, ports enrichis de
  `findBySlug`/`existsBySlug`, migration Flyway `V4__catalogue_slugs.sql`
  (colonnes `NOT NULL UNIQUE`).

### SEO on-page

- Chaque page publique porte un `<meta name="description">` et les balises Open
  Graph (`og:title`, `og:description`, `og:image`, `og:type`), en s'appuyant sur le
  comportement natif de Thymeleaf qui fusionne le `<head>` de la page dans celui du
  layout — pas de mécanisme de fragment dédié nécessaire.
- `robots.txt` interdit l'indexation de `/backoffice/**`.

### Pages d'erreur

- `shared.web.ErreurController` (implémente `ErrorController`) route vers un
  template selon le code HTTP et l'espace d'origine (public/back-office), déterminé
  via `RequestDispatcher.FORWARD_REQUEST_URI`.
- `catalogue.presentation.web.GestionnaireErreursCatalogue` (`@ControllerAdvice`
  scopé à `catalogue.presentation`) traduit les exceptions « introuvable » du
  catalogue (slug inconnu) en 404 avec le bon habillage — délibérément placé dans le
  module catalogue et non dans `shared`, pour que `shared` ne dépende jamais des
  exceptions d'un domaine précis (sens de dépendance).
- Templates dédiés par espace (`public/erreur-404.html`,
  `backoffice/erreur-404.html`, etc.), decorés par le layout de leur espace.

## Alternatives envisagées

- **Réécrire l'UUID en slug à chaque modification du nom** : écarté — casserait les
  liens déjà partagés/indexés à chaque renommage, contraire à l'objectif SEO visé.
- **Un seul `ErrorController` sans distinction d'espace** : écarté — le back-office
  et le site public ont des chartes visuelles différentes, une erreur back-office ne
  doit pas afficher la police/l'ambiance du site public et inversement.

## Conséquences

- Les fixtures de seed (`CatalogueInitialContentSeeder`) et les tests fournissent
  désormais un slug explicite à la création.
- Le premier paramètre des factories `Univers.creer`/`Collection.creer`/`Livre.creer`
  est le slug — tout nouvel appel doit en fournir un (généré via `Slug.genererUnique`
  dans les use cases de création, jamais tapé à la main côté présentation).
