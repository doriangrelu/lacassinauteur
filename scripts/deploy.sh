#!/usr/bin/env bash
# Deploie la derniere version du site sur le VPS de production : sauvegarde de
# securite, recuperation du code, reconstruction de l'image applicative et
# redemarrage de la stack Docker Compose prod (cf. docker-compose.prod.yml,
# ADR-0016).
#
# Les migrations Flyway s'executent automatiquement au demarrage de l'application
# (spring.flyway.enabled: true, aucun bean personnalise ne modifie ce comportement
# — verifie dans src/main/resources/application.yml et le code applicatif) :
# redemarrer le service "app" suffit, pas de commande "flyway migrate" separee.
#
# Deploiement mono-serveur, mono-operateur (pas de fleet, pas de rolling update,
# pas de registre d'images — l'image est reconstruite directement sur le VPS a
# partir des sources, cf. ADR-0016) : ce script reste volontairement simple, execute
# manuellement en SSH sur le VPS, depuis la racine du depot.
#
# Usage : scripts/deploy.sh

set -euo pipefail

COMPOSE_FICHIER="docker-compose.prod.yml"
DOMAINE="${APP_URL_BASE:-https://thierrylacassin-auteur.fr}"

echo "==> Sauvegarde de securite avant deploiement (cf. ADR-0012)..."
scripts/backup.sh

echo "==> Recuperation de la derniere version du code..."
git pull

echo "==> Reconstruction de l'image applicative et redemarrage de la stack..."
docker compose -f "$COMPOSE_FICHIER" up -d --build

# Le Caddyfile est monte en bind mount ("./Caddyfile:/etc/caddy/Caddyfile") : le
# conteneur suit l'INODE monte au demarrage, pas le chemin. Or "git pull" ne
# modifie pas le fichier en place, il le REMPLACE par un nouvel inode — le
# conteneur continue donc de servir l'ancienne version indefiniment. Et "up -d"
# ne le recree pas de lui-meme, puisque la definition du service n'a pas change.
# Un "caddy reload" ne suffit pas non plus : il recharge fidelement... l'ancien
# fichier (symptome observe : "config is unchanged" dans les logs alors que le
# fichier a bien change sur l'hote).
# Recreer le conteneur est le seul geste fiable, et il est quasi gratuit (Caddy
# demarre en moins d'une seconde) : on le fait donc systematiquement plutot que
# de dependre de la vigilance de l'operateur.
echo "==> Recreation du conteneur Caddy (prise en compte du Caddyfile)..."
docker compose -f "$COMPOSE_FICHIER" up -d --force-recreate caddy

echo "==> Nettoyage des images Docker intermediaires devenues obsoletes..."
docker image prune -f

echo "==> Deploiement termine. Verifications recommandees :"
echo "    - Sante applicative   : curl -fsS ${DOMAINE}/actuator/health"
echo "    - Chargement du site  : ouvrir ${DOMAINE} dans un navigateur"
echo "    - Logs en cas de doute : docker compose -f $COMPOSE_FICHIER logs -f app"
