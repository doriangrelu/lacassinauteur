# Règles d'architecture

> Ce document est **normatif** : tout code ajouté au projet doit s'y conformer. Toute
> dérogation doit être discutée et actée par un ADR dans `docs/architecture/decisions/`
> avant d'être appliquée. À maintenir à jour à chaque évolution des règles.

## 1. Principe général : Clean Architecture par domaine métier

Le projet est un **monolithe modulaire** : un seul déployable Spring Boot, mais
organisé en **domaines métiers** (bounded contexts) indépendants les uns des autres à
l'exception d'un socle partagé (`shared`) volontairement minimal.

Domaines identifiés (détail dans [domain-model.md](domain-model.md)) :

- `catalogue` (univers, collections, livres, avis lecteurs)
- `actualite` (événements et actus)
- `newsletter` (abonnés, campagnes)
- `contact` (messages entrants)
- `identity` (comptes back-office, rôles)
- `shared` (kernel technique commun : types de base, exceptions transverses, config
  générique — **le moins de contenu possible**)

Chaque domaine est un package racine indépendant, et à l'intérieur de chaque domaine,
on retrouve systématiquement les 4 couches suivantes, **dans cet ordre de dépendance**
(une couche ne dépend que des couches à sa droite) :

```
presentation  →  application  →  domain  ←  infrastructure
```

- `domain` ne dépend de **rien** d'autre (ni Spring, ni JPA, ni aucune autre couche).
  C'est le cœur métier pur.
- `application` dépend uniquement de `domain`.
- `presentation` dépend de `application` (et de `domain` pour les types partagés),
  jamais de `infrastructure` directement.
- `infrastructure` dépend de `domain` (elle implémente les ports définis par le
  domaine) et éventuellement de `application` si elle doit déclencher des cas d'usage
  (ex. job planifié qui appelle un use case).
- **`presentation` n'est jamais dans `infrastructure`** : c'est une couche à part
  entière, au même niveau. Un contrôleur Spring MVC est un détail de présentation, pas
  un détail d'infrastructure technique (base de données, mail, etc.).

## 2. Couche `domain`

Contient le cœur métier, indépendant de tout framework :

- **`model`** : entités et value objects métier (classes Java simples ou `record`).
  Pas d'annotations JPA/Jackson ici.
- **`port`** : interfaces définissant les besoins du domaine/de l'application vis-à-vis
  de l'extérieur (ex. `LivreRepository`, `EnvoiEmailPort`). Implémentées dans
  `infrastructure`.
- **`exception`** : exceptions métier du domaine (ex. `LivreIntrouvableException`).
- **`event`** (si besoin) : événements de domaine.

Règle : aucune classe de `domain` n'importe `org.springframework.*`,
`jakarta.persistence.*`, etc.

## 3. Couche `application`

Traduit les intentions utilisateur en orchestration du domaine :

- **`usecase`** : **un cas d'usage métier = une classe = une responsabilité.**
  Convention de nommage : verbe à l'infinitif + `UseCase`
  (`PublierLivreUseCase`, `InscrireAbonneNewsletterUseCase`,
  `ListerLivresParCollectionUseCase`). Chaque use case expose une méthode d'entrée
  unique (`execute(...)` ou `handle(...)`), reçoit une commande/query en entrée
  (`record`), retourne un résultat métier ou lève une exception de domaine.
  Un use case peut orchestrer plusieurs ports et/ou services applicatifs, mais ne
  contient pas de logique dupliquée entre plusieurs use cases : cette logique commune
  va dans un service applicatif.
- **`service`** : **services applicatifs**, pour la logique **partagée entre
  plusieurs use cases** d'un même domaine (ex. `DisponibiliteLivreService` utilisé à
  la fois par `PublierLivreUseCase` et par la présentation de la fiche livre). Un
  service applicatif n'est pas un fourre-tout : s'il n'est utilisé que par un seul use
  case, sa logique reste dans le use case.
- **`command`** / **`query`** : objets d'entrée des use cases (records immuables).
- **`result`** ou **`dto`** : objets de sortie des use cases, à la frontière de la
  couche application (ne pas faire fuiter les entités `domain` telles quelles vers la
  présentation quand une projection suffit).
- **`port`** : ports supplémentaires purement applicatifs si nécessaire (rare — la
  majorité des ports vivent dans `domain`).

## 4. Couche `presentation`

Réservée aux préoccupations de présentation, jamais mêlée à l'infrastructure :

- **`web`** : contrôleurs Spring MVC + Thymeleaf pour le site public
  (`LivreController`, `UniversController`...).
- **`backoffice`** : contrôleurs Spring MVC + Thymeleaf/htmx pour l'espace de gestion
  réservé aux rôles `ADMIN`/`AUTEUR`.
