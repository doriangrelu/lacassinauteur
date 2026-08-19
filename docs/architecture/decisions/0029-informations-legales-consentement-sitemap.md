# ADR-0029 — Domaine `legal`, recueil du consentement RGPD et génération du `sitemap.xml`

**Statut** : Acté
**Date** : 2026-08-19

## Contexte

Trois manques constatés sur le site en production :

1. **Aucune page légale.** Ni mentions légales (obligatoires en France, LCEN
   art. 6) ni politique de confidentialité (obligatoire dès lors qu'on collecte
   des données personnelles, RGPD art. 13) — alors que le site collecte des
   emails via la newsletter, des messages via le formulaire de contact et des
   avis de lecteurs.
2. **Aucun recueil de consentement.** Le brief demandait pourtant une « mention
   RGPD » sur le formulaire newsletter (§5). Seul un paragraphe informatif
   existait sous le formulaire, sans case à cocher ni lien vers une politique.
3. **Pas de `sitemap.xml`** (404 vérifié en production), alors que l'indexation
   Google venait d'être déclenchée via la Search Console.

L'utilisateur a demandé que les informations légales soient éditables depuis le
back-office, en précisant que **le texte lui-même n'a pas vocation à changer** :
seules quelques valeurs le doivent.

## Décision

### Un domaine `legal`, avec le texte figé et seules les variables éditables

Septième domaine métier, même schéma de sous-packages que les autres. Il porte
`InformationsLegales` : identité de l'éditeur, directeur de publication,
hébergeur, durées de conservation.

**Le texte légal vit dans les gabarits Thymeleaf, pas en base.** C'est la
demande explicite de l'utilisateur, et c'est aussi le bon découpage : ce texte
engage juridiquement, il est relu une fois puis versionné avec le code (donc
tracé par Git), là où un champ libre en base inviterait à le réécrire sans
relecture. Seules les **valeurs** varient, et ce sont précisément celles qui
peuvent changer dans la vie du site (déménagement, changement d'hébergeur).

Enregistrement unique, garanti en base par une contrainte, comme
`biographie` (cf. [ADR-0028](0028-domaine-biographie-et-qr-code-fiche-pro.md)).

**Identité de l'éditeur laissée vide au seed** : elle relève d'informations
juridiques réelles que seul l'auteur peut fournir. Les inventer aurait produit
des mentions légales fausses, ce qui est pire que des mentions incomplètes. Les
pages publiques affichent donc explicitement « à compléter » sur les champs
manquants, et le back-office affiche un avertissement tant que les mentions
obligatoires ne sont pas toutes renseignées — le manque se voit, au lieu de
passer inaperçu.

Seul l'hébergeur est pré-rempli (OVHcloud, cf.
[ADR-0016](0016-deploiement-caddy-prod.md)) : c'est un fait technique connu, pas
une donnée personnelle à deviner.

### Deux pages séparées plutôt que des CGU

L'utilisateur parlait de « CGU », mais le besoin décrit correspond aux
**mentions légales** et à la **politique de confidentialité**. Des CGU régissent
la relation contractuelle d'un service ou d'une vente : ce site est une vitrine
sans compte visiteur ni transaction, elles feraient doublon sans rien ajouter.
Arbitrage confirmé avec l'utilisateur avant implémentation.

`/mentions-legales` et `/confidentialite`, indexables et liées depuis le pied de
page : un visiteur qui cherche comment exercer ses droits doit pouvoir les
trouver.

### Consentement : case obligatoire, sans colonne dédiée en base

Case à cocher **non pré-cochée et bloquante** (`@AssertTrue`) sur les
formulaires newsletter et contact, avec lien vers la politique de
confidentialité.

**Aucune colonne « consentement » ni « date de consentement » n'est ajoutée.**
Le formulaire ne peut pas aboutir sans la case : tout enregistrement existant
implique donc un consentement, et les dates déjà stockées
(`date_inscription`, `date_reception`) le datent de fait. Une colonne
systématiquement à `true` n'apporterait aucune preuve supplémentaire, seulement
une migration et un champ à maintenir. La newsletter conserve par ailleurs son
double opt-in, preuve plus forte encore.

