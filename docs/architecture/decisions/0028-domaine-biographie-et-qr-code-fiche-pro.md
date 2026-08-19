# ADR-0028 — Domaine `biographie` pour la page Auteur, et QR code des fiches professionnelles

**Statut** : Acté
**Date** : 2026-08-19

## Contexte

Une revue du brief face au code a révélé deux besoins exprimés par l'auteur et
jamais livrés :

1. La page **« Auteur »** ([brief §5](../../business/brief.md)) : « Photo + texte
   de présentation court », gérable en back-office. Aucune route, aucun template,
   aucun modèle n'existait — et elle ne figurait pas non plus dans la roadmap :
   l'oubli datait du cadrage, pas de l'implémentation. Le texte et la photo
   étaient pourtant déjà fournis (`docs/business/source/Texte.docx` §« Page
   auteur », `Photos/Portraits/Page auteur.jpg`, cette dernière ayant même été
   copiée dans les ressources statiques… puis référencée nulle part).
2. Le **QR code** des fiches professionnelles. La page pro
   (`/livres/{slug}/pro`) existait déjà et était correcte, mais le brief précise
   « accessible par URL directe (QR code) » : l'auteur n'avait aucun moyen
   d'obtenir ce QR code, et devait copier l'URL vers un générateur externe.

## Décision

### Un domaine `biographie`, distinct de `catalogue`

La page Auteur n'entre dans aucun des cinq domaines existants (`identity`,
`catalogue`, `actualite`, `newsletter`, `contact`) : ce n'est ni un élément de
catalogue, ni une actualité. Sixième domaine métier, suivant exactement le même
schéma de sous-packages que les autres.

**Nommé `biographie`, pas `auteur`** : le rôle applicatif `AUTEUR` existe déjà
dans `identity`, et un domaine homonyme créerait une ambiguïté permanente à la
lecture (« l'auteur » le domaine vs « AUTEUR » le rôle). `presentation` était
exclu d'office — c'est déjà le nom d'une couche dans chaque domaine.

L'URL publique reste `/auteur` et l'intitulé reste « L'auteur » : c'est le
vocabulaire du brief et celui de l'utilisateur final. Seul le nom technique du
domaine diffère.

### Enregistrement unique garanti par la base

Il n'existe qu'une seule biographie : créée une fois par le seeder, ensuite
uniquement modifiée. Plutôt que de s'en remettre à une convention applicative,
l'unicité est **garantie physiquement** par la table (colonne `ligne_unique`
toujours vraie, portant à la fois un `CHECK` et une contrainte `UNIQUE`) — une
seconde insertion est impossible, quel que soit le chemin de code emprunté.

Conséquences : le port expose `charger()` (sans identifiant) plutôt qu'un
`findById`, et le back-office n'offre ni création ni suppression, seulement la
modification — d'où l'absence de liste et de modale, contrairement au reste du
back-office.

### QR code : capacité technique dans `shared`, SVG plutôt que PNG

La génération de QR code est une capacité technique transverse, sans lien avec
un domaine métier précis : elle vit donc dans `shared`
(`GenerationQrCodePort` + `ZxingQrCodeAdapter`), exactement comme le stockage de
fichiers et la conversion WebP.

**Format SVG** : la cible est l'impression sur une plaquette. Un SVG reste net à
n'importe quelle taille, là où un PNG impose de choisir une résolution à
l'avance. Cela permet aussi de n'ajouter que l'artefact `com.google.zxing:core`
— le SVG est produit directement depuis la matrice de modules, sans recourir à
`zxing-javase` (encodage d'images matricielles).

**Dépendance assumée** : le projet limite volontairement ses dépendances (htmx a
été supprimé, cf. [ADR-0025](0025-abandon-htmx.md)). Écrire un encodeur QR
maison — masques, correction de Reed-Solomon, matrices de version — serait
toutefois disproportionné et fragile pour un besoin aussi standard. ZXing est la
référence du domaine, en Java pur, sans dépendance native.

Le QR code est **regénéré à chaque appel** plutôt que stocké : l'opération prend
quelques millisecondes, et un QR code stocké deviendrait silencieusement faux si
le slug changeait — alors qu'un QR code déjà imprimé, lui, ne peut plus être
corrigé.

L'endpoint vit dans un contrôleur dédié (`BackofficeQrCodeFicheProController`)
plutôt qu'en méthode supplémentaire de `BackofficeLivreController`, qui porte
déjà neuf dépendances et n'a rien à voir avec le service d'un fichier.

### Ce qui ne change pas côté public

La page professionnelle reste **hors des menus, non listée et `noindex`** : le
QR code est un moyen de diffusion à la main de l'auteur, pas une mise en avant
sur le site. Le back-office n'affiche QR code et lien que si la page existera
réellement (livre publié **et** fiche renseignée) — mêmes conditions que
`PageProfessionnelleController`, sans quoi on imprimerait un QR code menant à un
404, donc irrattrapable une fois la plaquette éditée.

Le `robots.txt` n'est volontairement **pas** modifié : y ajouter un `Disallow`
sur `/livres/*/pro` empêcherait Google de lire la balise `noindex` de la page et
pourrait, paradoxalement, laisser l'URL indexée si un tiers la partageait.

## Alternatives envisagées

- **Rattacher la page Auteur à `catalogue`** : écarté — une biographie n'est pas
  un élément de catalogue ; cela aurait dilué la responsabilité d'un domaine déjà
  le plus gros du projet.
- **Domaine plus large (`vitrine`, `contenu-editorial`) accueillant d'autres
  blocs de texte libres** : envisagé au vu du brief §9 (personnalisation par
  l'auteur), écarté pour l'instant — rien d'autre n'est demandé aujourd'hui, et
  élargir `biographie` plus tard sera moins coûteux que de deviner maintenant le
  bon périmètre.
- **Stocker le texte de la biographie en dur dans le template** : écarté, le
  brief demande explicitement une gestion back-office.
- **QR code en PNG** : écarté pour l'impression (résolution figée), mais
  trivialement ajoutable si l'usage réel le réclame (ajout de `zxing-javase`).
- **Génération du QR code côté navigateur (JavaScript)** : écarté — imposerait
  une bibliothèque JS externe, que la Content-Security-Policy stricte du site
  bloquerait (`script-src 'self'`), et qui irait à rebours de la suppression
  d'htmx ([ADR-0025](0025-abandon-htmx.md)).

## Conséquences

- Nouveau domaine `fr.lacassinauteur.site.biographie` (domain / application /
  infrastructure / presentation), migration Flyway `V11__biographie_init.sql`.
- Nouvelle route publique `/auteur`, ajoutée à la navigation (header + footer) —
  contrairement à la page pro, celle-ci est indexable et devra figurer dans le
  futur `sitemap.xml`.
- Nouvel écran back-office `/backoffice/auteur` et entrée de sidebar.
- La photo fournie par l'auteur passe de `static/images/univers/auteur.jpg`
  (emplacement erroné, jamais référencé) à
  `static/images/auteur/thierry-lacassin.jpg`.
- `shared` gagne `GenerationQrCodePort` et son adaptateur ZXing ; nouvelle
  dépendance Maven `com.google.zxing:core`.
- Nouvelle propriété `app.catalogue.url-base` (même valeur que
  `app.newsletter.url-base`, dupliquée pour ne pas coupler les domaines) : un QR
  code imprimé ne peut pas encoder une URL relative.
