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

echo "==> Nettoyage des images Docker intermediaires devenues obsoletes..."
docker image prune -f

echo "==> Deploiement termine. Verifications recommandees :"
echo "    - Sante applicative   : curl -fsS ${DOMAINE}/actuator/health"
echo "    - Chargement du site  : ouvrir ${DOMAINE} dans un navigateur"
echo "    - Logs en cas de doute : docker compose -f $COMPOSE_FICHIER logs -f app"
