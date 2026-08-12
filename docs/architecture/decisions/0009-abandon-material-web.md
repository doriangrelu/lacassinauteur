# ADR-0009 — Abandon de Material Web, design system Tailwind natif + icônes inline

**Statut** : Acté — remplace la majeure partie d'[ADR-0007](0007-material-web-backoffice.md)
**Date** : 2026-08-12

## Contexte

[ADR-0007](0007-material-web-backoffice.md) avait retenu Material Web Components pour
l'apparence du back-office. Un premier amendement avait déjà écarté ses champs de
texte (`md-outlined-text-field`) pour cause de fiabilité de saisie. En poursuivant le
travail de finition visuelle (retour utilisateur : marges/alignements à soigner,
polices incohérentes, besoin d'icônes), un second défaut est apparu : les composants
Material Web restants (`md-filled-button`) embarquent leur propre typographie
(`font-family: Roboto` par défaut du design system Material), non chargée ni définie
dans le projet. Résultat : le bouton affichait une police différente — et dégradée en
absence de `Roboto` installée — de tout le reste de la page, créant l'incohérence
typographique remontée.

Deux défauts de fiabilité/cohérence sur la même dépendance, pour un gain visuel
finalement marginal (un seul type de composant encore utilisé), ne justifient plus son
maintien.

## Décision

Retirer entièrement Material Web du back-office. Le remplacer par :

- **Un système de composants Tailwind natif**, défini dans
  `frontend/backoffice.css` via `@layer components` (`.btn`, `.btn-primary`,
  `.btn-danger`, `.card`, `.badge`, `.champ-texte`, `.champ-label`, etc.) — cohérent
  avec ce qui existait déjà pour les champs de formulaire.
- **Une police explicitement harmonisée** : un seul empilement de polices système
  (`-apple-system, "Segoe UI", Roboto, ...` via le token Tailwind `--font-sans`),
  appliqué uniformément à tous les éléments (titres compris — la hiérarchie visuelle
  vient désormais du poids et de la taille, pas d'un changement de police). Pas de
  référence à `font-display`/Cormorant Garamond dans le back-office : cette identité
  typographique est réservée au site public (cf. brief fonctionnel §8), pas à l'outil
  d'administration.
- **Des icônes en SVG inline**, sourcées depuis [Heroicons](https://heroicons.com/)
  (MIT, par les créateurs de Tailwind — cohérence naturelle), intégrées comme
  fragments Thymeleaf réutilisables dans
  `templates/backoffice/fragments/icons.html`. Aucune dépendance JS, aucun appel
  réseau : les SVG sont statiques, héritent de `currentColor`, et se comportent comme
  n'importe quel élément HTML (pas de Shadow DOM, pas de risque d'interaction cassée).

## Conséquences

- Le back-office n'a plus **aucune dépendance JS externe** au-delà de htmx (déjà
  vendorisé en local). Suppression du `<script type="module" src="https://esm.run/...">`
  dans `layout-backoffice.html` et `connexion.html`.
- `ADR-0007` reste comme trace historique de la décision initiale et de son premier
  amendement, mais n'est plus la source de vérité sur le sujet.
- Tout nouveau besoin de composant riche (dialogue, snackbar...) sera d'abord tenté en
  HTML natif + htmx avant d'envisager une dépendance JS, au vu de l'expérience ci-dessus.
