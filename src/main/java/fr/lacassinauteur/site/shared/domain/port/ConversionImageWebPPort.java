package fr.lacassinauteur.site.shared.domain.port;

public interface ConversionImageWebPPort {

    /**
     * Convertit une image (jpg/png...) en WebP. Lève
     * {@link fr.lacassinauteur.site.shared.domain.exception.ConversionImageEchoueeException}
     * si la conversion échoue (binaire absent, image invalide, délai dépassé...) —
     * charge à l'appelant de décider de conserver le format d'origine plutôt que
     * d'échouer, la conversion étant une optimisation, pas une garantie.
     */
    byte[] convertirEnWebp(byte[] contenuOriginal);
}
