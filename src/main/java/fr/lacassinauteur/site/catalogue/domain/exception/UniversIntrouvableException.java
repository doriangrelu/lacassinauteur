package fr.lacassinauteur.site.catalogue.domain.exception;

import java.util.UUID;

public class UniversIntrouvableException extends RuntimeException {

    public UniversIntrouvableException(UUID id) {
        super("Univers introuvable : " + id);
    }
}
