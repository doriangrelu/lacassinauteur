# ADR-0023 — Bind mounts vers `~/volumes/` plutôt que des volumes Docker nommés

**Statut** : Acté
**Date** : 2026-08-18

## Contexte

Depuis [ADR-0016](0016-deploiement-caddy-prod.md), les données persistantes
(base PostgreSQL, images uploadées, logs applicatifs — cf. la journalisation
Logback introduite le 2026-08-18) et les certificats Let's Encrypt vivaient
dans des **volumes Docker nommés**
(`db-data`, `images-data`, `logs-data`, `caddy-data`, `caddy-config`) — le
comportement par défaut du driver `local`. Ces volumes sont physiquement
stockés sous `/var/lib/docker/volumes/mybook_<nom>/_data`, un chemin interne à
Docker, invisible par une simple exploration du système de fichiers
(`ls ~/mybook`) et nécessitant `docker volume inspect` pour le localiser.

L'utilisateur, qui apprend à opérer le serveur lui-même (cf.
[mode-operatoire-deploiement.md](../../mode-operatoire-deploiement.md)),
voulait pouvoir retrouver ces données directement sur le disque, au même
niveau que le dépôt cloné (`~/mybook`).

## Décision

Chaque service référence désormais un chemin hôte explicite via un **bind
mount**, plutôt qu'un volume nommé :

```yaml
volumes:
  - ../volumes/db-data:/var/lib/postgresql/data
```

Le chemin relatif `../volumes/<nom>` (résolu par Compose relativement à
l'emplacement de `docker-compose.prod.yml`, donc `~/mybook`) pointe vers
`~/volumes/<nom>` — un dossier sibling du dépôt, directement visible avec un
simple `ls ~/volumes`. Plus de section `volumes:` top-level dans le fichier
Compose : il n'y a plus rien à déclarer, un bind mount n'est pas une ressource
nommée gérée par Docker.

## Migration réalisée

Effectuée manuellement sur le VPS avant ce commit (pas automatisée dans un
script, opération ponctuelle unique) :

1. Sauvegarde de sécurité (`scripts/backup.sh`).
2. `docker compose down` (sans `-v` : les volumes nommés existants ne sont
   **pas** supprimés, ils servent de filet de sécurité).
3. Copie du contenu de chacun des cinq volumes vers `~/volumes/<nom>` via
   `sudo cp -a`, qui préserve exactement propriétaire/permissions
   d'origine — point critique pour PostgreSQL, qui refuse de démarrer si son
   répertoire de données n'appartient pas au bon UID.
4. `docker compose up -d` avec le fichier Compose mis à jour : les conteneurs
   redémarrent en pointant vers les bind mounts, sans avoir besoin de
   redemander de certificat Let's Encrypt (`caddy-data`/`caddy-config` copiés
   à l'identique).
5. Vérification : santé applicative, contenu de la base identique (mêmes
   compteurs univers/collections/livres/comptes/abonnés), tunnel SSH vers
   Postgres toujours fonctionnel.

Les anciens volumes nommés (`mybook_db-data`, etc.) restent présents mais
inutilisés sur le VPS — à supprimer explicitement (`docker volume rm`) une
fois la nouvelle configuration validée dans la durée, pas fait dans cette
passe pour garder un filet de sécurité immédiat.

## Alternatives envisagées

- **Garder les volumes nommés, juste documenter leur emplacement** : écarté —
  ne répond pas au besoin exprimé (visibilité directe sans commande Docker
  dédiée), et l'utilisateur apprend justement à opérer le serveur par
  lui-même.
- **Volume nommé avec `driver_opts` pointant vers un chemin hôte personnalisé**
  (`driver: local`, `driver_opts: {type: none, device: ..., o: bind}`) :
  techniquement équivalent à un bind mount direct mais garde une couche
  d'indirection (`docker volume ls` montrerait toujours un volume nommé) sans
  bénéfice sur ce périmètre mono-serveur — le bind mount direct est plus
  simple à lire dans le fichier Compose.

## Conséquences

- `docker-compose.prod.yml` : chaque service pointe vers `../volumes/<nom>`,
  plus de section `volumes:` top-level.
- Les données sont désormais à `~/volumes/` sur le VPS, au même niveau que
  `~/mybook` — visibles avec `ls`, sauvegardables/inspectables directement en
  tant que fichiers, sans détour par les commandes `docker volume`.
- Attention pour l'opérateur : ces dossiers appartiennent en partie à `root`
  (ou à l'UID interne des images officielles Postgres/Caddy) — normal, ne pas
  faire de `chown` dessus sous peine d'empêcher PostgreSQL de démarrer (il
  vérifie strictement le propriétaire de son répertoire de données).
