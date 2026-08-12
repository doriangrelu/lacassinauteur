CREATE TABLE univers (
    id         UUID PRIMARY KEY,
    nom        VARCHAR(255) NOT NULL,
    sous_titre VARCHAR(255),
    texte      TEXT,
    photo_url  VARCHAR(500),
    ordre      INT NOT NULL DEFAULT 0
);

CREATE TABLE collection (
    id         UUID PRIMARY KEY,
    univers_id UUID NOT NULL REFERENCES univers (id),
    nom        VARCHAR(255) NOT NULL,
    sous_titre VARCHAR(255),
    texte      TEXT,
    ordre      INT NOT NULL DEFAULT 0
);
CREATE INDEX idx_collection_univers_id ON collection (univers_id);

CREATE TABLE livre (
    id                 UUID PRIMARY KEY,
    collection_id      UUID NOT NULL REFERENCES collection (id),
    titre              VARCHAR(255) NOT NULL,
    sous_titre         VARCHAR(255),
    couverture_url     VARCHAR(500),
    pitch_court        TEXT,
    resume             TEXT,
    lien_achat_url     VARCHAR(500),
    lien_achat_libelle VARCHAR(100),
    ordre              INT NOT NULL DEFAULT 0,
    derniere_parution  BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_livre_collection_id ON livre (collection_id);
