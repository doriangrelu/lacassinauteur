CREATE TABLE message_contact (
    id             UUID PRIMARY KEY,
    nom            VARCHAR(255) NOT NULL,
    email          VARCHAR(255) NOT NULL,
    objet          VARCHAR(255) NOT NULL,
    message        TEXT NOT NULL,
    date_reception TIMESTAMP NOT NULL,
    statut         VARCHAR(20) NOT NULL
);
CREATE INDEX idx_message_contact_date_reception ON message_contact (date_reception);
