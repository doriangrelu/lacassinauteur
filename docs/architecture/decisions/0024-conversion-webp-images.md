# ADR-0024 — Conversion WebP à l'upload et à la volée (avec cache) via `cwebp`

**Statut** : Acté
**Date** : 2026-08-18

## Contexte

L'utilisateur a signalé des lenteurs perçues sur le site public, en lien avec le
poids des images. Deux sources d'images distinctes (cf.
[ADR-0010](0010-upload-images-stockage-local.md)) :

- Les visuels **uploadés** par le back-office (photos d'univers, couvertures de
  livres), stockés sous `/media/**` via `StockageFichierPort`.
- Les visuels du **seed initial**, packagés dans le jar sous `/images/**`
  (`src/main/resources/static/images/`) — jpg/png non optimisés.

WebP produit des fichiers nettement plus légers que jpg/png à qualité visuelle
équivalente, avec un support navigateur aujourd'hui quasi-universel.

## Décision

### Conversion à l'upload

`StockageFichierLocal.enregistrer` convertit systématiquement le contenu reçu en
WebP avant de l'écrire sur disque, sauf s'il l'est déjà ou s'il s'agit d'un GIF
(potentiellement animé — `cwebp` ne gère pas l'animation, le convertir perdrait
silencieusement les GIF animés). Si la conversion échoue pour n'importe quelle
raison, le fichier d'origine est conservé tel quel — **la conversion est une
optimisation, jamais une condition de succès de l'upload**, même logique déjà
appliquée à l'envoi d'e-mail (cf. `InscrireAbonneUseCase`) : un effet de bord
défaillant ne doit jamais transformer une action réussie en échec.

### Conversion à la volée (+ cache) pour les visuels du seed

`shared.web.ImageStatiqueController` intercepte `/images/**` (prioritaire sur la
résolution de ressources statiques par défaut de Spring Boot, un contrôleur
explicite passe toujours avant) :

- Si le client envoie `Accept: image/webp`, sert une version WebP — convertie à la
  première requête, puis **mise en cache sur disque**
  (`<STOCKAGE_IMAGES_CHEMIN>/.cache-webp/`, donc dans le volume `images-data`
  existant, persistant entre redéploiements) pour ne plus jamais recalculer.
- Sinon (client n'annonçant pas WebP), sert l'original tel quel.
- Si la conversion échoue, se rabat sur l'original — même principe de dégradation
  gracieuse que côté upload.

**Limite connue** : la clé de cache est le chemin de la ressource, pas un hash de
son contenu. Si une image du seed est remplacée par un fichier différent sans
changer son nom, le cache servirait l'ancienne version tant qu'il n'est pas vidé
manuellement (`rm -rf .cache-webp` dans le volume). Accepté : ces visuels sont des
ressources de code, changées uniquement via un redéploiement, pas du contenu géré
au quotidien par l'utilisateur.

### Binaire externe `cwebp`, pas une bibliothèque Java

`CwebpConversionAdapter` (implémente le nouveau port `ConversionImageWebPPort`)
shell out vers le binaire `cwebp` (paquet Debian `webp`, installé dans l'image
Docker de prod) plutôt que d'utiliser une bibliothèque Java avec bindings natifs —
même logique que le CLI Tailwind standalone (cf.
[ADR-0006](0006-tailwind-cli-standalone.md)) : un binaire de référence, éprouvé,
sans risque de compatibilité JNI/JNA entre plateformes.

**Conséquence assumée** : `cwebp` n'est pas installé sur un poste de développement
Windows (l'application tourne hors Docker en dev, cf. CLAUDE.md). Dans ce cas, la
conversion échoue systématiquement et se rabat sur le format d'origine — le
développement fonctionne normalement, juste sans l'optimisation WebP en local. Les
tests qui exigent réellement `cwebp` (`CwebpConversionAdapterTest`) se désactivent
proprement via `Assumptions` plutôt que d'échouer, même logique que
`JpaUtilisateurRepositoryTest` (cf. CLAUDE.md « Pièges connus »).

## Alternatives envisagées

- **Bibliothèque Java pure (ex. `webp-imageio` à bindings JNA)** : écartée — risque
  de compatibilité native non vérifiable facilement, alors qu'un `apt-get install`
  d'un paquet Debian standard est fiable à 100 % dans l'image de prod.
- **Convertir uniquement à l'upload, sans mécanisme à la volée pour le seed** :
  écarté — ne répond pas à la demande explicite de l'utilisateur, et les visuels du
  seed (photos d'univers, couvertures des 7 livres) sont justement les images les
  plus vues du site (pages d'accueil, listes).
- **Content negotiation par extension d'URL** (générer des URLs `.webp` distinctes
  dans les templates) : écarté — aurait exigé de changer les valeurs `photo_url`
  déjà en base et toute la génération de balises `<img>` ; la négociation par
  en-tête `Accept` est transparente, aucune URL ne change.
- **Redimensionnement/compression au-delà du changement de format** : hors
  périmètre de cette passe — pas demandé, ajouté seulement si un besoin réel
  apparaît (proportionnalité, cf. CLAUDE.md).

## Conséquences

- Nouveau port `shared.domain.port.ConversionImageWebPPort` +
  `shared.domain.exception.ConversionImageEchoueeException`.
- Nouvel adaptateur `shared.infrastructure.image.CwebpConversionAdapter`.
- Nouveau contrôleur `shared.web.ImageStatiqueController` (remplace la résolution
  de ressources statiques par défaut pour `/images/**` uniquement).
- `Dockerfile` (image finale) : installe le paquet `webp`.
- `StockageFichierLocal` : dépendance supplémentaire sur `ConversionImageWebPPort`.
- Le cache WebP grossit `images-data` au fil des requêtes (borné par le nombre de
  visuels du seed, une dizaine de fichiers — négligeable).
