-- Présentation publique de l'auteur (page « Auteur » du brief §5), cf. ADR-0028.
--
-- Enregistrement unique : "ligne_unique" est toujours TRUE et porte une contrainte
-- d'unicité, ce qui rend physiquement impossible l'insertion d'une seconde ligne.
-- L'invariant « il n'existe qu'une biographie » est ainsi garanti par la base et
-- pas seulement par convention applicative.
CREATE TABLE biographie (
    id           UUID PRIMARY KEY,
    texte        TEXT NOT NULL,
    photo_url    VARCHAR(500),
    ligne_unique BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT biographie_ligne_unique_vraie CHECK (ligne_unique),
    CONSTRAINT biographie_une_seule_ligne UNIQUE (ligne_unique)
);
