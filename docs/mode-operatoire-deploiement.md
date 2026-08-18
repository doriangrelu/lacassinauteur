# Mode opératoire — déployer soi-même

> Ce guide s'adresse à toi (l'utilisateur), pour apprendre à faire les gestes de
> base toi-même sans attendre que je m'en occupe. Contrairement à
> [`docs/deploiement.md`](deploiement.md) (qui décrit la toute première mise en
> ligne, faite une seule fois), ce document couvre les opérations du
> quotidien : déployer une nouvelle version, vérifier que tout va bien,
> consulter les logs, revenir en arrière en cas de souci.

## 1. Rappel de ce qui tourne sur le serveur

Un seul VPS (OVHcloud), trois conteneurs Docker gérés par **Docker Compose** :

| Conteneur | Rôle |
|---|---|
| `app` | Le site (Spring Boot), reconstruit à chaque déploiement |
| `db` | La base PostgreSQL (les données) |
| `caddy` | Le reverse proxy qui gère le HTTPS automatique |

Tout se pilote depuis le dossier `~/mybook` **sur le serveur** (pas sur ta
machine), via `docker compose` (sans tiret entre "docker" et "compose").

## 2. Se connecter au serveur

Depuis un terminal (PowerShell sur Windows) :

```bash
ssh ubuntu@152.228.237.190
```

Le mot de passe est celui que tu as utilisé pour ajouter ma clé SSH la
première fois. Une fois connecté, place-toi dans le dossier du projet :

```bash
cd ~/mybook
```

Toutes les commandes ci-dessous supposent que tu es dans ce dossier.

**Conseil pour plus tard** : passer toi aussi à une connexion par clé SSH
(comme moi) plutôt que par mot de passe est plus sûr. Dis-le-moi si tu veux
qu'on mette ça en place — je peux générer une clé pour toi et t'expliquer
comment l'utiliser depuis ton PC.

## 3. Vérifier l'état actuel (avant de toucher à quoi que ce soit)

```bash
docker compose ps
```

Tu dois voir les trois conteneurs (`app`, `db`, `caddy`) avec le statut `Up`
(et `healthy` pour `db`). Si l'un d'eux manque ou est `Exited`, quelque chose
ne va pas — regarde les logs (§6) avant d'aller plus loin.

## 4. Déployer une nouvelle version

C'est l'opération la plus courante : récupérer le code le plus récent
(poussé sur GitHub) et le mettre en ligne.

```bash
scripts/deploy.sh
```

Ce script fait tout, dans l'ordre, et affiche ce qu'il fait :
1. Sauvegarde de sécurité (base + images) — `scripts/backup.sh` en interne.
2. `git pull` — récupère le dernier code.
3. Reconstruit l'image et redémarre l'application (`docker compose up -d
   --build`).
4. Nettoie les anciennes images Docker devenues inutiles.

Ça prend en général 30 à 60 secondes. Le script affiche des indications de
vérification à la fin — passe directement à l'étape suivante.

## 5. Vérifier que le déploiement s'est bien passé

Deux vérifications rapides, l'une depuis le serveur, l'autre depuis ton
navigateur :

```bash
curl -fsS https://thierrylacassin-auteur.fr/actuator/health
```

Doit répondre `{"status":"UP",...}`. Si ça répond autre chose (ou rien du
tout après quelques secondes), regarde les logs (§6).

Ensuite, ouvre **https://thierrylacassin-auteur.fr** dans un navigateur et
clique sur deux ou trois pages pour t'assurer visuellement que tout s'affiche
normalement.

## 6. Consulter les logs en cas de doute

Logs de l'application (le plus utile en cas de problème) :

```bash
docker compose logs app --tail 100
```

Suivre les logs en direct (utile pendant que tu testes le site) :

```bash
docker compose logs app -f
```
*(`Ctrl+C` pour arrêter de suivre — ça n'arrête pas l'application.)*

Logs d'un autre conteneur (remplace `app` par `db` ou `caddy`) :

```bash
docker compose logs caddy --tail 50
```

**Logs persistants sur disque** : en plus de la sortie console ci-dessus (qui
se perd si le conteneur est recréé), l'application écrit aussi ses logs dans
un fichier, conservé 7 jours et gardé au redémarrage du conteneur (volume
Docker nommé `logs-data`) :

```bash
docker compose exec app tail -f /data/logs/application.log
```

Utile si tu veux consulter un incident survenu il y a quelques jours, ou
récupérer le fichier complet plutôt qu'un extrait `--tail`.

## 7. Modifier un réglage (fichier `.env`)

Les mots de passe, clés API, etc. sont dans le fichier `.env` (jamais dans le
code). Pour changer une valeur :

```bash
nano .env
```
*(`Ctrl+O` puis Entrée pour sauvegarder, `Ctrl+X` pour quitter.)*

Une fois modifié, il faut redémarrer l'application pour que le changement
soit pris en compte (pas besoin de reconstruire l'image, juste redémarrer le
conteneur) :

```bash
docker compose up -d app
```

## 8. Sauvegarder / restaurer manuellement

Une sauvegarde automatique tourne déjà chaque nuit à 3h (cf. cron), mais tu
peux en déclencher une à tout moment :

```bash
scripts/backup.sh
```

L'archive est créée dans `backups/` (nom horodaté). Pour restaurer une
sauvegarde (⚠️ **écrase les données actuelles**, demande confirmation avant
d'agir) :

```bash
scripts/restore.sh backups/sauvegarde-20260101-030000.tar.gz
```

## 9. Revenir en arrière si un déploiement pose problème

Si après un `scripts/deploy.sh` le site ne fonctionne plus correctement :

**Option A — remettre le code précédent** (le plus courant, si le bug vient
d'un changement de code) :

```bash
git log --oneline -5          # repérer le commit d'avant le déploiement problématique
git checkout <hash-du-commit-precedent>
docker compose up -d --build
```

Une fois revenu à une version stable, préviens-moi (ou attends que je m'en
occupe) plutôt que de continuer à bidouiller — je regarderai ce qui a cassé.

**Option B — restaurer une sauvegarde** (si le problème vient des données,
pas du code) : voir §8 ci-dessus.

## 10. Lexique rapide

| Commande | Ce que ça fait |
|---|---|
| `docker compose ps` | Liste les conteneurs et leur état |
| `docker compose logs <service> --tail 100` | Affiche les 100 dernières lignes de logs |
| `docker compose logs <service> -f` | Suit les logs en direct |
| `docker compose up -d` | (Re)démarre les conteneurs (sans reconstruire) |
| `docker compose up -d --build` | Reconstruit l'image puis redémarre |
| `docker compose restart <service>` | Redémarre un seul conteneur sans le reconstruire |
| `git pull` | Récupère le dernier code depuis GitHub |
| `git log --oneline -10` | Liste les 10 derniers commits |
| `exit` | Se déconnecter du serveur |

## En cas de doute

Si une commande renvoie une erreur que tu ne comprends pas, ou si le site est
indisponible après une manipulation : ne force rien, copie le message
d'erreur et transmets-le-moi (ou attends que je vérifie) — mieux vaut un site
en pause quelques minutes qu'une donnée perdue par précipitation.
