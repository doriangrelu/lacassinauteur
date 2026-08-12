package fr.lacassinauteur.site.newsletter.domain.port;

import fr.lacassinauteur.site.newsletter.domain.model.Email;

import java.util.ArrayList;
import java.util.List;

public class FakeEnvoiEmailPort implements EnvoiEmailPort {

    public record EnvoiConfirmation(Email destinataire, String prenom, String lienConfirmation) {
    }

    public record EnvoiBienvenue(Email destinataire, String prenom, String lienDesinscription) {
    }

    public final List<EnvoiConfirmation> confirmationsEnvoyees = new ArrayList<>();
    public final List<EnvoiBienvenue> bienvenuesEnvoyees = new ArrayList<>();

    @Override
    public void envoyerEmailConfirmation(Email destinataire, String prenom, String lienConfirmation) {
        confirmationsEnvoyees.add(new EnvoiConfirmation(destinataire, prenom, lienConfirmation));
    }

    @Override
    public void envoyerEmailBienvenue(Email destinataire, String prenom, String lienDesinscription) {
        bienvenuesEnvoyees.add(new EnvoiBienvenue(destinataire, prenom, lienDesinscription));
    }
}
