package fr.lacassinauteur.site.identity.infrastructure.email;

/**
 * Contenu (sujet + corps HTML minimal) des emails transactionnels du domaine
 * identity, partagé entre les adaptateurs Brevo et log pour éviter de dupliquer
 * les mêmes textes.
 */
final class ContenuEmailIdentity {

    static final String SUJET_REINITIALISATION = "Réinitialisation de votre mot de passe — Back-office Thierry Lacassin";

    private ContenuEmailIdentity() {
    }

    static String reinitialisation(String lienReinitialisation) {
        return """
                <p>Bonjour,</p>
                <p>Une demande de réinitialisation de mot de passe a été effectuée pour votre compte du \
                back-office Thierry Lacassin. Cliquez sur le lien ci-dessous pour choisir un nouveau mot de \
                passe :</p>
                <p><a href="%s">%s</a></p>
                <p>Ce lien est valable 15 minutes. Si vous n'êtes pas à l'origine de cette demande, vous \
                pouvez ignorer cet email — votre mot de passe actuel reste inchangé.</p>
                """.formatted(lienReinitialisation, lienReinitialisation);
    }
}
