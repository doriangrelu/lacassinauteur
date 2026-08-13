# ADR-0014 — Domaine contact : SMTP générique, statut lu-implicite-à-la-consultation

**Statut** : Acté
**Date** : 2026-08-13

## Contexte

Le formulaire de contact doit notifier l'auteur par email transactionnel à chaque
message reçu, et l'enregistrer pour consultation ultérieure en back-office. La
roadmap laissait ouvert le choix du fournisseur (compte Gmail existant de l'auteur
vs SMTP transactionnel dédié type Brevo), sans compte ni identifiants disponibles au
moment de l'implémentation — même situation que la newsletter (cf. ADR-0013).

## Décision

### Transport email

- Port `contact.domain.port.EnvoiEmailContactPort` (une seule méthode,
  `envoyerNotification`), implémenté par deux adaptateurs selon le profil Spring,
  même approche que `newsletter.infrastructure.email` (ADR-0013) :
  - `SmtpEnvoiEmailContactAdapter` (`!dev`) : envoie un vrai email via
    `JavaMailSender`.
  - `LogEnvoiEmailContactAdapter` (`dev`) : logue le message au lieu de l'envoyer,
    pour tester le parcours complet en local sans identifiants réels.
- Contrairement à la newsletter (API HTTP Brevo dédiée), le contact utilise
  **SMTP générique** (`spring-boot-starter-mail`, déjà une dépendance du projet) :
  host/port/identifiants configurables (`app.contact.smtp.*`), sans verrouiller un
  fournisseur précis — fonctionne aussi bien avec le compte Gmail existant de
  l'auteur (mot de passe d'application) qu'avec un relais SMTP transactionnel. Le
  choix définitif reste une décision opérationnelle de déploiement (quelles
  variables d'environnement renseigner), pas une décision architecturale.
- Le bean `JavaMailSender` est **construit manuellement** (`SmtpContactClientConfig`)
  à partir de `SmtpContactProperties`, plutôt que de dépendre de
  l'auto-configuration Spring Boot (conditionnelle à la présence de
  `spring.mail.host`, absente tant qu'aucun SMTP réel n'est fourni). Ce choix évite
  qu'un démarrage sans SMTP configuré échoue faute de bean disponible — leçon tirée
  d'un vrai incident de cette nature rencontré ailleurs dans le projet lors de cette
  session (profil Spring manquant faisant échouer un autre module au démarrage).

### Statuts du message (`NOUVEAU` / `LU` / `TRAITE`)

Le brief ne décrit explicitement que « consultation » et « marquage traité », mais
`domain-model.md` définit trois statuts. Plutôt que de laisser `LU` inutilisé :
consulter un message (`GET /backoffice/messages/{id}`) le marque automatiquement
comme lu s'il était `NOUVEAU`, sans écraser un statut déjà plus avancé (`TRAITE`).
`TRAITE` reste une action explicite (bouton dédié). Ce comportement « lu à la
consultation » est un idiome courant de boîte de réception, cohérent avec l'intention
du modèle de domaine sans inventer d'écran supplémentaire.

## Alternatives envisagées

- **Verrouiller un fournisseur SMTP précis dès maintenant** (Gmail ou Brevo) : écarté
  — la décision n'a pas été tranchée par l'utilisateur, et le port générique ne coûte
  rien de plus qu'un port spécifique à un fournisseur.
- **Dépendre de l'auto-configuration `spring.mail.*` de Spring Boot** : écarté — le
  bean `JavaMailSender` ne serait créé que si `spring.mail.host` est renseigné,
  risque de `NoSuchBeanDefinitionException` au démarrage tant que le SMTP réel n'est
  pas configuré (cf. construction manuelle ci-dessus).
- **Bouton « marquer lu » séparé** : écarté — redondant avec le geste naturel de
  consultation, ajoute un clic sans bénéfice pour l'usage réel.

## Conséquences

- `CONTACT_SMTP_HOST`/`CONTACT_SMTP_USERNAME`/`CONTACT_SMTP_PASSWORD` (et
  `CONTACT_EMAIL_AUTEUR`) restent à fournir en variables d'environnement de
  production ; tant qu'ils sont vides, `SmtpEnvoiEmailContactAdapter` échouera à
  l'envoi (pas au démarrage) — comportement attendu et sans impact tant que le
  formulaire n'est pas encore utilisé en production.
- Migration Flyway `V7__contact_init.sql`.
