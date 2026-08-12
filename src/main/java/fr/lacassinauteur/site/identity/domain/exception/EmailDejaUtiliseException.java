package fr.lacassinauteur.site.identity.domain.exception;

import fr.lacassinauteur.site.identity.domain.model.Email;

public class EmailDejaUtiliseException extends RuntimeException {

    public EmailDejaUtiliseException(Email email) {
        super("Un compte existe déjà avec l'email : " + email.valeur());
    }
}
