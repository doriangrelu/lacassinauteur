# ADR-0017 — Synchronisation des abonnés vers Brevo plutôt qu'un éditeur de campagnes maison

**Statut** : Acté
**Date** : 2026-08-14

## Contexte

`domain-model.md` prévoyait dès la conception initiale un use case « Synchroniser
les abonnés confirmés avec l'ESP tiers (Brevo) », `CampagneNewsletter` étant
explicitement marqué « v1 minimal, potentiellement délégué en grande partie à
l'outil tiers ». La Phase 4 (ADR-0013) avait volontairement laissé cette
synchronisation hors périmètre pour livrer d'abord le double opt-in. L'utilisateur
a maintenant demandé de « gérer les campagnes de newsletter ».

Deux approches possibles :
1. Synchroniser les abonnés confirmés vers une liste de contacts Brevo, et laisser
   Thierry composer/envoyer ses newsletters depuis l'interface Brevo elle-même
   (éditeur visuel, statistiques d'ouverture déjà matures).
2. Construire un éditeur de campagnes complet dans notre back-office (sujet,
   contenu, déclenchement d'envoi en masse), qui appelle l'API Brevo en coulisses.

Question posée à l'utilisateur le 2026-08-14 : approche 1 retenue.

## Décision

- Nouveau port `newsletter.domain.port.SynchronisationEspPort`
  (`ajouterOuMettreAJour` / `retirer`), même schéma que `EnvoiEmailPort` (deux
  implémentations selon le profil Spring) :
  - `BrevoContactSyncAdapter` (`!dev`) : appelle l'API Contacts Brevo
    (`POST /v3/contacts` pour ajouter/mettre à jour avec `updateEnabled: true`,
    `PUT /v3/contacts/{email}` avec `unlinkListIds` pour retirer).
  - `LogSynchronisationEspAdapter` (`dev`) : logue au lieu d'appeler l'API,
    même approche que le reste du domaine newsletter/contact.
- `ConfirmerInscriptionUseCase` et `DesinscrireAbonneUseCase` appellent ce port
  respectivement après confirmation et après désinscription — synchronisation en
  temps réel, pas de tâche planifiée séparée à maintenir.
- Un échec de synchronisation Brevo est **rattrapé et logué, jamais propagé** : la
  confirmation/désinscription est déjà enregistrée en base à ce stade, un problème
  côté Brevo ne doit pas se traduire par une erreur 500 pour le visiteur (même
  raisonnement que la correction apportée au domaine contact, cf. relecture de code
  du 2026-08-13).
- Nouvelle propriété `app.newsletter.brevo.liste-id` (variable d'environnement
  `BREVO_LISTE_ID`, défaut `0` = non configurée, synchronisation silencieusement
  ignorée avec un log d'avertissement plutôt qu'une erreur).
- **Aucun éditeur de campagnes n'est construit dans le back-office** : Thierry
  compose et envoie ses newsletters directement dans Brevo, une fois sa liste de
  contacts synchronisée automatiquement par le site.

## Alternatives envisagées

- **Éditeur de campagnes intégré** (option 2 ci-dessus) : écartée par l'utilisateur
  — chantier bien plus lourd (éditeur de contenu, envoi en masse, suivi des
  ouvertures/clics) qui dupliquerait des fonctionnalités déjà matures chez Brevo,
  pour un bénéfice limité (Thierry gère déjà plusieurs outils : back-office +
  réseaux sociaux).
- **Synchronisation par lot planifiée (cron)** plutôt qu'en temps réel : écartée —
  la synchronisation à l'événement (confirmation/désinscription) est plus simple
  (pas de job à orchestrer) et garantit que la liste Brevo reste à jour sans délai,
  ce qui compte particulièrement pour les désinscriptions (obligation légale de les
  respecter rapidement).

## Conséquences

- `BREVO_LISTE_ID` reste à fournir en production, une fois la liste créée dans
  Brevo (même situation que `BREVO_API_KEY`, cf. ADR-0013) — non testé contre
  l'API réelle faute de compte disponible au moment de l'implémentation.
- Si un vrai besoin d'éditeur de campagnes en interne apparaît plus tard (retour de
  Thierry après usage réel), ce sera une extension explicite du domaine
  `newsletter`, pas une réécriture de cette décision.
