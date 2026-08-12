package fr.lacassinauteur.site.catalogue.domain.exception;

import java.util.UUID;

public class CollectionIntrouvableException extends RuntimeException {

    public CollectionIntrouvableException(UUID id) {
        super("Collection introuvable : " + id);
    }

    public CollectionIntrouvableException(String slug) {
        super("Collection introuvable : " + slug);
    }
}
