# ADR-0022 — Limites de ressources par conteneur via `deploy.resources.limits`, pas de Docker Swarm

**Statut** : Acté
**Date** : 2026-08-18

## Contexte

L'utilisateur veut éviter qu'un service ne puisse épuiser les ressources du
VPS (2 vCore / 4 Go RAM — gamme VPS-1 2027, cf.
[tech-stack.md](../tech-stack.md#déploiement--ovhcloud)) et demandait
initialement d'évaluer une migration vers Docker Swarm pour « supporter un
minimum de charge ».

## Décision

**Pas de Swarm.** Swarm répartit la charge entre **plusieurs machines** — sur
un unique VPS, il n'y a rien à répartir : aucun gain de capacité réel,
seulement de la complexité opérationnelle supplémentaire (init du cluster,
réseaux overlay, `docker stack deploy` au lieu de `docker compose up`) pour
zéro bénéfice mesurable. Ça contredirait aussi le choix mono-nœud déjà acté
dans [ADR-0016](0016-deploiement-caddy-prod.md).

Le besoin réel exprimé — plafonner vCPU/RAM par conteneur — est directement
couvert par `deploy.resources.limits` dans `docker-compose.prod.yml`.
Contrairement à une idée reçue, cette clé **n'exige pas** le mode Swarm :
Docker Compose v2 (confirmé en version 2.40.3 sur le VPS) l'applique aussi en
mode Compose classique, en la traduisant en limites cgroup directement sur le
conteneur (`docker inspect` montre bien `NanoCpus`/`Memory` renseignés après
un `up -d`).

Plafonds retenus (limites hautes, pas des réservations garanties — un service
inactif ne consomme rien) :

| Service | CPU | RAM |
|---|---|---|
| `app` | 1.5 vCore | 1536 Mo |
| `db` | 1.0 vCore | 768 Mo |
| `caddy` | 0.5 vCore | 128 Mo |

La somme CPU (3.0) dépasse volontairement les 2 vCore physiques : ce sont des
plafonds individuels, pas une réservation — les trois services ne pointent
jamais tous simultanément, et ça laisse de la marge de rafale à `app` (le plus
gourmand, JVM + Thymeleaf) sans pénaliser les deux autres au repos. La somme
RAM (2432 Mo) reste elle nettement sous les 4 Go disponibles, pour garder une
marge confortable au système et à Docker lui-même — un OOM-kill sur `db` ou
`app` est plus coûteux à diagnostiquer qu'un plafond CPU trop bas.

## Alternatives envisagées

- **Docker Swarm réel** (`docker swarm init` en mono-nœud) : techniquement
  possible même à un seul nœud, mais n'apporte alors que la syntaxe
  `deploy.resources.limits` — déjà utilisable sans lui — au prix d'un mode
  opératoire different (`docker stack deploy`, incompatible avec
  `scripts/deploy.sh`/`backup.sh`/`restore.sh` actuels qui utilisent
  `docker compose`). Écarté : coût de migration réel pour un bénéfice nul à ce
  périmètre.
- **`deploy.resources.reservations`** (réservation garantie plutôt que
  plafond) : ignorée par Compose hors Swarm (uniquement honorée par
  l'ordonnanceur Swarm) — n'aurait aucun effet ici, seul `limits` s'applique
  en mode Compose classique.
- **Ne rien plafonner** (statu quo) : écarté, c'est le problème que
  l'utilisateur voulait résoudre — un service qui fuit en mémoire (ex. un bug
  applicatif) pourrait sinon affamer les deux autres et rendre le VPS
  inutilisable au lieu de simplement faire échouer ses propres requêtes.

## Conséquences

- `docker-compose.prod.yml` : chaque service (`db`, `app`, `caddy`) gagne un
  bloc `deploy.resources.limits`.
- Si l'usage réel (à observer via `docker stats` une fois en charge) montre
  qu'un plafond est trop bas — l'application se met à ralentir ou swap sous
  charge normale — ajuster les valeurs du tableau ci-dessus est un simple
  changement de fichier, sans redémarrage d'infrastructure.