- **`api`** (si besoin d'endpoints JSON, ex. pour htmx partiel ou future app mobile) :
  `@RestController`.
- **`viewmodel`** : objets de présentation construits à partir des résultats
  d'application (ne pas transmettre directement les entités `domain` aux templates).
- **`form`** : objets de formulaire (binding Thymeleaf), avec leur validation Jakarta
  Bean Validation.
- **`mapper`** : conversion résultat d'application ↔ viewmodel/form.

## 5. Couche `infrastructure`

Détails techniques, remplaçables sans toucher au métier :

- **`persistence`** : entités JPA (`@Entity`, distinctes des entités `domain`),
  `Spring Data` repositories, implémentations des ports `*Repository` du domaine
  (`JpaLivreRepository implements LivreRepository`), mappers entité JPA ↔ entité
  domaine.
- **`email`** : adaptateur du fournisseur d'emailing (Brevo) implémentant les ports
  d'envoi définis dans `domain`/`application`.
- **`security`** : configuration Spring Security, `UserDetailsService`, filtres.
- **`config`** : configuration Spring (beans, propriétés) propre au domaine.

## 6. Règle de sous-packaging

**Aucun package ne mélange plusieurs types d'objets.** Un package contenant à la fois
un contrôleur et un DTO, ou une entité et un repository, est une violation de cette
règle. Chaque type d'objet (use case, service, command, entité, port, mapper,
contrôleur, viewmodel, entité JPA, etc.) a son propre sous-package, y compris quand
cela signifie des packages ne contenant qu'une seule classe.

Voir [package-structure.md](package-structure.md) pour l'arborescence complète.

## 7. Principes SOLID appliqués

- **SRP** : un use case = une responsabilité métier ; un service applicatif = une
  responsabilité partagée précise ; pas de classe « fourre-tout » de type `LivreUtils`.
- **OCP** : les ports (`interface`) permettent d'ajouter un nouvel adaptateur
  (ex. changer de fournisseur d'emailing) sans modifier le domaine/l'application.
- **LSP** : toute implémentation d'un port respecte intégralement son contrat
  (exceptions, invariants).
- **ISP** : des ports fins et spécifiques plutôt qu'un unique gros port par domaine
  (ex. `LivreRepository` pour la persistance des livres, `EnvoiEmailPort` pour l'envoi
  d'email, pas un `CataloguePort` monolithique).
- **DIP** : `application` et `presentation` dépendent d'abstractions (`port`) définies
  dans `domain`, jamais d'implémentations concrètes d'`infrastructure`. Le câblage
  concret (quelle implémentation pour quel port) se fait via l'injection de dépendances
  Spring, configurée dans `infrastructure/config` ou via les annotations
  `@Repository`/`@Service` sur les adaptateurs.

## 8. Java moderne

- Java 25 (cf. [tech-stack.md](tech-stack.md)). Utiliser `record` pour les value
  objects, commandes, queries, résultats, DTOs immuables.
- Lambdas et Stream API pour les transformations de collections, pas de boucles
  impératives quand un stream est plus lisible.
- `switch` expressions et pattern matching (`instanceof` pattern, `switch` sur
  `sealed interface`) plutôt que des chaînes de `if/else instanceof`.
- Pas de Lombok : les records et le Java moderne couvrent l'essentiel du besoin
  (immutabilité, `equals`/`hashCode`/`toString` générés). Pour les entités JPA
  mutables (nécessaires pour Hibernate), écrire les accesseurs explicitement.
- Injection de dépendances par constructeur uniquement (pas de `@Autowired` sur champ).

## 9. Sécurité (Spring Security)

- Le site public (`/`, `/livres/**`, `/newsletter`, `/contact`, `/actualites`, page
  pro) est accessible sans authentification.
- Le back-office (`/backoffice/**`) exige une authentification, avec deux rôles :
  `ADMIN` et `AUTEUR` (détail des permissions dans
  [ADR-0003](decisions/0003-roles-multiples.md)).
- Les formulaires publics avec écriture (newsletter, contact, avis lecteur) sont
  protégés contre le spam (honeypot et/ou limitation de fréquence par IP a minima ;
  CAPTCHA si le spam s'avère un problème réel après mise en ligne).

## 10. Tests

- **`domain`** et **`application`** : tests unitaires purs (JUnit 5 + AssertJ), sans
  contexte Spring, avec des implémentations de test des ports (pas de mock-heavy si
  un fake suffit).
- **`infrastructure.persistence`** : tests d'intégration avec **Testcontainers**
  (PostgreSQL réel, pas H2, pour éviter les écarts de comportement SQL).
- **`presentation`** : tests `@WebMvcTest` ciblés sur les contrôleurs, use cases
  mockés.
- Un test de chaque use case couvre au minimum le chemin nominal et le principal
  chemin d'erreur métier.

## 11. Documentation vivante

`/docs` fait partie de la definition of done : toute création de domaine, tout
changement de règle d'architecture, toute décision structurante se traduit par une
mise à jour de ce dossier **dans le même travail**, pas après coup.
