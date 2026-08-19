-- Variables injectées dans les pages légales publiques (mentions légales et
-- politique de confidentialité), cf. ADR-0029. Le texte légal lui-même vit dans
-- les gabarits : seules ces valeurs sont éditables en back-office.
--
-- Enregistrement unique garanti par la base, même mécanisme que la table
-- biographie (V11) : "ligne_unique" toujours TRUE et porteur d'une contrainte
-- d'unicité, ce qui rend une seconde ligne physiquement impossible.
CREATE TABLE informations_legales (
    id                           UUID PRIMARY KEY,
    editeur_nom                  VARCHAR(255),
    editeur_statut               VARCHAR(255),
    editeur_adresse              VARCHAR(500),
    editeur_email                VARCHAR(255),
    directeur_publication        VARCHAR(255),
    hebergeur_nom                VARCHAR(255),
    hebergeur_adresse            VARCHAR(500),
    conservation_newsletter_mois INTEGER NOT NULL DEFAULT 36,
    conservation_contact_mois    INTEGER NOT NULL DEFAULT 12,
    ligne_unique                 BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT informations_legales_ligne_unique_vraie CHECK (ligne_unique),
    CONSTRAINT informations_legales_une_seule_ligne UNIQUE (ligne_unique)
);
