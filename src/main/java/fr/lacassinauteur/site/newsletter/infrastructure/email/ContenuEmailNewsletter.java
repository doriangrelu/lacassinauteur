package fr.lacassinauteur.site.newsletter.infrastructure.email;

/**
 * Contenu (sujet + corps HTML minimal) des emails transactionnels de la newsletter,
 * partagé entre {@link BrevoEmailAdapter} et {@link LogEmailAdapter} pour éviter de
 * dupliquer les mêmes textes dans les deux adaptateurs.
 */
final class ContenuEmailNewsletter {

    static final String SUJET_CONFIRMATION = "Confirmez votre inscription à la newsletter";
    static final String SUJET_BIENVENUE = "Bienvenue dans la newsletter de Thierry Lacassin";

    private ContenuEmailNewsletter() {
    }

    static String confirmation(String prenom, String lienConfirmation) {
        return """
                <p>Bonjour %s,</p>
                <p>Merci de votre inscription à la newsletter de Thierry Lacassin. Pour la confirmer, \
                cliquez sur le lien ci-dessous :</p>
                <p><a href="%s">%s</a></p>
                <p>Si vous n'êtes pas à l'origine de cette inscription, vous pouvez ignorer cet email.</p>
                """.formatted(prenom, lienConfirmation, lienConfirmation);
    }

    static String bienvenue(String prenom, String lienDesinscription) {
        return """
                <p>Bonjour %s,</p>
                <p>Votre inscription à la newsletter de Thierry Lacassin est confirmée. Vous recevrez \
                désormais ses actualités (parutions, événements, dédicaces).</p>
                <p>Vous pouvez vous désinscrire à tout moment via ce lien : <a href="%s">%s</a></p>
                """.formatted(prenom, lienDesinscription, lienDesinscription);
    }
}
