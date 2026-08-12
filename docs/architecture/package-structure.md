# Structure de packages

> Application concrète des règles de [architecture.md](architecture.md) et du
> découpage de [domain-model.md](domain-model.md). Groupe racine (base package) :
> `fr.lacassinauteur.site`.

## Arborescence complète (exemple détaillé : domaine `catalogue`)

```
fr.lacassinauteur.site
└── catalogue
    ├── domain
    │   ├── model
    │   │   ├── Univers.java
    │   │   ├── Collection.java
    │   │   ├── Livre.java
    │   │   ├── LienAchat.java              (record)
    │   │   ├── FicheProfessionnelle.java   (record)
    │   │   ├── AvisLecteur.java
    │   │   └── StatutAvis.java             (enum)
    │   ├── port
    │   │   ├── UniversRepository.java
    │   │   ├── CollectionRepository.java
    │   │   ├── LivreRepository.java
    │   │   └── AvisLecteurRepository.java
    │   └── exception
    │       ├── LivreIntrouvableException.java
    │       ├── CollectionIntrouvableException.java
    │       └── UniversIntrouvableException.java
    │
    ├── application
    │   ├── usecase
    │   │   ├── univers
    │   │   │   ├── CreerUniversUseCase.java
    │   │   │   ├── ModifierUniversUseCase.java
    │   │   │   └── ListerUniversUseCase.java
    │   │   ├── collection
    │   │   │   ├── CreerCollectionUseCase.java
    │   │   │   └── ListerCollectionsParUniversUseCase.java
    │   │   ├── livre
    │   │   │   ├── CreerLivreUseCase.java
    │   │   │   ├── PublierLivreUseCase.java
    │   │   │   ├── ConsulterLivreUseCase.java
    │   │   │   └── DefinirDerniereParutionUseCase.java
    │   │   └── avis
    │   │       ├── SoumettreAvisLecteurUseCase.java
    │   │       ├── ApprouverAvisLecteurUseCase.java
    │   │       └── RejeterAvisLecteurUseCase.java
    │   ├── service
    │   │   └── DisponibiliteLivreService.java
    │   ├── command
    │   │   ├── CreerLivreCommand.java
    │   │   ├── PublierLivreCommand.java
    │   │   └── SoumettreAvisLecteurCommand.java
    │   ├── query
    │   │   └── ListerLivresParCollectionQuery.java
    │   └── result
    │       ├── LivreDetailResult.java
    │       └── LivreResumeResult.java
    │
    ├── infrastructure
    │   ├── persistence
    │   │   ├── entity
    │   │   │   ├── UniversJpaEntity.java
    │   │   │   ├── CollectionJpaEntity.java
    │   │   │   ├── LivreJpaEntity.java
    │   │   │   └── AvisLecteurJpaEntity.java
    │   │   ├── repository
    │   │   │   ├── SpringDataUniversRepository.java
    │   │   │   ├── SpringDataCollectionRepository.java
    │   │   │   ├── SpringDataLivreRepository.java
    │   │   │   └── SpringDataAvisLecteurRepository.java
    │   │   ├── adapter
    │   │   │   ├── JpaUniversRepository.java        (implements domain.port.UniversRepository)
    │   │   │   ├── JpaCollectionRepository.java
    │   │   │   ├── JpaLivreRepository.java
    │   │   │   └── JpaAvisLecteurRepository.java
    │   │   └── mapper
    │   │       ├── UniversEntityMapper.java
    │   │       ├── CollectionEntityMapper.java
    │   │       ├── LivreEntityMapper.java
    │   │       └── AvisLecteurEntityMapper.java
    │   └── config
    │       └── CatalogueBeansConfig.java   (si câblage explicite nécessaire)
    │
    └── presentation
        ├── web
        │   ├── AccueilController.java
        │   ├── UniversController.java
        │   ├── CollectionController.java
        │   ├── LivreController.java
        │   └── PageProfessionnelleController.java
        ├── backoffice
        │   ├── BackofficeUniversController.java
        │   ├── BackofficeCollectionController.java
        │   ├── BackofficeLivreController.java
        │   └── BackofficeAvisLecteurController.java
        ├── viewmodel
        │   ├── LivreDetailViewModel.java
        │   ├── LivreCarteViewModel.java
        │   ├── CollectionPageViewModel.java
        │   └── UniversPageViewModel.java
        ├── form
        │   ├── LivreForm.java
        │   ├── CollectionForm.java
        │   ├── UniversForm.java
        │   └── AvisLecteurForm.java
        └── mapper
            ├── LivreViewModelMapper.java
            └── CollectionViewModelMapper.java
```

