# ADR-0003 — Rôles multiples dès le départ (ADMIN / AUTEUR)

**Statut** : Acté
**Date** : 2026-08-12

## Contexte

Le back-office sera utilisé par au moins deux profils dès la mise en production : le
développeur (maintenance, configuration technique) et Thierry Lacassin (gestion
éditoriale en autonomie).

## Décision

Deux rôles Spring Security dès la v1 :

- **`ADMIN`** : accès complet, y compris gestion des comptes back-office et
  configuration technique.
- **`AUTEUR`** : accès à tout le périmètre éditorial — catalogue (univers,
  collections, livres), actualités, newsletter, contact, modération des avis
  lecteurs. Pas d'accès à la gestion des comptes utilisateurs.

La structure du catalogue (univers/collections) n'est **pas figée en dur** : le rôle
`AUTEUR` peut créer/modifier/réorganiser librement univers, collections et livres
(cf. brief fonctionnel §4), pour rester autonome si un nouvel univers ou une nouvelle
collection voit le jour.

## Conséquences

- `identity` est un domaine à part entière dès la v1 (pas ajouté après coup).
- Les contrôleurs `presentation.backoffice` déclarent leurs contraintes de rôle via
  Spring Security (`@PreAuthorize` ou configuration de routes), domaine par domaine.
- Prévoir, en implémentation, une distinction claire dans chaque domaine entre ce qui
  est accessible aux deux rôles et ce qui est réservé à `ADMIN` (essentiellement :
  gestion des comptes `identity`).
