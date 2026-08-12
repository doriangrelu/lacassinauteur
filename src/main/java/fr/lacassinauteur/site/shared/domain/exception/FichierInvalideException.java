package fr.lacassinauteur.site.shared.domain.exception;

public class FichierInvalideException extends RuntimeException {

    public FichierInvalideException(String message) {
        super(message);
    }
}
