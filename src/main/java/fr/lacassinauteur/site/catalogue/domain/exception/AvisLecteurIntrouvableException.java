package fr.lacassinauteur.site.catalogue.domain.exception;

import java.util.UUID;

public class AvisLecteurIntrouvableException extends RuntimeException {

    public AvisLecteurIntrouvableException(UUID id) {
        super("Avis lecteur introuvable : " + id);
    }
}
