# ADR-0015 — Polices auto-hébergées : Cormorant Garamond + Inter (remplace Aptos)

**Statut** : Acté
**Date** : 2026-08-14

## Contexte

Le brief fonctionnel (§8) demandait Cormorant Garamond (titres) et Aptos (texte
courant), déclarées en tokens de thème Tailwind depuis la Phase 2 mais jamais
auto-hébergées : sans `@font-face`, elles ne s'affichent que si le visiteur les a
déjà installées. Cormorant Garamond est une police Google Fonts (SIL Open Font
License, librement redistribuable). **Aptos est une police propriétaire Microsoft**
(succédait à Calibri, livrée avec Windows 11/Microsoft 365) : embarquer les fichiers
réels sans vérifier la licence aurait été un risque juridique, et elle n'est de toute
façon disponible sur aucun catalogue de polices libres pour être auto-hébergée
légalement.

## Décision

- **Cormorant Garamond** : auto-hébergée telle quelle (licence compatible).
- **Aptos → remplacée par Inter** (SIL Open Font License), une police sans-serif
  libre visuellement proche (humaniste, moderne), pour le texte courant et les
  boutons.
- Fichiers récupérés une fois depuis l'API Google Fonts (CSS2, avec un User-Agent
  moderne pour obtenir du woff2) puis servis localement — aucun appel à un tiers au
  chargement d'une page, cf. tech-stack.md.
- **Sous-ensemble minimal** : seul le sous-ensemble « latin » (couvre les caractères
  accentués français) est conservé, et seuls les styles/graisses réellement
  utilisés dans les templates (vérifié par recherche dans `templates/public/**`) :
  - Cormorant Garamond normal (400/700, variable) + italique (400/700, variable) —
    2 fichiers.
  - Inter normal (400/700, variable) — 1 fichier. Pas d'italique Inter : jamais
    utilisé.
  - Les deux familles sont livrées comme des **fichiers de police variables**
    (Google sert le même fichier pour toutes les graisses testées) : une seule
    règle `@font-face` par style, avec `font-weight: 400 700` (plage), plutôt
    qu'une règle par graisse.
- Fichiers dans `src/main/resources/static/fonts/`, servis via le mapping statique
  Spring Boot par défaut (`/fonts/**`), pas de configuration supplémentaire
  nécessaire.

## Alternatives envisagées

- **Conserver Aptos en s'appuyant sur la police système** : écarté — la plupart des
  visiteurs (hors Windows 11/Office récents) ne l'ont pas installée, rendu
  incohérent selon l'appareil, contraire à l'objectif d'une charte graphique
  maîtrisée.
- **Charger les polices depuis Google Fonts en direct (`<link>` CDN)** : écarté dès
  la conception du projet (cf. tech-stack.md) — performance et confidentialité
  (pas d'appel tiers au chargement).
- **Répliquer Aptos avec une police plus proche visuellement (ex. Segoe UI-like)** :
  écarté — Inter est un choix éprouvé, largement utilisé, avec un rendu neutre et
  lisible cohérent avec la charte blanc/noir/gris du site.

## Conséquences

- `frontend/public.css` : token `--font-body` pointe désormais sur `"Inter"` (plus
  `"Aptos"`), trois règles `@font-face` ajoutées.
- Le back-office garde son propre empilement de polices système (ADR-0009,
  volontairement distinct de l'identité éditoriale du site public) — non concerné
  par cet ADR.
- Si Thierry a une préférence différente ou une licence Aptos dont l'équipe n'a pas
  connaissance, remplacer `inter.woff2`/le token `--font-body` reste un changement
  localisé, sans impact sur le reste du design system.
