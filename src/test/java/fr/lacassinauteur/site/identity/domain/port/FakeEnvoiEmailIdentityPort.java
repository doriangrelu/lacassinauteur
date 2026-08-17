package fr.lacassinauteur.site.identity.domain.port;

import fr.lacassinauteur.site.identity.domain.model.Email;

import java.util.ArrayList;
import java.util.List;

public class FakeEnvoiEmailIdentityPort implements EnvoiEmailIdentityPort {

    public record LienEnvoye(Email destinataire, String lien) {
    }

    public final List<LienEnvoye> liensEnvoyes = new ArrayList<>();

    @Override
    public void envoyerLienReinitialisation(Email destinataire, String lienReinitialisation) {
        liensEnvoyes.add(new LienEnvoye(destinataire, lienReinitialisation));
    }
}