Les domaines `actualite`, `newsletter`, `contact`, `identity` suivent **exactement le
même schéma de sous-packages** (`domain.model` / `domain.port` / `domain.exception`,
`application.usecase.<sous-thème>` / `application.service` / `application.command` /
`application.query` / `application.result`, `infrastructure.persistence.*` /
`infrastructure.<adaptateur technique>`, `presentation.web` /
`presentation.backoffice` / `presentation.viewmodel` / `presentation.form` /
`presentation.mapper`), avec leurs propres sous-thèmes dans `usecase` :

```
newsletter/application/usecase/
├── InscrireAbonneUseCase.java
├── ConfirmerInscriptionUseCase.java
├── DesinscrireAbonneUseCase.java
└── SynchroniserAbonneAvecEspUseCase.java

newsletter/infrastructure/
├── persistence/...
└── email/
    ├── BrevoEmailAdapter.java        (implements domain.port.EnvoiEmailPort)
    └── config/BrevoClientConfig.java
```

## Packages transverses (hors domaines)

```
fr.lacassinauteur.site
├── shared
│   ├── domain
│   │   ├── port
│   │   │   └── StockageFichierPort.java        (upload de fichiers, cf. ADR-0010)
│   │   └── exception
│   │       └── FichierInvalideException.java
│   ├── infrastructure
│   │   └── stockage
│   │       ├── StockageFichierLocal.java       (implémente StockageFichierPort)
│   │       └── StockageImagesProperties.java   (@ConfigurationProperties)
│   └── web
│       └── GestionnaireErreursGlobal.java      (@ControllerAdvice commun)
│
├── config
│   ├── SecurityConfig.java
│   └── WebConfig.java   (sert /media/** depuis le dossier de stockage configuré)
│
└── SiteApplication.java   (classe main Spring Boot)
```

`shared` reste réservé aux capacités réellement transverses (aucune logique métier
d'un domaine ne doit s'y échapper) — le stockage de fichiers en est un exemple
légitime : catalogue en a besoin aujourd'hui (photos, couvertures), d'autres domaines
pourront le réutiliser (ex. visuels d'actualités) sans dupliquer l'infrastructure.

## Ressources (hors code Java)

Séparation stricte public / back-office, cf.
[architecture.md §12](architecture.md#12-conventions-front-end) — **aucun** fragment ni
layout n'est partagé entre les deux arborescences.

```
src/main/resources
├── templates
│   ├── public
│   │   ├── layout
│   │   │   └── layout-public.html      (structure HTML commune, th:fragment)
│   │   ├── fragments
│   │   │   ├── header-public.html
│   │   │   ├── footer-public.html
│   │   │   └── ...                      (un fragment réutilisable = un fichier)
│   │   ├── accueil.html
│   │   ├── univers.html
│   │   ├── collection.html
│   │   ├── livre.html
│   │   ├── newsletter.html
│   │   ├── contact.html
│   │   ├── actualites.html
│   │   └── page-pro.html
│   └── backoffice
│       ├── layout
│       │   └── layout-backoffice.html
│       ├── fragments
│       │   ├── sidebar.html
│       │   ├── topbar.html
│       │   ├── icons.html               (icônes SVG inline, cf. ADR-0009)
│       │   └── ...
│       ├── connexion.html
│       └── ...                          (un template par écran de gestion)
├── static
│   ├── css
│   │   ├── public.css                  (généré par Tailwind, cf. ADR-0006)
│   │   └── backoffice.css              (généré par Tailwind, cf. ADR-0006)
│   ├── js                              (JS vanilla minimal, si besoin)
│   ├── vendor                          (librairies figées en local : htmx.min.js)
│   └── images
├── db/migration          (scripts Flyway, voir tech-stack.md)
│   └── V1__init.sql, V2__catalogue_init.sql, ...
└── application.yml, application-dev.yml, application-prod.yml
```

Les fragments propres à un seul domaine (ex. carte livre) ne vivent pas dans
`templates/public/fragments/` mais dans le `presentation.web` du domaine concerné
(cf. arborescence Java ci-dessus) — `templates/*/fragments/` est réservé aux éléments
transverses à tout l'espace (header, footer, navigation, layout).

Point d'entrée des sources Tailwind (hors `src/`, jamais packagé tel quel) :

```
frontend
├── public.css        (@import "tailwindcss"; @source "../src/.../templates/public"; @theme {...})
└── backoffice.css    (@import "tailwindcss"; @source "../src/.../templates/backoffice";)
```

## Règle de test miroir

L'arborescence de test reflète l'arborescence de prod, package par package
(`src/test/java/fr/lacassinauteur/site/catalogue/application/usecase/livre/...`), pour
qu'un test soit toujours trouvable à côté conceptuellement de la classe qu'il teste.
