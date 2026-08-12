# Modèle de domaine

> Découpage en domaines métiers (bounded contexts) et entités principales, dérivé du
> [brief fonctionnel](../business/brief.md). Chaque domaine correspond à un package
> racine (voir [package-structure.md](package-structure.md)).

## Vue d'ensemble

| Domaine | Responsabilité | Dépend de |
|---|---|---|
| `catalogue` | Univers, collections, livres, avis lecteurs, page pro | `identity` (auteur des actions), `shared` |
| `actualite` | Événements et actus (à venir / passées) | `shared` |
| `newsletter` | Abonnés, consentement, envoi de campagnes | `shared` |
| `contact` | Messages entrants du formulaire de contact | `shared` |
| `identity` | Comptes back-office, rôles, authentification | `shared` |
| `shared` | Kernel technique commun (pas de logique métier) | — |

Les domaines ne s'appellent pas directement entre eux par leurs classes internes : une
communication inter-domaines (rare dans ce projet) passe par un port exposé
explicitement, jamais par un import direct des classes `application`/`domain` d'un
autre module.

## Domaine `catalogue`

Le cœur du site : la présentation de l'œuvre de l'auteur.

### Entités / value objects

- **`Univers`** — id, nom, sous-titre/accroche, texte de présentation, photo
  d'illustration, ordre d'affichage.
- **`Collection`** — id, univers parent, nom, sous-titre, texte de présentation, ordre
  d'affichage.
- **`Livre`** — id, collection parente, titre, sous-titre, couverture, pitch court,
  résumé, `LienAchat` (value object : URL + libellé marchand) *ou* statut
  `BientotDisponible`, ordre d'affichage dans la collection, `FicheProfessionnelle`
  (value object optionnel : ISBN, format, pagination, nombre de pages, prix, lieux,
  pitch éditeur, synopsis éditeur), date de publication.
- **`AvisLecteur`** — id, livre concerné, nom de l'auteur de l'avis, texte, note
  éventuelle, statut (`EN_ATTENTE` / `PUBLIE` / `REJETE`), date de soumission.

### Cas d'usage principaux (indicatif, affiné en implémentation)

- Gérer le catalogue : créer/modifier/réordonner un univers, une collection, un livre.
- Publier un livre (renseigner le lien d'achat, sortir du statut « bientôt
  disponible »).
- Mettre en avant un livre sur la page d'accueil (« dernière parution »).
- Soumettre un avis lecteur (public) / modérer un avis (back-office).
- Consulter le catalogue public par univers / par collection / par livre.
- Consulter la fiche professionnelle d'un livre (page pro, livres publiés uniquement).

## Domaine `actualite`

- **`Actualite`** — id, titre, texte court, date (ou plage date/heure pour un
  événement), lieu (optionnel), lien billetterie/organisateur (optionnel), image
  (optionnel), type (`EVENEMENT_A_VENIR` / `ACTUALITE_PASSEE`, dérivé de la date sauf
  archivage manuel).

### Cas d'usage principaux

- Créer/modifier/supprimer une actualité.
- Lister les événements à venir (tri chronologique croissant).
- Lister les actualités passées mises en avant (sélection manuelle, pas tout
  l'historique).

## Domaine `newsletter`

- **`AbonneNewsletter`** — id, prénom, email, statut de consentement
  (`EN_ATTENTE_CONFIRMATION` / `CONFIRME` / `DESINSCRIT`), date d'inscription, date de
  confirmation, jeton de confirmation/désinscription.
- **`CampagneNewsletter`** (v1 minimal, potentiellement délégué en grande partie à
  l'outil tiers) — id, sujet, contenu, date d'envoi, statut.

### Cas d'usage principaux

- S'inscrire à la newsletter (double opt-in : email de confirmation).
- Confirmer l'inscription via le lien reçu par email.
- Se désinscrire (lien présent sur chaque envoi).
- Synchroniser les abonnés confirmés avec l'ESP tiers (Brevo) — voir
  [ADR-0002](decisions/0002-fournisseur-emailing.md).

## Domaine `contact`

- **`MessageContact`** — id, nom, email, objet, message, date de réception, statut
  (`NOUVEAU` / `LU` / `TRAITE`).

### Cas d'usage principaux

- Envoyer un message de contact (public) → email transactionnel à l'auteur +
  enregistrement pour consultation ultérieure dans le back-office.
- Consulter/marquer comme traité un message (back-office).

## Domaine `identity`

- **`Utilisateur`** — id, email, mot de passe (haché), rôle (`ADMIN` / `AUTEUR`),
  statut actif/inactif.

### Cas d'usage principaux

- Authentification (délégué en grande partie à Spring Security).
- Gestion des comptes back-office (créer un compte, changer un rôle, désactiver) —
  réservé au rôle `ADMIN`.

## Domaine `shared`

Volontairement minimal : types de base réutilisables sans porter de logique métier
(ex. value object `Ordre` pour l'ordre d'affichage s'il est identique partout,
exceptions techniques transverses). **Ne doit jamais devenir un fourre-tout** — en cas
de doute, préférer dupliquer une petite classe plutôt que créer un couplage artificiel
via `shared`.
