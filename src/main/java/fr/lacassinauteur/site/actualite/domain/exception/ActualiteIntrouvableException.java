package fr.lacassinauteur.site.actualite.domain.exception;

import java.util.UUID;

public class ActualiteIntrouvableException extends RuntimeException {

    public ActualiteIntrouvableException(UUID id) {
        super("Actualité introuvable : " + id);
    }
}
