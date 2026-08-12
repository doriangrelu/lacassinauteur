package fr.lacassinauteur.site.identity.domain.exception;

import java.util.UUID;

public class UtilisateurIntrouvableException extends RuntimeException {

    public UtilisateurIntrouvableException(UUID id) {
        super("Utilisateur introuvable : " + id);
    }
}
