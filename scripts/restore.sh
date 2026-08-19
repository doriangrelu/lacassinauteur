#!/usr/bin/env bash
# Restaure la base de donnees PostgreSQL et les images depuis une archive produite
# par scripts/backup.sh. OPERATION DESTRUCTIVE : remplace entierement la base et le
# volume d'images courants. Demande une confirmation explicite avant d'agir.
#
# Usage : scripts/restore.sh <chemin-vers-archive.tar.gz>

set -euo pipefail

if [ $# -ne 1 ]; then
    echo "Usage : scripts/restore.sh <chemin-vers-archive.tar.gz>" >&2
    exit 1
fi

ARCHIVE="$1"
if [ ! -f "$ARCHIVE" ]; then
    echo "Erreur : archive introuvable : $ARCHIVE" >&2
    exit 1
fi

CHEMIN_IMAGES_CONTENEUR="/data/images"
SERVICE_DB="db"
SERVICE_APP="app"
BASE_DE_DONNEES="${POSTGRES_DB:-site}"
UTILISATEUR_DB="${POSTGRES_USER:-site}"
# Base Keycloak (cf. ADR-0027) : optionnelle, meme logique que backup.sh.
BASE_KEYCLOAK="${KEYCLOAK_DB:-}"
UTILISATEUR_KEYCLOAK="${KEYCLOAK_DB_USER:-}"

echo "/!\\ Cette operation va ECRASER la base de donnees ($BASE_DE_DONNEES) et le"
echo "    volume d'images du service \"$SERVICE_APP\" avec le contenu de :"
echo "    $ARCHIVE"
read -r -p "Tapez RESTAURER en majuscules pour confirmer : " CONFIRMATION
if [ "$CONFIRMATION" != "RESTAURER" ]; then
    echo "Annule."
    exit 1
fi

DOSSIER_TEMPORAIRE="$(mktemp -d)"
trap 'rm -rf "$DOSSIER_TEMPORAIRE"' EXIT

echo "==> Extraction de l'archive..."
tar -xzf "$ARCHIVE" -C "$DOSSIER_TEMPORAIRE"

if [ ! -f "$DOSSIER_TEMPORAIRE/base.dump" ] || [ ! -f "$DOSSIER_TEMPORAIRE/images.tar" ]; then
    echo "Erreur : archive invalide, base.dump ou images.tar absent." >&2
    exit 1
fi

echo "==> Arret de l'application (pour eviter les ecritures concurrentes)..."
docker compose stop "$SERVICE_APP" 2>/dev/null || true
if [ -n "$BASE_KEYCLOAK" ]; then
    docker compose stop keycloak 2>/dev/null || true
fi

echo "==> Restauration de la base de donnees ($BASE_DE_DONNEES)..."
docker compose exec -T "$SERVICE_DB" pg_restore -U "$UTILISATEUR_DB" -d "$BASE_DE_DONNEES" --clean --if-exists < "$DOSSIER_TEMPORAIRE/base.dump"

if [ -n "$BASE_KEYCLOAK" ] && [ -f "$DOSSIER_TEMPORAIRE/keycloak.dump" ]; then
    echo "==> Restauration de la base Keycloak ($BASE_KEYCLOAK)..."
    docker compose exec -T "$SERVICE_DB" pg_restore -U "$UTILISATEUR_KEYCLOAK" -d "$BASE_KEYCLOAK" --clean --if-exists < "$DOSSIER_TEMPORAIRE/keycloak.dump"
fi

echo "==> Remplacement des images (volume Docker du service \"$SERVICE_APP\")..."
# "$CHEMIN_IMAGES_CONTENEUR" est le point de montage du volume : on vide son
# contenu (find -delete) plutot que de le supprimer lui-meme (rm -rf echouerait
# avec "Device or resource busy").
docker compose run --rm --no-deps -T --entrypoint sh "$SERVICE_APP" \
    -c "find '$CHEMIN_IMAGES_CONTENEUR' -mindepth 1 -delete && tar -xf - -C '$CHEMIN_IMAGES_CONTENEUR'" \
    < "$DOSSIER_TEMPORAIRE/images.tar"

echo "==> Redemarrage de l'application..."
docker compose start "$SERVICE_APP"
if [ -n "$BASE_KEYCLOAK" ]; then
    docker compose start keycloak 2>/dev/null || true
fi

echo "==> Restauration terminee."
