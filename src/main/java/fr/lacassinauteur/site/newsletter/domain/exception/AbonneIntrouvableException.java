package fr.lacassinauteur.site.newsletter.domain.exception;

import fr.lacassinauteur.site.newsletter.domain.model.Email;

import java.util.UUID;

/**
 * Levée quand un abonné est recherché par id, par jeton (confirmation ou
 * désinscription — les deux liens publics utilisent le même {@code UUID}, cf.
 * {@code AbonneNewsletter}) ou par email, sans résultat.
 */
public class AbonneIntrouvableException extends RuntimeException {

    public AbonneIntrouvableException(UUID id) {
        super("Abonné introuvable : " + id);
    }

    public AbonneIntrouvableException(Email email) {
        super("Abonné introuvable pour l'email : " + email.valeur());
    }
}
