# ADR-0034 — Administration du VPS : socle versionné (`vps-socle`), compte `ia`, Keycloak sur branche fixe

**Statut** : Acté — modifie [ADR-0030](0030-socle-vps-partage.md) (socle non
versionné), [ADR-0032](0032-mise-a-jour-automatique-keycloak.md) (suivi de
`latest`) et [ADR-0012](0012-sauvegarde-restauration.md) (sauvegardes locales
seulement)
**Date** : 2026-11-19

## Contexte

Le VPS OVH héberge désormais trois projets qui partagent le même socle (Caddy,
PostgreSQL, Keycloak) : **Life Manager** (`maison.dorian-grelu.fr`), **ce site**
(mybook : `thierrylacassin-auteur.fr`, `leblogdethierry.fr`) et **Keycloak**
(`iabilis.fr`). Le 2026-11-19, une session d'audit lancée depuis Life Manager a
sécurisé et réorganisé le serveur. Plusieurs décisions de ce dépôt sont
devenues fausses :

- ADR-0030 décrivait `~/infra` et `~/keycloak` comme **non versionnés** ;
- ADR-0032 faisait suivre à Keycloak le tag **`latest`** ;
- les IA se connectaient en `ubuntu` avec la clé `claude-deploy-mybook` ;
- le panneau de supervision prévu (roadmap, Phase 9) n'était pas cadré ;
  Portainer avait été préparé puis jamais démarré.

La référence du serveur ne vit plus dans ce dépôt mais dans le dépôt privé
**`vps-socle`** (GitHub `doriangrelu/vps-socle`), dont la copie de travail est
`/home/ubuntu` sur le VPS. Ses fichiers `AGENTS.md`, `admin/INVENTAIRE.md`,
`admin/PROCEDURES.md` et `admin/JOURNAL.md` font foi.

## Décision

### 1. Le socle est versionné dans `vps-socle`

`/home/ubuntu` est un dépôt git : `AGENTS.md`, `admin/`, la configuration de
`~/infra`, `~/keycloak`, `~/dozzle`, `~/beszel` et des copies des réglages
système (`~/systeme/`). Les secrets (`.env`, etc.) n'y sont jamais. `~/mybook`
en est exclu : **le site reste versionné et déployé depuis ce dépôt-ci**.

Le VPS n'a **aucun accès à GitHub** (voulu) : on commite sur le serveur, puis
Dorian relaie vers GitHub depuis WSL (procédure 11 de `admin/PROCEDURES.md`).
L'alternative « dépôt dédié » écartée par ADR-0030 est donc retenue.

### 2. Les IA ont leur propre compte : `ia`

- La clé `claude-deploy-mybook` n'est plus acceptée sur `ubuntu` ; elle est sur
  l'utilisateur **`ia`** (`sudo` sans mot de passe, groupe `docker`).
- Une IA se connecte en `ssh ia@152.228.237.190` et travaille avec
  `sudo -iu ubuntu`, car tous les fichiers (dont `~/mybook`) appartiennent à
  `ubuntu`. Pour un script : `ssh ia@… "sudo -iu ubuntu bash -s" < script.sh`.
- Dorian garde `ssh ubuntu@…` avec ses propres clés. SSH par **clé uniquement**
  (plus de mot de passe).
- Toute IA lit `/home/ubuntu/AGENTS.md` avant d'agir et respecte ses niveaux
  d'autonomie (déployer mybook = **demander l'accord** de Dorian). Après chaque
  intervention : une entrée dans `~/admin/JOURNAL.md` **et** un commit dans
  `vps-socle` sur le serveur, à relayer ensuite vers GitHub.

Couper l'accès des IA (`sudo usermod -L -e 1 ia`) ne touche pas au compte de
Dorian.

### 3. Keycloak sur la branche fixe `26.8`

`~/keycloak/docker-compose.yml` utilise `quay.io/keycloak/keycloak:26.8`, plus
`latest`. `~/keycloak/auto-update.sh` (4 h 15 UTC) reste en place avec le même
filet de sécurité (vérification publique, retour arrière automatique), mais il
n'applique plus que les **correctifs** de la branche. Changer de branche
(26.9, 27…) se fait **à la main**, après lecture du guide de mise à jour
officiel (procédure 6 de `admin/PROCEDURES.md`).

Le risque accepté par ADR-0032 (saut de version majeure sans revue humaine)
disparaît.

### 4. Supervision : Dozzle et Beszel, pas de Portainer

- **Dozzle** (journaux des conteneurs) : https://docker.dorian-grelu.fr ;
- **Beszel** (métriques avec historique) : https://metrics.dorian-grelu.fr.

Les deux sont derrière oauth2-proxy + Keycloak (realm `infra`) et réservés à
Dorian. Portainer est **abandonné** (`~/portainer` supprimé, jamais démarré).
Cela tranche la question du « panneau web de supervision » de la Phase 9.

### 5. Sauvegardes hors site et alertes

- `~/infra/backup.sh` à 3 h UTC (14 archives gardées), puis copie **chiffrée
  vers OneDrive** à 3 h 30 (`~/infra/onedrive/send-backup.sh`, 30 jours) : la
  copie hors site que l'ADR-0012 laissait « à planifier » existe.
- Toutes les 10 min, `~/admin/signal-vie.sh` lance `~/admin/etat.sh` et prévient
  Dorian par Healthchecks.io en cas d'écart.
- Les anciennes sauvegardes propres au site (`~/mybook/backups`, ancien
  `scripts/backup.sh`) sont **arrêtées depuis août 2026** et ne servent plus.
  Le dossier existe encore sur le serveur ; son éventuelle suppression est une
  décision de Dorian (liste des points ouverts de `admin/JOURNAL.md`).

## Conséquences

- `CLAUDE.md` dit à une IA comment se connecter et quelles règles suivre ; le
  détail reste dans `vps-socle` pour ne pas dupliquer (et laisser dériver) deux
  sources.
- `docs/mode-operatoire-deploiement.md` est aligné (connexion par clé,
  sauvegardes du socle, outils de supervision).
- Ce qui touche au socle (Caddy, PostgreSQL, Keycloak, sauvegardes, cron) se
  décide et se documente dans `vps-socle`, plus dans les ADR de ce dépôt.
- `scripts/deploy.sh` est inchangé dans son comportement ; il appelle
  `~/infra/backup.sh </dev/null` pour pouvoir être lancé dans un script envoyé
  par SSH (`bash -s`) sans que la sauvegarde « avale » la suite du script.
