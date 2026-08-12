CREATE TABLE actualite (
    id                     UUID PRIMARY KEY,
    titre                  VARCHAR(255) NOT NULL,
    texte                  TEXT,
    date                   DATE NOT NULL,
    lieu                   VARCHAR(255),
    lien_billetterie       VARCHAR(500),
    image_url              VARCHAR(500),
    archivee_manuellement  BOOLEAN NOT NULL DEFAULT FALSE,
    mis_en_avant           BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_actualite_date ON actualite (date);
