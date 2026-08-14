# ADR-0012 — Sauvegarde et restauration : `pg_dump`/`pg_restore` + volume d'images, via scripts ops

**Statut** : Acté
**Date** : 2026-08-12

## Contexte

Le site tourne sur un unique VPS OVHcloud via Docker Compose (cf.
[ADR-0010](0010-upload-images-stockage-local.md)) : la base PostgreSQL et les images
téléversées (volume Docker nommé `images-data`, monté sur `/data/images` dans le
service `app`) sont les deux seules sources de vérité à protéger avant tout
déploiement ou changement risqué.

## Décision

- Deux scripts shell autonomes, `scripts/backup.sh` et `scripts/restore.sh`, pilotant
  `docker compose` — aucun outil PostgreSQL requis sur la machine hôte.
- **Sauvegarde** (`backup.sh [dossier-destination]`) :
  - `pg_dump -F custom` via `docker compose exec -T db`.
  - Images : le volume `images-data` n'est monté que dans le conteneur `app`, donc
    inaccessible directement depuis l'hôte. On y accède via un conteneur **éphémère**
    (`docker compose run --rm --no-deps --entrypoint sh app -c "tar -cf - -C
    /data/images ."`), qui fonctionne que le service `app` soit démarré ou arrêté
    (contrairement à `docker compose exec`, qui exige un conteneur déjà en cours
    d'exécution) et sans dépendre du nom interne (préfixé par le projet Compose) du
    volume.
  - Les deux artefacts (`base.dump`, `images.tar`) sont regroupés dans une archive
    unique horodatée `backups/sauvegarde-<horodatage>.tar.gz`.
- **Restauration** (`restore.sh <archive>`) : opération **destructive**, gardée par
  une confirmation explicite (`Tapez RESTAURER en majuscules`). Arrête `app` (évite
  les écritures concurrentes pendant la restauration), restaure la base
  (`pg_restore --clean --if-exists`), vide puis reremplit le volume d'images
  (`find /data/images -mindepth 1 -delete`, **pas** `rm -rf /data/images` — ce chemin
  est le point de montage du volume lui-même, sa suppression échoue avec *Device or
  resource busy*, seul son contenu est supprimable), puis redémarre `app`.
- Restauration **volontairement absente du back-office** — aucune UI self-service
  pour une opération destructive de cette ampleur ; c'est une procédure manuelle,
  exécutée par un opérateur en ligne de commande, avec l'archive à restaurer choisie
  explicitement.
- `.gitattributes` force `*.sh text eol=lf` : ces scripts doivent garder des fins de
  ligne LF quel que soit le `core.autocrlf` de la machine Windows qui les édite,
  sans quoi un shebang terminé par `\r` casse l'exécution sur le VPS Linux cible.
  Le bit exécutable est fixé dans l'index Git (`git update-index --chmod=+x`), NTFS
  ne portant pas cette information.
- Testé en conditions réelles (fichier témoin déposé dans le volume, sauvegarde,
  suppression, restauration, contenu vérifié identique) avant d'être considéré acté.

## Alternatives envisagées

- **Bouton de restauration dans le back-office** : écarté — restaurer une sauvegarde
  écrase silencieusement tout travail effectué depuis, un risque disproportionné
  pour un geste accessible en un clic à un compte `ADMIN`/`AUTEUR`.
- **Monter le volume d'images en bind mount sur un chemin hôte connu** (au lieu d'un
  volume nommé Docker) pour simplifier l'accès aux fichiers : écarté — le volume
  nommé est la pratique standard Docker Compose, portable entre environnements ; le
  détour par un conteneur éphémère résout l'accès sans sacrifier cet avantage.
- **Stocker les sauvegardes hors du VPS** (objet storage, autre serveur) : hors
  périmètre de cet ADR — les scripts produisent l'archive localement ;
  son transfert/rétention (cron + rsync vers un stockage distant, par exemple) reste
  à planifier en phase de déploiement (cf. [roadmap.md](../../roadmap.md), Phase 7).

## Conséquences

- Avant tout déploiement ou migration Flyway risquée sur la production, exécuter
  `scripts/backup.sh` devient le réflexe attendu.
- La planification récurrente (cron sur le VPS) et la rétention/purge des anciennes
  archives restent à définir lors du provisionnement effectif (Phase 7 de la
  roadmap) — cet ADR couvre le mécanisme, pas encore son automatisation en
  production.
