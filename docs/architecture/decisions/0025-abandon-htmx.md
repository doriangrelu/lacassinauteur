# ADR-0025 — Abandon de htmx (jamais utilisé en pratique)

**Statut** : Acté — modifie la partie « htmx » d'[ADR-0001](0001-rendu-hybride-thymeleaf-htmx.md)
**Date** : 2026-08-18

## Contexte

Trouvé en corrigeant [C-3 du dossier de sécurité](../../../) (ajout d'une
Content-Security-Policy, cf. `SecurityConfig`) : `htmx.min.js` est chargé sur
**toutes** les pages du back-office, mais aucun attribut `hx-*` n'existe nulle
part dans les templates — l'interactivité effectivement livrée (modales,
réordonnancement par glisser-déposer) a été implémentée en JS vanilla
(`modales.js`, `reordonner.js`), sans jamais recourir à htmx.

Or htmx injecte au chargement une feuille de style interne (classes
`.htmx-indicator`) via un `<style>` sans nonce — bloqué par une CSP stricte
(`style-src 'self'`), ce qui aurait exigé soit d'affaiblir la CSP
(`'unsafe-inline'` sur `style-src`, contraire à l'objectif même du
correctif), soit de la contourner par nonce pour une dépendance qui ne sert à
rien.

ADR-0001 avait explicitement anticipé ce scénario : « Thymeleaf pur sans htmx :
reste une option de repli simple si htmx s'avère superflu à l'usage ; htmx
n'est pas structurant au point d'être coûteux à retirer. »

## Décision

Retrait complet : `htmx.min.js` supprimé de `static/vendor/`, son
`<script>` retiré de `backoffice/layout/layout-backoffice.html`. Le rendu
reste « Thymeleaf pur » comme envisagé par ADR-0001 ; `modales.js` et
`reordonner.js` suffisent à l'interactivité actuelle du back-office
(ouverture/fermeture de modales, glisser-déposer pour réordonner).

## Conséquences

- `docs/architecture/tech-stack.md` et `docs/architecture/package-structure.md`
  mis à jour (plus de mention de htmx comme dépendance vendorisée).
- Si un besoin réel d'interactivité serveur-partielle apparaît plus tard
  (rechargement d'un fragment sans page complète), réévaluer alors — soit en
  réintroduisant htmx avec un nonce CSP correctement câblé, soit en JS
  vanilla comme les deux cas actuels, selon la complexité réelle du besoin.
