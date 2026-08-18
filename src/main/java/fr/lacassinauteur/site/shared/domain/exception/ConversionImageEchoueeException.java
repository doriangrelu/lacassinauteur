package fr.lacassinauteur.site.shared.domain.exception;

public class ConversionImageEchoueeException extends RuntimeException {

    public ConversionImageEchoueeException(String message) {
        super(message);
    }

    public ConversionImageEchoueeException(String message, Throwable cause) {
        super(message, cause);
    }
}
