CREATE TABLE utilisateur (
    id                UUID PRIMARY KEY,
    email             VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe_hache VARCHAR(255) NOT NULL,
    role              VARCHAR(20) NOT NULL,
    actif             BOOLEAN NOT NULL DEFAULT TRUE
);
