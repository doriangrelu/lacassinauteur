# ADR-0007 — Material Web Components pour le back-office

**Statut** : Remplacé par [ADR-0009](0009-abandon-material-web.md) (2026-08-12) —
conservé pour l'historique de la décision et de son premier amendement.
**Date** : 2026-08-12

## Contexte

Le back-office doit avoir une identité visuelle Material Design, distincte du site
public (dont la charte est définie dans le
[brief fonctionnel §8](../../business/brief.md#8-identité-graphique-contraintes-pour-lintégration)
et n'a **aucune** vocation Material Design — cette librairie ne doit **jamais** être
chargée sur les pages du site public). Le projet reste un monolithe Thymeleaf + htmx,
sans build JS de type SPA (cf. [ADR-0001](0001-rendu-hybride-thymeleaf-htmx.md)).

## Décision

Utiliser **Material Web Components** (`@material/web`), la librairie officielle de
composants Material Design 3 de Google, distribuée en **web components** standard
(`<md-filled-button>`, `<md-text-field>`, `<md-dialog>`, etc.). Chargée via un tag
`<script type="module">` (import depuis un CDN ou un fichier vendorisé en local dans
`static/vendor/`), sans étape de build JS : les web components fonctionnent nativement
dans le HTML généré par Thymeleaf, s'intègrent avec htmx sans friction (ce sont des
éléments DOM standards).

Tailwind (cf. [ADR-0006](0006-tailwind-cli-standalone.md)) reste utilisé pour la mise
en page, l'espacement et le layout du back-office ; Material Web fournit l'apparence
des composants interactifs (boutons, champs, dialogues, snackbars). Les deux
coexistent sans conflit : Tailwind ne stylise pas l'intérieur du Shadow DOM des
composants Material.

## Alternatives envisagées

- **Materialize CSS** : écarté — projet historique peu maintenu, base sur des classes
  CSS plutôt que des composants encapsulés, plus de risque de collision avec Tailwind.
- **Material Tailwind** : écarté — orienté React en premier lieu, la variante HTML pure
  est moins mature/maintenue que la librairie officielle Google.
- **Construire des composants Material "maison" en CSS uniquement** : écarté — beaucoup
  de travail pour un rendu fidèle (élévation, ripple, accessibilité) que Material Web
  fournit déjà, correctement testé.

## Conséquences

- Un layout back-office dédié (cf.
  [architecture.md §12](../architecture.md#12-conventions-front-end)) charge Material
  Web + `backoffice.css` ; le layout public ne charge ni l'un ni l'autre.
- Les web components Material étant encapsulés en Shadow DOM, l'interaction avec les
  formulaires Thymeleaf (binding, validation) reste standard (attributs `name`/`value`
  usuels côté HTML, pas de JS de binding supplémentaire nécessaire pour la soumission).

## Amendement (2026-08-12) — champs de saisie en HTML natif

En implémentant l'écran de connexion (Phase 1), `md-outlined-text-field` s'est avéré
peu fiable à la saisie clavier/souris dans les tests (le composant est bien
form-associated — il apparaît dans `FormData` — mais ne capte pas correctement le
focus/la frappe selon le contexte). Pour un formulaire aussi critique que la
connexion, la fiabilité prime sur l'homogénéité visuelle stricte :

- **Champs de saisie de données** (email, mot de passe, texte, etc.) : `<input>`/
  `<select>` HTML natifs, stylés Tailwind via les classes utilitaires `.champ-label` /
  `.champ-texte` définies dans `frontend/backoffice.css`, pour rester visuellement
  proches de Material sans dépendre du composant.
- **Actions** (`md-filled-button`, `md-outlined-button`) : Material Web conservé, le
  composant bouton s'est montré fiable dans les mêmes tests.
- Si un composant Material non-bouton doit être réintroduit plus tard (dialogue,
  snackbar, select), le valider par un test d'interaction réel avant de généraliser.
