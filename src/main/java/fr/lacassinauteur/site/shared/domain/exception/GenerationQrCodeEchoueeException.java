package fr.lacassinauteur.site.shared.domain.exception;

public class GenerationQrCodeEchoueeException extends RuntimeException {

    public GenerationQrCodeEchoueeException(String contenu, Throwable cause) {
        super("Impossible de générer un QR code pour : " + contenu, cause);
    }
}
