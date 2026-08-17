package fr.lacassinauteur.site.identity.domain.port;

import fr.lacassinauteur.site.identity.domain.model.Email;

/** Port d'envoi des emails transactionnels du domaine identity (mot de passe oublié). */
public interface EnvoiEmailIdentityPort {

    void envoyerLienReinitialisation(Email destinataire, String lienReinitialisation);
}
