#!/usr/bin/env bash
# Sauvegarde la base de donnees PostgreSQL et les images uploadees dans une seule
# archive horodatee. Utilise docker compose : aucun outil Postgres n'est necessaire
# sur la machine hote.
#
# Les images vivent dans le volume Docker nomme "images-data", monte uniquement dans
# le conteneur du service "app" (cf. docs/architecture/decisions/0010-*.md) — on y
# accede via un conteneur ephemere ("docker compose run"), qui fonctionne que le
# service "app" soit demarre ou non.
#
# Usage : scripts/backup.sh [dossier-destination]
#   dossier-destination : par defaut "backups" (cree si absent)

set -euo pipefail

DOSSIER_DESTINATION="${1:-backups}"
HORODATAGE="$(date +%Y%m%d-%H%M%S)"
NOM_ARCHIVE="sauvegarde-${HORODATAGE}.tar.gz"

CHEMIN_IMAGES_CONTENEUR="/data/images"
SERVICE_DB="db"
SERVICE_APP="app"
BASE_DE_DONNEES="${POSTGRES_DB:-site}"
UTILISATEUR_DB="${POSTGRES_USER:-site}"
# Base Keycloak (cf. ADR-0027), meme instance Postgres que "site" mais dump a
# part : optionnelle, un environnement sans Keycloak (KEYCLOAK_DB non definie)
# garde le comportement d'avant sans y toucher.
BASE_KEYCLOAK="${KEYCLOAK_DB:-}"
UTILISATEUR_KEYCLOAK="${KEYCLOAK_DB_USER:-}"

mkdir -p "$DOSSIER_DESTINATION"
# Contient des dumps complets de la base (mots de passe hashes, donnees
# personnelles des abonnes/contacts) : jamais lisible par d'autres comptes
# locaux que celui qui exploite le VPS.
chmod 700 "$DOSSIER_DESTINATION"

DOSSIER_TEMPORAIRE="$(mktemp -d)"
trap 'rm -rf "$DOSSIER_TEMPORAIRE"' EXIT

echo "==> Sauvegarde de la base de donnees ($BASE_DE_DONNEES)..."
docker compose exec -T "$SERVICE_DB" pg_dump -U "$UTILISATEUR_DB" -F custom "$BASE_DE_DONNEES" > "$DOSSIER_TEMPORAIRE/base.dump"

FICHIERS_ARCHIVE="base.dump images.tar"
if [ -n "$BASE_KEYCLOAK" ]; then
    echo "==> Sauvegarde de la base Keycloak ($BASE_KEYCLOAK)..."
    docker compose exec -T "$SERVICE_DB" pg_dump -U "$UTILISATEUR_KEYCLOAK" -F custom "$BASE_KEYCLOAK" > "$DOSSIER_TEMPORAIRE/keycloak.dump"
    FICHIERS_ARCHIVE="$FICHIERS_ARCHIVE keycloak.dump"
fi

echo "==> Copie des images (volume Docker du service \"$SERVICE_APP\")..."
docker compose run --rm --no-deps -T --entrypoint sh "$SERVICE_APP" \
    -c "tar -cf - -C '$CHEMIN_IMAGES_CONTENEUR' ." > "$DOSSIER_TEMPORAIRE/images.tar"

echo "==> Creation de l'archive..."
tar -czf "$DOSSIER_DESTINATION/$NOM_ARCHIVE" -C "$DOSSIER_TEMPORAIRE" $FICHIERS_ARCHIVE
chmod 600 "$DOSSIER_DESTINATION/$NOM_ARCHIVE"

TAILLE="$(du -h "$DOSSIER_DESTINATION/$NOM_ARCHIVE" | cut -f1)"
echo "==> Sauvegarde terminee : $DOSSIER_DESTINATION/$NOM_ARCHIVE ($TAILLE)"
