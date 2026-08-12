# ADR-0004 — Page Actualités plutôt qu'un module blog

**Statut** : Acté
**Date** : 2026-08-12

## Contexte

La demande initiale évoquait une « page type blog » gérable en autonomie. Le cahier
des charges détaillé fourni par l'auteur ne décrit en réalité qu'une page
**Actualités** : événements à venir (salons, dédicaces) triés chronologiquement, plus
une courte section d'actualités passées sélectionnées manuellement — pas d'articles
longs, pas de catégories/tags, pas d'éditeur de texte riche.

## Décision

Implémenter uniquement le domaine `actualite` tel que décrit dans le cahier des
charges (voir [domain-model.md](../domain-model.md#domaine-actualite)). Pas de module
blog séparé en v1.

## Alternatives envisagées

- **Blog complet en plus de la page Actualités** : écarté pour la v1 — périmètre non
  demandé explicitement par l'auteur, ajouterait de la complexité (éditeur riche,
  modération de commentaires éventuels) sans besoin exprimé.
- **Fusionner Actualités et blog dans un seul modèle générique** : écarté — le besoin
  actuel est simple et structuré (date, lieu, horaire, lien billetterie) ; un modèle
  générique de blog serait une sur-ingénierie prématurée pour ce périmètre.

## Conséquences

- Si un vrai blog (articles longs) devient nécessaire plus tard, ce sera un nouveau
  domaine `blog` à part entière, pas une extension du domaine `actualite` — à
  documenter via un nouvel ADR le moment venu.
- Le brief fonctionnel et la roadmap reflètent ce choix (pas de tâche « blog » en v1).
