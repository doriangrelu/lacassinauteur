# ADR-0001 — Rendu hybride Thymeleaf + htmx

**Statut** : Acté
**Date** : 2026-08-12

## Contexte

Le site doit rester un « petit monolithe » Spring Boot (choix explicite du
demandeur, qui maîtrise Java/Spring et ne souhaite pas maintenir un frontend séparé).
Le site public a des besoins de SEO forts (livres, blog/actualités) et le back-office
a besoin d'un minimum d'interactivité (réordonner, publier/dépublier, modérer un avis)
sans la lourdeur d'une SPA.

## Décision

Rendu serveur avec **Thymeleaf** pour toutes les pages, complété par **htmx** pour les
interactions ciblées ne nécessitant pas un rechargement complet de page.

## Alternatives envisagées

- **SPA séparée (React/Vue) + API REST** : écartée — double projet à maintenir, CORS,
  build JS, complexité de déploiement disproportionnée pour le périmètre et le
  contexte (un seul développeur, stack Java).
- **Thymeleaf pur sans htmx** : reste une option de repli simple si htmx s'avère
  superflu à l'usage ; htmx n'est pas structurant au point d'être coûteux à retirer.

## Conséquences

- Pas de build JS/npm dans le pipeline.
- Bon SEO natif (rendu serveur complet).
- Le back-office reste dans le même projet, testable avec `@WebMvcTest`.
