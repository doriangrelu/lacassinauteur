ALTER TABLE univers
    ADD COLUMN photo_complementaire_url VARCHAR(500),
    ADD COLUMN photo_complementaire_legende TEXT;

ALTER TABLE actualite
    ADD COLUMN photo_complementaire_url VARCHAR(500),
    ADD COLUMN photo_complementaire_legende TEXT;
