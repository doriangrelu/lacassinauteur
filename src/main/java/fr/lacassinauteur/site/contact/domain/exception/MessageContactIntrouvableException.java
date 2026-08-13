package fr.lacassinauteur.site.contact.domain.exception;

import java.util.UUID;

public class MessageContactIntrouvableException extends RuntimeException {

    public MessageContactIntrouvableException(UUID id) {
        super("Message de contact introuvable : " + id);
    }
}
