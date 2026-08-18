# ADR-0021 — Accès à la base de production : tunnel SSH + client desktop, pas d'outil web hébergé

**Statut** : Acté
**Date** : 2026-08-18

## Contexte

L'utilisateur veut pouvoir consulter/modifier directement les données en
production avec un outil type pgAdmin, sans que ça pèse sur les ressources
limitées du VPS (cf. [ADR-0022](0022-limites-ressources-conteneurs.md) — un
seul VPS-1 2027, 2 vCore / 4 Go RAM, partagé avec l'application elle-même et
Caddy).

## Décision

Pas d'outil d'administration web hébergé sur le VPS (pgAdmin4 ou Adminer en
conteneur). À la place : un **tunnel SSH** vers le port Postgres, utilisé avec
un client desktop du choix de l'utilisateur (DBeaver, pgAdmin en local, TablePlus...).

```bash
ssh -L 5433:localhost:5432 ubuntu@<ip-du-vps>
```

Puis connexion du client desktop sur `localhost:5433` (base/utilisateur/mot de
passe : mêmes valeurs que `POSTGRES_DB`/`POSTGRES_USER`/`POSTGRES_PASSWORD`
dans le `.env` du VPS). Le tunnel ne vit que le temps de la commande SSH —
rien n'est exposé en dehors de cette session.

Pour que le tunnel ait un port à cibler, `docker-compose.prod.yml` publie
désormais le port du service `db` — mais **uniquement en boucle locale**
(`127.0.0.1:5432:5432`, jamais `0.0.0.0:...`) : le port reste physiquement
inatteignable depuis le réseau public, seul un processus déjà présent sur le
VPS (ou arrivé via SSH, comme le tunnel) peut s'y connecter. Ça ne change donc
rien à la surface d'attaque décrite dans [ADR-0016](0016-deploiement-caddy-prod.md)
(« la base ne doit jamais être exposée directement sur le réseau public ») —
seul le mécanisme d'accès local change, pas l'exposition externe.

## Alternatives envisagées

- **pgAdmin4 en conteneur** : l'option la plus proche de ce que l'utilisateur
  connaît, mais nettement la plus lourde (souvent 200-300 Mo de RAM à elle
  seule, en continu) pour un usage mono-utilisateur occasionnel — disproportionné
  sur une machine à 4 Go de RAM déjà partagée. Écartée pour cette v1,
  réévaluable si le besoin d'un accès web sans SSH devient réel.
- **Adminer en conteneur** : nettement plus léger (une image PHP de quelques
  Mo, pas de process serveur permanent significatif), mais ajoute quand même
  un point d'entrée web supplémentaire à sécuriser (authentification,
  exposition via Caddy) pour un gain marginal face au tunnel SSH — l'opérateur
  a de toute façon déjà un accès SSH complet au VPS. Gardée en réserve si un
  jour un accès web devient nécessaire sans passer par SSH.
- **Publier le port sur toutes les interfaces (`"5432:5432"`)** : écarté sans
  discussion — exposerait Postgres directement à Internet, contraire au choix
  de sécurité déjà acté dans ADR-0016.

## Conséquences

- `docker-compose.prod.yml` : le service `db` gagne une section `ports` liée
  à `127.0.0.1` (absente jusqu'ici).
- [`docs/mode-operatoire-deploiement.md`](../../mode-operatoire-deploiement.md)
  documente la commande de tunnel et les identifiants à utiliser.
- Aucun nouveau conteneur, aucune consommation de ressources supplémentaire au
  repos — le tunnel n'existe que pendant une session de travail active.
