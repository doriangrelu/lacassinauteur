#!/usr/bin/env bash
# Deploie la derniere version du site sur le VPS de production.
#
# Depuis ADR-0030, ce script ne pilote QUE l'application : PostgreSQL et Caddy
# appartiennent au socle partage (~/infra), qui a son propre cycle de vie. Un
# deploiement du site ne redemarre donc plus la base ni le reverse proxy, et ne
# touche plus du tout a Keycloak.
#
# La sauvegarde est desormais assuree par ~/infra/backup.sh (planifiee en cron),
# puisque la base vit dans le socle. Ce script en declenche une avant d'agir, si
# le socle est present.
#
# Les migrations Flyway s'executent automatiquement au demarrage de l'application
# (spring.flyway.enabled: true) : redemarrer "app" suffit.
#
# Usage : scripts/deploy.sh

set -euo pipefail

COMPOSE_FICHIER="docker-compose.prod.yml"
DOMAINE="${APP_URL_BASE:-https://thierrylacassin-auteur.fr}"
SOCLE="${SOCLE_CHEMIN:-$HOME/infra}"
FRAGMENT_CADDY="caddy/mybook.caddy"

if [ -x "$SOCLE/backup.sh" ]; then
    echo "==> Sauvegarde de securite avant deploiement (socle, cf. ADR-0012)..."
    "$SOCLE/backup.sh"
else
    echo "/!\\ ATTENTION : $SOCLE/backup.sh introuvable, deploiement SANS sauvegarde prealable."
    echo "    Verifier l'installation du socle (cf. ADR-0030) avant de continuer."
fi

echo "==> Recuperation de la derniere version du code..."
git pull

echo "==> Reconstruction de l'image applicative et redemarrage de l'application..."
docker compose -f "$COMPOSE_FICHIER" up -d --build

echo "==> Publication du fragment Caddy du site..."
# Copie dans le dossier conf.d du socle, monte en tant que REPERTOIRE dans le
# conteneur Caddy : contrairement au montage d'un fichier unique, le conteneur
# relit le contenu a chaque rechargement, sans etre attache a un inode figé.
if [ -d "$SOCLE/conf.d" ]; then
    cp "$FRAGMENT_CADDY" "$SOCLE/conf.d/mybook.caddy"
    docker compose -f "$SOCLE/docker-compose.yml" exec -T caddy \
        caddy reload --config /etc/caddy/Caddyfile
else
    echo "/!\\ $SOCLE/conf.d introuvable : fragment Caddy NON publie."
fi

echo "==> Nettoyage des images Docker intermediaires devenues obsoletes..."
docker image prune -f

echo "==> Deploiement termine. Verifications recommandees :"
echo "    - Sante applicative   : curl -fsS ${DOMAINE}/actuator/health"
echo "    - Chargement du site  : ouvrir ${DOMAINE} dans un navigateur"
echo "    - Logs en cas de doute : docker compose -f $COMPOSE_FICHIER logs -f app"
