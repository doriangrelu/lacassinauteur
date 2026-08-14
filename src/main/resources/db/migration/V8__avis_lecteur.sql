CREATE TABLE avis_lecteur (
    id               UUID PRIMARY KEY,
    livre_id         UUID NOT NULL REFERENCES livre (id) ON DELETE CASCADE,
    nom_auteur_avis  VARCHAR(255) NOT NULL,
    texte            TEXT NOT NULL,
    note             INT,
    statut           VARCHAR(20) NOT NULL,
    date_soumission  TIMESTAMP NOT NULL
);
CREATE INDEX idx_avis_lecteur_livre_id ON avis_lecteur (livre_id);
CREATE INDEX idx_avis_lecteur_statut ON avis_lecteur (statut);
