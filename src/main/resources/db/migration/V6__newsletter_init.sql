CREATE TABLE abonne_newsletter (
    id                 UUID PRIMARY KEY,
    prenom             VARCHAR(255) NOT NULL,
    email              VARCHAR(255) NOT NULL UNIQUE,
    statut             VARCHAR(30) NOT NULL,
    date_inscription   TIMESTAMP NOT NULL,
    date_confirmation  TIMESTAMP,
    jeton_confirmation UUID NOT NULL UNIQUE
);
