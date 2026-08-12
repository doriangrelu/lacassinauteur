# ADR-0013 — Newsletter : double opt-in, jeton unique, adaptateur email dev/prod

**Statut** : Acté (implémentation), Brevo non vérifié contre l'API réelle
**Date** : 2026-08-12

## Contexte

Implémentation du domaine `newsletter` (Phase 4, cf. [roadmap](../../roadmap.md)) :
inscription publique avec consentement RGPD (double opt-in), confirmation,
désinscription, liste des abonnés en back-office. Le choix de principe (ESP tiers,
Brevo pressenti) est acté depuis [ADR-0002](0002-fournisseur-emailing.md) ; cet ADR
couvre les décisions prises à l'implémentation.

## Décisions

### 1. Un seul jeton, réutilisé confirmation ↔ désinscription

`AbonneNewsletter.jetonConfirmation` (UUID) sert à la fois de jeton pour le lien de
confirmation (email de double opt-in) et de jeton pour le lien de désinscription
(email de bienvenue, puis chaque envoi futur). Alternative envisagée : deux jetons
distincts (un par usage). Choix retenu — un seul jeton stable par abonné — pour
rester minimal : les deux liens sont mutuellement exclusifs dans le temps (le lien de
confirmation n'a plus d'usage une fois l'abonné confirmé, le lien de désinscription
n'existe que pour un abonné confirmé), donc aucun risque de collision d'usage. Le
jeton n'est régénéré qu'en cas de ré-inscription après désinscription
(`AbonneNewsletter.relancerInscription()`), pour invalider l'ancien lien.

### 2. Inscription idempotente plutôt qu'en échec bruyant

`InscrireAbonneUseCase` ne lève pas d'exception sur une ré-inscription :
- email déjà `CONFIRME` : retour silencieux, aucun ré-envoi (évite de spammer un
  abonné déjà actif si le formulaire public est resoumis) ;
- email `EN_ATTENTE_CONFIRMATION` : relance l'email de confirmation (nouveau jeton) ;
- email `DESINSCRIT` : relance une inscription complète (nouveau jeton) ;
- email inconnu : création normale.

Conséquence : `EmailDejaInscritException` (envisagée dans le cadrage initial) n'a pas
été créée — elle aurait été un type d'exception jamais levé par le seul point
d'entrée v1 (formulaire public en self-service), donc du code mort. Une exception
dédiée pourra être introduite si une future fonctionnalité (ex. ajout manuel d'un
abonné par l'auteur, hors périmètre de ce lot) a besoin d'un échec strict sur
doublon.

### 3. Adaptateur email : `LogEmailAdapter` (dev) vs `BrevoEmailAdapter` (par défaut/prod)

`EnvoiEmailPort` a deux implémentations, sélectionnées par profil Spring :
- `LogEmailAdapter` (`@Profile("dev")`) : logue le contenu (sujet + lien) au lieu
  d'appeler un service externe. Permet de tester le parcours complet inscription →
  confirmation → désinscription en local, y compris via `docker compose up -d`
  (profil `dev` actif dans `docker-compose.yml`), sans compte ni clé API.
- `BrevoEmailAdapter` (`@Profile("!dev")`, donc actif par défaut et en prod) : appelle
  l'API transactionnelle Brevo (`POST /v3/smtp/email`) via `RestClient`, clé API lue
  depuis `app.newsletter.brevo.api-key` (variable d'environnement `BREVO_API_KEY`,
  jamais commitée).

**Important — non vérifié contre l'API réelle** : aucun compte Brevo n'était
disponible au moment de l'implémentation (la création d'un compte externe est hors
périmètre d'un agent automatisé). `BrevoEmailAdapter` est écrit d'après la
documentation publique de l'API Brevo mais n'a fait l'objet d'aucun appel réel. C'est
une lacune connue et volontaire, pas un oubli — à vérifier manuellement dès que le
compte Brevo et sa clé API sont disponibles (cf.
[roadmap.md](../../roadmap.md), item restant Phase 4).

### 4. URL absolue des liens email : propriété `app.newsletter.url-base`

Un email n'a pas de « origine courante » comme un navigateur : les liens de
confirmation/désinscription doivent être absolus. Plutôt que de faire remonter la
construction de l'URL jusqu'au contrôleur (couplage à la requête HTTP en cours, alors
que l'envoi peut être différé), la base d'URL est une propriété de configuration
(`app.newsletter.url-base`, variable d'environnement `APP_URL_BASE`) injectée
directement dans `InscrireAbonneUseCase`/`ConfirmerInscriptionUseCase` — cohérent
avec le reste du projet où les cas d'usage reçoivent déjà des types Spring
(`@Component`, ports) sans que cela remette en cause la séparation des couches.

### 5. `CampagneNewsletter` et synchronisation ESP explicitement hors périmètre

Conforme à [domain-model.md](../domain-model.md) (« v1 minimal, potentiellement
délégué en grande partie à l'outil tiers ») : aucune entité `CampagneNewsletter`, pas
de `SynchroniserAbonneAvecEspUseCase`. `AbonneNewsletterRepository.findAllConfirmes()`
existe dans le port (contrat complet du repository) mais n'est consommé par aucun use
case de ce lot — préparé pour une future synchronisation ESP plutôt qu'ajouté après
coup.

### 6. Anti-spam : honeypot uniquement (pas de limitation par IP dans ce lot)

Le formulaire public (`InscriptionNewsletterForm`) inclut un champ honeypot
(`siteWeb`), masqué en CSS (`.piege-anti-spam` dans `frontend/public.css`) et jamais
révélé à l'utilisateur ou au bot : si rempli, la soumission est silencieusement
traitée comme un succès sans rien enregistrer. La limitation de fréquence par IP
(mentionnée « a minima » dans tech-stack.md) n'a pas été ajoutée dans ce lot — Bucket4j
existe déjà dans le projet pour l'anti brute-force de connexion
([ADR-0008](0008-anti-bruteforce-bucket4j.md)) et pourra être étendu au formulaire
newsletter si le spam s'avère un problème réel en production, plutôt que d'ajouter
une protection non éprouvée dès la v1.

## Conséquences

- Le parcours complet (inscription → email de confirmation → clic → confirmation →
  email de bienvenue avec lien de désinscription → désinscription) est testable de
  bout en bout en local via le profil `dev`, sans dépendance externe.
- `BrevoEmailAdapter` reste à valider manuellement une fois la clé API réelle
  disponible : test manuel recommandé — inscription réelle, vérifier réception de
  l'email de confirmation, cliquer le lien, vérifier réception de l'email de
  bienvenue.
- Si Brevo est finalement abandonné au profit d'un autre ESP (Mailjet, cf. décision
  en attente dans `roadmap.md`), seul `infrastructure.email` change : le port
  `EnvoiEmailPort` et tout le reste du domaine restent identiques (DIP).
