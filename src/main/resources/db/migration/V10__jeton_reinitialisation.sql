CREATE TABLE jeton_reinitialisation (
    id                UUID PRIMARY KEY,
    utilisateur_id    UUID NOT NULL REFERENCES utilisateur(id) ON DELETE CASCADE,
    jeton             VARCHAR(1000) NOT NULL,
    date_expiration   TIMESTAMP NOT NULL
);

CREATE INDEX idx_jeton_reinitialisation_utilisateur ON jeton_reinitialisation(utilisateur_id);
