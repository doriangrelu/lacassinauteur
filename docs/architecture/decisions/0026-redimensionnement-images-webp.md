# ADR-0026 — Redimensionnement des images surdimensionnées à la conversion WebP

**Statut** : Acté — modifie la partie « redimensionnement » d'[ADR-0024](0024-conversion-webp-images.md)
**Date** : 2026-08-19

## Contexte

Un audit Google Lighthouse (mobile) sur `https://thierrylacassin-auteur.fr/` a
mesuré un score performance de 90/100, avec pour cause principale un LCP à 3,2s.
L'audit `image-delivery-insight` chiffre à lui seul **285 Kio gaspillés** : les
images sont bien livrées en WebP (ADR-0024) mais **à leur résolution source**,
souvent très supérieure à leur taille d'affichage réelle — ex. la couverture
« dernière parution » de l'accueil (`de-franck-a-keller.png`, 2626×4000 px
sources) n'est jamais affichée à plus de 331×504 px CSS, gaspillant à elle seule
~173 Kio.

ADR-0024 excluait explicitement ce redimensionnement (« hors périmètre de cette
passe — ajouté seulement si un besoin réel apparaît »). Ce besoin est maintenant
mesuré.

## Décision

`CwebpConversionAdapter` lit les dimensions de l'image source via les en-têtes
(`ImageIO.getImageReaders` + `ImageReader.getWidth/getHeight(0)`, sans décoder les
pixels — rapide même sur un gros fichier) et, si sa plus grande dimension dépasse
**1600 px**, passe l'option `-resize` à `cwebp` en bornant le plus grand côté à
1600 px et en laissant `cwebp` calculer l'autre pour préserver le ratio. En
dessous du seuil, aucun redimensionnement (jamais d'agrandissement).

1600 px est très généreux pour tous les usages actuels du site (cartes univers,
couvertures de livre, visuel « dernière parution ») même en tenant compte d'un
affichage rétina 2×/3×, tout en éliminant le gaspillage mesuré par Lighthouse.

Si la lecture des dimensions échoue (format non reconnu par `ImageIO`, contenu
invalide), le redimensionnement est simplement désactivé — `cwebp` se chargera
ensuite de rejeter l'entrée si ce n'est vraiment pas une image, même logique de
dégradation gracieuse que le reste de l'adaptateur.

**Conséquence sur le cache existant** : les images du seed déjà mises en cache
(`.cache-webp/`) l'ont été avec l'ancienne logique (pas de redimensionnement). Le
cache est vidé manuellement lors du déploiement de ce changement pour qu'elles
soient régénérées à la résolution bornée — même procédure que documentée dans
ADR-0024 pour tout changement de contenu source.

## Alternatives envisagées

- **Redimensionner uniquement les visuels du seed (accueil)** : écarté — les
  couvertures uploadées par l'utilisateur via le back-office (`StockageFichierLocal`)
  passent par le même adaptateur et sont tout aussi susceptibles d'être
  surdimensionnées (photo envoyée telle quelle depuis un appareil photo/scanner) ;
  corriger au niveau de l'adaptateur couvre les deux chemins sans dupliquer la
  logique.
- **Cible de redimensionnement par usage (ex. 400 px pour les cartes univers, 600
  px pour les couvertures)** : écarté — demanderait de faire remonter le contexte
  d'usage jusqu'à l'adaptateur (violerait sa simplicité actuelle, indépendante du
  domaine appelant) pour un gain marginal ; un seuil unique généreux couvre tous
  les cas mesurés.

## Conséquences

- `CwebpConversionAdapter.DIMENSION_MAX_PIXELS = 1600`.
- Nouveau test `redimensionne_une_image_surdimensionnee` (décode le WebP produit
  via `dwebp` pour vérifier la dimension bornée), désactivé via `Assumptions` si
  `dwebp` est absent — même logique que le reste de la classe de test.
- Cache `.cache-webp/` vidé lors du déploiement de ce changement (action manuelle
  ponctuelle, pas un changement de mécanisme).
