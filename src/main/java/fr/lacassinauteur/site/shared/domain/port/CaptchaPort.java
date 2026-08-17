package fr.lacassinauteur.site.shared.domain.port;

/**
 * Vérification anti-spam par captcha (reCAPTCHA v3) sur les formulaires publics.
 * Capacité technique transverse (aucune logique métier), légitime dans
 * {@code shared} au même titre que {@link StockageFichierPort}.
 */
public interface CaptchaPort {

    /**
     * @return {@code true} si le jeton est valide et le score au moins égal au
     *         seuil configuré ; {@code false} sinon (jeton absent/invalide, score
     *         trop bas, ou API indisponible — fermé par défaut plutôt qu'ouvert).
     */
    boolean verifier(String jeton);
}
