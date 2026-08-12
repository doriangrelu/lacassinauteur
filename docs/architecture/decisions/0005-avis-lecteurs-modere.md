# ADR-0005 — Avis lecteurs via formulaire public modéré

**Statut** : Acté
**Date** : 2026-08-12

## Contexte

Le cahier des charges prévoit une section « Avis des lecteurs » sur chaque fiche
livre. Deux approches possibles : saisie manuelle par l'auteur (contenu éditorial), ou
formulaire public alimenté par les visiteurs.

## Décision

**Formulaire public** accessible sur chaque page livre, avec **modération
obligatoire** avant publication : un avis soumis passe par le statut `EN_ATTENTE` et
n'apparaît sur le site qu'après validation explicite par un utilisateur du
back-office (`ADMIN` ou `AUTEUR`).

## Conséquences

- `AvisLecteur` porte un statut (`EN_ATTENTE` / `PUBLIE` / `REJETE`), cf.
  [domain-model.md](../domain-model.md#domaine-catalogue).
- Le use case `SoumettreAvisLecteurUseCase` (public) est distinct des use cases
  `ApprouverAvisLecteurUseCase` / `RejeterAvisLecteurUseCase` (back-office).
- Protection anti-spam nécessaire dès la v1 sur ce formulaire (honeypot a minima,
  cf. [tech-stack.md](../tech-stack.md#sécurité)), car c'est le point d'entrée public
  en écriture le plus exposé du site avec le formulaire de contact.
- Le back-office doit exposer une file de modération (liste des avis `EN_ATTENTE`)
  pour que ce travail reste simple pour l'auteur.
