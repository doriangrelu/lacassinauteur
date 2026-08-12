package fr.lacassinauteur.site.catalogue.domain.exception;

import java.util.UUID;

public class LivreIntrouvableException extends RuntimeException {

    public LivreIntrouvableException(UUID id) {
        super("Livre introuvable : " + id);
    }

    public LivreIntrouvableException(String slug) {
        super("Livre introuvable : " + slug);
    }
}
