# ADR-0020 — Jeton de réinitialisation à usage unique (table dédiée)

**Statut** : Acté — remplace le choix « purement stateless » d'ADR-0018
**Date** : 2026-08-17

## Contexte

ADR-0018 avait délibérément choisi un JWT purement stateless pour le jeton de
réinitialisation de mot de passe, en notant explicitement dans ses
« Alternatives envisagées » qu'un jeton non révocable individuellement était
un compromis acceptable pour la v1. L'utilisateur a demandé, après coup, deux
garanties qu'un JWT seul ne peut pas offrir :

1. Le jeton doit être définitivement invalidé après usage (« brûlé »), pas
   seulement à expiration.
2. Un seul jeton actif à la fois par compte — impossible d'en générer un
   second tant qu'un jeton valide existe déjà.

## Décision

### JWT conservé pour la partie cryptographique, table dédiée pour l'usage unique

Le jeton reste un JWT signé HS256 (aucun changement côté format, secret,
durée de validité — cf. ADR-0018), mais son identifiant standard `jti` porte
désormais l'id d'une ligne dans une nouvelle table `jeton_reinitialisation`
(id, utilisateur_id, jeton, date_expiration — migration V10). Le JWT seul ne
suffit plus à prouver la validité : `ReinitialiserMotDePasseUseCase` vérifie
la signature/expiration du JWT (`JetonReinitialisationMotDePassePort`) **et**
que la ligne correspondante existe toujours et n'est pas expirée
(`JetonReinitialisationRepository`) avant d'appliquer le nouveau mot de passe.

### Dé-référencement à l'usage

La ligne est supprimée (`deleteById`) dès que le mot de passe est effectivement
changé — un second essai avec le même lien échoue immédiatement même si le JWT
lui-même n'a pas encore expiré (fenêtre de 15 minutes).

### Un seul jeton actif par compte

`DemanderReinitialisationMotDePasseUseCase` cherche d'abord un jeton non expiré
existant pour le compte (`findValidePourUtilisateur`) ; s'il y en a un, le même
lien est renvoyé par email plutôt que d'en émettre un second — évite
d'accumuler des jetons concurrents pour le même compte. Si l'existant a expiré,
il est supprimé avant d'en créer un nouveau (`deleteParUtilisateur`), pour
garder au plus une ligne par utilisateur dans la table à tout instant (pas de
contrainte d'unicité SQL : la fenêtre entre lecture et écriture reste
acceptable à l'échelle d'un compte ADMIN/AUTEUR unique sur un petit site).

## Alternatives envisagées

- **Jeton opaque en base sans JWT du tout** (comme
  `AbonneNewsletter.jetonConfirmation`) : aurait été plus simple, mais le JWT
  était une exigence explicite de l'utilisateur dès la demande initiale
  (ADR-0018) — conservé pour ne pas revenir dessus, la table dédiée comble
  seulement ce que le JWT seul ne pouvait pas garantir.
- **Contrainte d'unicité SQL sur `utilisateur_id`** : écartée au profit d'une
  suppression explicite des jetons expirés avant d'en créer un nouveau — une
  contrainte stricte aurait bloqué toute nouvelle demande tant que l'ancienne
  ligne (même expirée) n'est pas nettoyée, ce qui n'est pas le comportement
  voulu (« interdiction d'en générer un autre **si un valide existe déjà** »,
  pas si un jeton quelconque, même expiré, traîne encore).

## Conséquences

- Nouvelle migration Flyway V10 (`jeton_reinitialisation`), FK vers
  `utilisateur(id)` en cascade.
- `JetonReinitialisationMotDePassePort` change de signature
  (`genererJeton(utilisateurId, jetonId)`, `validerEtExtraire` renvoie un
  `JetonDecode(utilisateurId, jetonId)`) — mise à jour d'ADR-0018 par cet ADR,
  pas de nouvel ADR pour le port lui-même.
