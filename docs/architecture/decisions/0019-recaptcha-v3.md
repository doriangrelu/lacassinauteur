# ADR-0019 — reCAPTCHA v3 en complément du honeypot sur les formulaires publics

**Statut** : Acté
**Date** : 2026-08-17

## Contexte

Les trois formulaires publics (newsletter, contact, avis lecteurs) sont
protégés par honeypot depuis leur création (cf. `HoneypotAntiSpam`), mais ce
mécanisme ne filtre que les bots les plus simples. L'utilisateur a demandé
l'ajout de Google reCAPTCHA v3 (clés de site/secrète déjà fournies) en couche
supplémentaire.

## Décision

### Port dans `shared`, pas dans chaque domaine

Contrairement aux adaptateurs email (volontairement dupliqués par domaine, cf.
ADR-0018), la vérification captcha est une capacité **purement technique, sans
aucun contenu métier** (un jeton entre, un booléen sort) — exactement le cas
d'usage déjà cité en exemple dans `package-structure.md` pour justifier
`shared` (`StockageFichierPort`). D'où `shared.domain.port.CaptchaPort` +
`shared.infrastructure.captcha.RecaptchaV3Adapter`, réutilisé tel quel par les
trois domaines concernés.

### Fermé par défaut

`RecaptchaV3Adapter.verifier` renvoie `false` si la clé secrète est absente, si
le jeton est vide, ou si l'appel à l'API Google échoue — jamais d'ouverture par
défaut en cas de panne. Le score minimal accepté (`app.captcha.seuil-score`,
0.5 par défaut) est configurable sans redéploiement.

### Même traitement qu'un honeypot déclenché

Un jeton absent/invalide ne renvoie pas d'erreur de validation au visiteur : le
contrôleur se comporte comme en cas de honeypot rempli (redirection avec le
même message de succès, sans rien enregistrer ni révéler le mécanisme) — cf.
`NewsletterController`, `ContactController`, `LivreController`. Cohérent avec
le choix déjà fait pour le honeypot, et évite de perturber un visiteur légitime
qui obtiendrait occasionnellement un score bas (reCAPTCHA v3 n'a pas de
« second essai » explicite comme v2 ; mieux vaut laisser passer un faux négatif
que bloquer un vrai visiteur avec une erreur peu compréhensible).

### Exécution silencieuse côté client

reCAPTCHA v3 n'affiche aucun défi — le script Google
(`recaptcha/api.js?render=SITE_KEY`), chargé uniquement sur les trois pages
concernées (pas globalement dans le layout), exécute la vérification au moment
de la soumission via `static/js/captcha.js` (générique, activé par l'attribut
`data-captcha-site-key` plutôt que dupliqué par page), pose le jeton dans un
champ cache `captchaToken`, puis soumet réellement le formulaire.

## Conséquences

- Nouvelles variables d'environnement : `RECAPTCHA_SITE_KEY` (publique par
  nature, exposée côté navigateur) et `RECAPTCHA_SECRET_KEY` (jamais commise).
- Aucune dépendance Maven supplémentaire : un simple appel HTTP suffit
  (`RestClient` déjà utilisé partout ailleurs pour les intégrations tierces).
- Pas de test unitaire de `RecaptchaV3Adapter` contre l'API réelle, même
  situation que les adaptateurs Brevo (ADR-0013/0017/0018) — vérifié
  manuellement une fois les clés configurées en production.
