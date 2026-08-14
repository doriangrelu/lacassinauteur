# Brief fonctionnel — Site auteur Thierry Lacassin

> Synthèse du besoin exprimé dans `docs/business/source/` (Cahier des charges, maquette
> textuelle, textes, exemples de sites) et des échanges de cadrage. Ce document est la
> référence fonctionnelle ; il doit être tenu à jour à chaque évolution du besoin.

## 1. Contexte

Thierry Lacassin écrit des romans à temps perdu, publiés en auto-édition, disponibles
notamment sur Amazon. Il souhaite un site vitrine personnel pour présenter son univers
littéraire, ses livres, communiquer via une newsletter et des actualités (salons,
dédicaces), et être contacté par ses lecteurs et par des professionnels (libraires,
éditeurs).

Le site est développé par son beau-fils (le demandeur de ce projet), qui pilote la
partie technique. Une fois livré, **Thierry doit être autonome** pour gérer son
catalogue de livres, ses actualités et sa newsletter, sans intervention technique.

## 2. Domaine du web (v1)

`www.thierrylacassin-auteur.fr` — nom de domaine à acheter et configurer à la mise en
ligne.

## 3. Personas

- **Visiteur / lecteur** : découvre l'univers, les livres, s'inscrit à la newsletter,
  contacte l'auteur, consulte les actualités.
- **Auteur (Thierry, rôle `AUTEUR`)** : gère en autonomie le catalogue (univers,
  collections, livres), les actualités, la newsletter, modère les avis lecteurs.
- **Administrateur technique (rôle `ADMIN`)** : gère les comptes back-office, la
  configuration technique, a accès à tout ce que l'AUTEUR peut faire.
- **Professionnel (libraire / éditeur)** : accède à une page dédiée non référencée dans
  le menu public (lien direct via QR code sur une plaquette), avec les informations
  techniques de commande.

## 4. Structure du contenu éditorial

Le catalogue suit une hiérarchie à 3 niveaux, **librement gérable par l'auteur** (pas
figée en dur dans le code) :

```
Univers (ex. "Deux univers. Une seule façon d'écrire.")
 └─ Collection (ex. LACASSIN noir — "Les Origines")
     └─ Livre (ex. "De Franck à Keller")
```

État initial (donnée, pas contrainte technique) :

- **Univers 1** : photo "dépouillée/sobre"
  - Collection *LACASSIN noir* — "Les Origines"
  - Collection *LACASSIN territoire* — "Les enquêtes d'Eugène Cazal"
- **Univers 2** : photo "urbaine/graffiti"
  - Collection *LACASSIN trajectoire* — "Le monde selon Camille B."
  - Collection *LACASSIN ailleurs* — "Où tout est permis"

Chaque **livre** porte : titre, sous-titre optionnel, couverture, pitch court, résumé
long, avis lecteurs (voir §6), lien d'achat (Amazon ou autre marchand) ou statut
« bientôt disponible », et des champs professionnels optionnels destinés à la page
pro : ISBN, format, pagination, prix, lieux évoqués, pitch éditeur, synopsis éditeur.

## 5. Pages du site public

| Page | Contenu | Gestion back-office |
|---|---|---|
| Accueil | Présentation de l'univers global, mise en avant des 2 univers, bloc « dernière parution » modifiable | Oui (choix du livre mis en avant) |
| Auteur | Photo + texte de présentation court | Oui |
| Univers (×2) | Texte de présentation, lien vers les 2 collections, navigation vers l'autre univers | Oui (CRUD univers) |
| Collection (×4 initialement) | Texte de présentation, liste des livres (couverture, titre, pitch), navigation croisée | Oui (CRUD collection) |
| Livre (×7 initialement) | Détail complet + bouton achat / « bientôt disponible » + avis lecteurs | Oui (CRUD livre) |
| Newsletter | Formulaire inscription (prénom, email), mention RGPD | Gestion des abonnés, envoi des campagnes |
| Contact | Formulaire (nom, email, objet, message) → email à l'auteur | Consultation des messages reçus |
| Actualités | Événements à venir (date, titre, lieu, horaire, texte, photo, lien billetterie), triés du plus proche au plus lointain + section « dernières actualités » passées | CRUD événements/actus |
| Pro (Libraires/Éditeurs) | **Non listée dans le menu**, accessible par URL directe (QR code) — fiche technique des livres publiés uniquement | Alimentée par les mêmes fiches livre (champs pro) |

Navigation : aucun cul-de-sac — chaque page propose un retour à l'accueil et/ou vers le
niveau parent.

## 6. Avis lecteurs

Formulaire public sur chaque page livre. Un avis soumis est **en attente de
modération** et n'apparaît sur le site qu'après validation par l'auteur depuis le
back-office (protection anti-spam à prévoir : captcha/honeypot + limitation de
fréquence).

## 7. Newsletter

- Double opt-in recommandé (conformité RGPD).
- Envoi via un prestataire tiers (ESP) plutôt qu'un SMTP auto-hébergé, pour la
  délivrabilité (cf. [ADR-0002](../architecture/decisions/0002-fournisseur-emailing.md)).
- Désinscription en un clic, obligatoire sur chaque envoi.
- Contenu type : nouvelles parutions, dates de salons/dédicaces — envois peu fréquents.

## 8. Identité graphique (contraintes pour l'intégration)

- Typographies : **Cormorant Garamond** (titres, nom d'auteur, noms de collection,
  titres de romans, accroches) / **Aptos** (texte courant, boutons).
- Couleurs : fond blanc, texte noir, gris pour le secondaire ; pas de grands aplats
  noirs (sauf rupture ponctuelle) ; la couleur vient des couvertures et photos.
- Réseaux sociaux (Facebook, Instagram, TikTok) en pied de page, sur toutes les pages,
  avec logos, liens fournis dans `docs/business/source/Exemple de site et autres
  liens.docx`.
- Références d'ambiance (pas de contenu à copier) : sites listés dans le même fichier.
- Maquette typographique détaillée : `docs/business/source/La maquette textuelle.docx`.
- Assets fournis : couvertures dans `docs/business/source/Photos/Couverture/`,
  portraits dans `docs/business/source/Photos/Portraits/`.

## 9. Périmètre v1 vs v2

**v1 (ce projet)** : tout ce qui précède — site vitrine + back-office complet
(catalogue, actualités, newsletter, contact, avis lecteurs, page pro), déploiement sur
OVHcloud.

**v2 (backlog, non détaillé)** : personnalisation avancée de la mise en page/du thème
par l'auteur (au-delà de la simple gestion de contenu). À affiner une fois le socle v1
livré.

## 10. Hors périmètre (sauf demande explicite)

- Pas de vente/paiement en ligne sur le site : les liens d'achat renvoient vers des
  plateformes tierces (Amazon, etc.).
- Pas de blog au sens « articles longs » — remplacé par la page Actualités (voir
  [ADR-0004](../architecture/decisions/0004-actualites-vs-blog.md)).
- Pas de compte lecteur / espace personnel visiteur.
