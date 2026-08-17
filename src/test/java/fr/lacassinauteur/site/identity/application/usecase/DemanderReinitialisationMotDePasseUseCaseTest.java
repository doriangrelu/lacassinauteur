package fr.lacassinauteur.site.identity.application.usecase;

import fr.lacassinauteur.site.identity.application.command.DemanderReinitialisationMotDePasseCommand;
import fr.lacassinauteur.site.identity.domain.model.Email;
import fr.lacassinauteur.site.identity.domain.model.MotDePasseHache;
import fr.lacassinauteur.site.identity.domain.model.Role;
import fr.lacassinauteur.site.identity.domain.model.Utilisateur;
import fr.lacassinauteur.site.identity.domain.port.FakeEnvoiEmailIdentityPort;
import fr.lacassinauteur.site.identity.domain.port.FakeJetonReinitialisationMotDePassePort;
import fr.lacassinauteur.site.identity.domain.port.FakeUtilisateurRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DemanderReinitialisationMotDePasseUseCaseTest {

    private static final String URL_BASE = "http://localhost:8080";

    @Test
    void envoie_un_lien_si_le_compte_existe_et_est_actif() {
        FakeUtilisateurRepository utilisateurRepository = new FakeUtilisateurRepository();
        FakeJetonReinitialisationMotDePassePort jetonPort = new FakeJetonReinitialisationMotDePassePort();
        FakeEnvoiEmailIdentityPort envoiEmailIdentityPort = new FakeEnvoiEmailIdentityPort();
        Utilisateur utilisateur = Utilisateur.creer(new Email("alice@example.com"), new MotDePasseHache("hache"), Role.ADMIN);
        utilisateurRepository.save(utilisateur);

        new DemanderReinitialisationMotDePasseUseCase(utilisateurRepository, jetonPort, envoiEmailIdentityPort, URL_BASE)
                .execute(new DemanderReinitialisationMotDePasseCommand("alice@example.com"));

        assertThat(envoiEmailIdentityPort.liensEnvoyes).hasSize(1);
        assertThat(envoiEmailIdentityPort.liensEnvoyes.getFirst().lien())
                .startsWith(URL_BASE + "/backoffice/reinitialiser-mot-de-passe?jeton=");
    }

    @Test
    void nenvoie_rien_si_le_compte_nexiste_pas() {
        FakeUtilisateurRepository utilisateurRepository = new FakeUtilisateurRepository();
        FakeJetonReinitialisationMotDePassePort jetonPort = new FakeJetonReinitialisationMotDePassePort();
        FakeEnvoiEmailIdentityPort envoiEmailIdentityPort = new FakeEnvoiEmailIdentityPort();

        new DemanderReinitialisationMotDePasseUseCase(utilisateurRepository, jetonPort, envoiEmailIdentityPort, URL_BASE)
                .execute(new DemanderReinitialisationMotDePasseCommand("inconnu@example.com"));

        assertThat(envoiEmailIdentityPort.liensEnvoyes).isEmpty();
    }

    @Test
    void nenvoie_rien_si_le_compte_est_desactive() {
        FakeUtilisateurRepository utilisateurRepository = new FakeUtilisateurRepository();
        FakeJetonReinitialisationMotDePassePort jetonPort = new FakeJetonReinitialisationMotDePassePort();
        FakeEnvoiEmailIdentityPort envoiEmailIdentityPort = new FakeEnvoiEmailIdentityPort();
        Utilisateur utilisateur = Utilisateur.creer(new Email("bob@example.com"), new MotDePasseHache("hache"), Role.ADMIN);
        utilisateur.desactiver();
        utilisateurRepository.save(utilisateur);

        new DemanderReinitialisationMotDePasseUseCase(utilisateurRepository, jetonPort, envoiEmailIdentityPort, URL_BASE)
                .execute(new DemanderReinitialisationMotDePasseCommand("bob@example.com"));

        assertThat(envoiEmailIdentityPort.liensEnvoyes).isEmpty();
    }

    @Test
    void un_echec_denvoi_email_ne_fait_pas_echouer_la_demande() {
        FakeUtilisateurRepository utilisateurRepository = new FakeUtilisateurRepository();
        FakeJetonReinitialisationMotDePassePort jetonPort = new FakeJetonReinitialisationMotDePassePort();
        Utilisateur utilisateur = Utilisateur.creer(new Email("alice@example.com"), new MotDePasseHache("hache"), Role.ADMIN);
        utilisateurRepository.save(utilisateur);
        fr.lacassinauteur.site.identity.domain.port.EnvoiEmailIdentityPort envoiEnPanne = (destinataire, lien) -> {
            throw new RuntimeException("ESP indisponible");
        };

        new DemanderReinitialisationMotDePasseUseCase(utilisateurRepository, jetonPort, envoiEnPanne, URL_BASE)
                .execute(new DemanderReinitialisationMotDePasseCommand("alice@example.com"));
        // Aucune exception ne doit remonter.
    }
}
