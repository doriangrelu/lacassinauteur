# Guide de mise en ligne

> Marche à suivre pour la toute première mise en production, une fois les accès
> (VPS, domaine) disponibles. La configuration technique est déjà prête et validée
> (cf. [ADR-0016](architecture/decisions/0016-deploiement-caddy-prod.md)) — ce qui
> reste relève d'actions humaines (comptes, paiement) que je ne peux pas faire à ta
> place, cf. [`docs/roadmap.md`](roadmap.md) Phase 7.

## Vue d'ensemble

1. Toi : provisionner le VPS Hetzner + acheter/configurer le domaine.
2. Toi : me transmettre l'adresse IP du VPS et confirmer que ma clé SSH y a été
   ajoutée.
3. Moi : préparer le serveur (Docker), cloner le dépôt, créer le `.env` réel,
   lancer `scripts/deploy.sh`, vérifier que tout fonctionne.
4. Toi : créer les comptes externes (Brevo, SMTP) et me transmettre les
   identifiants — ou me laisser les saisir directement dans le `.env` du VPS si tu
   préfères ne pas me les dicter en clair.

## 1. Provisionner le VPS Hetzner

Recommandation actée (cf. `roadmap.md` Phase 7) : **CX22** (2 vCPU, 4 Go RAM,
40 Go NVMe, ~4,35-4,59 €/mois), image **Ubuntu 24.04 LTS**, région **Falkenstein**
ou **Nuremberg** (proximité France).

À la création du serveur, dans la section clé SSH, ajoute la clé publique dédiée
que j'ai générée pour piloter les déploiements :

```
ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAINCgcrkt05xLVsXW3VPXbRCDjyBk1d8HjrbOGSB1DSrP claude-deploy-mybook
```

Une fois le serveur créé, transmets-moi son adresse IP — c'est tout ce dont j'ai
besoin pour m'y connecter en SSH avec la clé privée correspondante (conservée en
local, jamais commitée).

## 2. Acheter et configurer le domaine

Recommandation actée : **OVHcloud**, `thierrylacassin-auteur.fr`
(~4,99 € la 1ère année, ~7,79 €/an au renouvellement).

Une fois le domaine acheté, configure deux enregistrements DNS pointant vers
l'IP du VPS (chez OVHcloud : zone DNS du domaine) :

| Type | Nom | Valeur |
|---|---|---|
| A | `@` (apex) | IP du VPS |
| A | `www` | IP du VPS |

La propagation DNS peut prendre de quelques minutes à quelques heures. Pas besoin
d'attendre pour me transmettre l'IP — je peux préparer le serveur en parallèle, la
première émission de certificat HTTPS par Caddy attendra simplement que le DNS soit
propagé.

## 3. Ce que je fais une fois l'accès SSH confirmé

Sur le VPS, dans cet ordre :

1. Installer Docker Engine + le plugin Docker Compose (script officiel Docker,
   pas de configuration exotique nécessaire pour Ubuntu 24.04).
2. Cloner le dépôt (`git clone` — nécessite soit un accès en lecture au dépôt
   distant, soit un simple transfert des sources si le dépôt reste privé sans
   accès VPS ; à voir selon où le code est hébergé).
3. Copier `.env.example` vers `.env` et renseigner les valeurs réelles
   (mot de passe PostgreSQL généré aléatoirement, `APP_URL_BASE`, et les
   identifiants Brevo/SMTP dès qu'ils sont disponibles — cf. §4 ci-dessous, le
   site fonctionne déjà sans eux, ces fonctionnalités échoueront juste
   silencieusement en attendant, cf. ADR-0013/0014).
4. Lancer `scripts/deploy.sh` (sauvegarde préventive — sans effet sur un serveur
   vierge —, `git pull`, `docker compose -f docker-compose.prod.yml up -d --build`).
5. Vérifier `curl -fsS https://thierrylacassin-auteur.fr/actuator/health` et un
   chargement complet du site dans un navigateur, y compris le certificat HTTPS
   émis automatiquement par Caddy.
6. Vérifications SEO de base (sitemap si présent, meta descriptions, balises
   Open Graph déjà en place depuis la Phase 2 — cf. `roadmap.md`).

## 4. Comptes externes (newsletter et contact)

Deux fonctionnalités attendent des identifiants réels, déjà câblées et testées
en local avec des adaptateurs de log (profil `dev`) mais jamais vérifiées contre
les vrais services (aucun compte disponible au moment de leur implémentation) :

- **Brevo** (newsletter, cf. [ADR-0013](architecture/decisions/0013-newsletter-double-opt-in-brevo.md)
  et [ADR-0017](architecture/decisions/0017-synchronisation-brevo-campagnes.md)) :
  créer un compte, une clé API (`BREVO_API_KEY`), une liste de contacts dédiée
  (`BREVO_LISTE_ID`), et valider l'adresse expéditeur.
- **SMTP** (formulaire de contact, cf. [ADR-0014](architecture/decisions/0014-contact-smtp-generique.md)) :
  au choix, un Gmail existant (mot de passe d'application si validation en 2
  étapes) ou un fournisseur SMTP transactionnel dédié — le code est générique,
  aucune modification nécessaire selon le choix.

Tu peux me transmettre ces identifiants pour que je les saisisse dans le `.env`
du VPS, ou les saisir toi-même directement sur place si tu préfères ne pas me les
communiquer en clair — dans les deux cas, un simple `docker compose -f
docker-compose.prod.yml up -d` (sans rebuild) suffit à les prendre en compte après
modification du `.env`.

## 5. Sauvegardes régulières

`scripts/backup.sh` existe et fonctionne (cf. [ADR-0012](architecture/decisions/0012-sauvegarde-restauration.md))
mais n'est pas encore planifié. Une fois le VPS en place, je mettrai en place une
tâche cron (ex. quotidienne, 3h du matin) :

```
0 3 * * * cd /chemin/vers/le/depot && scripts/backup.sh >> /var/log/mybook-backup.log 2>&1
```

Les archives restent locales au VPS (`scripts/backup.sh` ne les exporte pas
ailleurs) — à envisager en v2 si une copie hors-site devient nécessaire (ex. envoi
vers un stockage objet).
