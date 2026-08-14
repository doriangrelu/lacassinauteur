# ADR-0002 — Newsletter via fournisseur d'emailing tiers (ESP)

**Statut** : Acté (fournisseur précis à confirmer en implémentation)
**Date** : 2026-08-12

## Contexte

Le site doit permettre l'envoi d'une newsletter à une liste d'abonnés. Deux options :
SMTP auto-hébergé depuis le VPS OVHcloud, ou API d'un Email Service Provider (ESP)
tiers.

## Décision

Utiliser un **ESP tiers** (Brevo pressenti : offre gratuite généreuse, hébergement UE,
gestion native du double opt-in et de la désinscription, bonne délivrabilité).

## Alternatives envisagées

- **SMTP auto-hébergé** : écarté pour la newsletter — délivrabilité fragile
  (réputation IP à construire, configuration SPF/DKIM/DMARC à la charge du projet),
  gestion manuelle des bounces/désinscriptions à redévelopper. Reste possible pour
  l'email de contact simple (volume faible, pas d'enjeu de délivrabilité en masse).

## Conséquences

- Le domaine `newsletter` définit un port applicatif d'envoi/synchronisation
  (`EnvoiEmailPort` ou équivalent), implémenté par un adaptateur
  `infrastructure.email` dédié à Brevo — remplaçable sans toucher au métier (DIP).
- Nécessite un compte externe (Brevo) et la configuration d'une clé API en variable
  d'environnement (jamais commitée).
- Le choix définitif du fournisseur (Brevo vs Mailjet) sera confirmé avant
  l'implémentation du domaine `newsletter`, sans impact sur l'architecture (le port
  reste identique).