### `sitemap.xml` généré à la volée, alimenté par un port

Généré à chaque appel plutôt que stocké : le catalogue change depuis le
back-office, un fichier figé deviendrait faux sans que personne ne s'en
aperçoive. Le volume (une vingtaine d'URL) rend le coût négligeable.

Le sitemap doit connaître des URL de plusieurs domaines. Pour ne pas faire
dépendre `shared` du catalogue — interdit par les règles du projet — un port
`FournisseurUrlsPubliquesPort` est défini dans `shared`, et le catalogue
l'implémente : la dépendance va du domaine vers `shared`, dans le bon sens. Les
pages fixes (accueil, contact, pages légales…) restent une simple constante du
contrôleur : ce sont des chemins de routage, pas de la logique métier, et les
faire transiter par un port n'aurait rien apporté.

**Les fiches professionnelles `/livres/{slug}/pro` sont volontairement exclues**
du sitemap : elles sont en `noindex` et diffusées par l'auteur via QR code (cf.
ADR-0028). Les y lister reviendrait à demander leur indexation, exactement
l'inverse de l'intention. Un test le vérifie explicitement, car c'est la
régression la plus plausible de ce mécanisme.

`robots.txt` référence désormais le sitemap. Il continue de **ne pas** interdire
`/livres/*/pro` : un `Disallow` empêcherait les moteurs de lire la balise
`noindex` de la page, ce qui pourrait paradoxalement laisser l'URL indexée si un
tiers la partageait.

## Alternatives envisagées

- **Stocker le texte légal complet en base, éditable en WYSIWYG** : écarté —
  contraire à la demande, et un texte qui engage juridiquement gagne à être
  versionné et relu, pas modifiable à la volée sans trace.
- **Pré-remplir l'identité de l'éditeur avec des valeurs plausibles** : écarté —
  des mentions légales inexactes sont juridiquement pires qu'incomplètes.
- **Une seule page groupant mentions légales et confidentialité** : proposé à
  l'utilisateur, qui a préféré deux pages distinctes (pratique la plus
  répandue, et lien direct plus clair depuis les formulaires).
- **Colonne `date_consentement` dédiée** : proposée, écartée après arbitrage —
  redondante avec les dates existantes, cf. ci-dessus.
- **Sitemap statique régénéré au déploiement** : écarté — le catalogue évolue
  entre deux déploiements, via le back-office.
- **Contrôleur de sitemap important directement les use cases du catalogue** :
  écarté — ferait dépendre `shared` d'un domaine métier, ce que `CLAUDE.md`
  interdit explicitement.
- **Suppression automatique des données à l'échéance annoncée** : hors périmètre
  de cette passe. Les durées affichées sont un engagement à tenir manuellement,
  ce que le back-office signale explicitement pour ne pas laisser croire à une
  purge automatique.

## Conséquences

- Nouveau domaine `fr.lacassinauteur.site.legal`, migration
  `V12__informations_legales_init.sql`.
- Nouvelles routes publiques `/mentions-legales`, `/confidentialite`,
  `/sitemap.xml` ; liens ajoutés au pied de page.
- Nouvel écran `/backoffice/informations-legales` et entrée de sidebar.
- `shared` gagne `FournisseurUrlsPubliquesPort` et `SitemapController` ;
  `catalogue` gagne `CatalogueUrlsPubliquesAdapter`.
- Nouvelle propriété `app.site.url-base` (un sitemap n'accepte que des URL
  absolues).
- Les formulaires newsletter et contact gagnent une case de consentement
  obligatoire.
- **À faire côté utilisateur** : renseigner l'identité de l'éditeur dans
  `/backoffice/informations-legales`, sans quoi les pages légales affichent
  publiquement « à compléter ». Puis soumettre le sitemap dans la Search Console.
- **Non couvert** : purge automatique des données à l'expiration des durées
  annoncées, et registre des traitements (non obligatoire à cette échelle, mais
  à réévaluer si l'activité grandit).
