package fr.lacassinauteur.site.identity.application.usecase;

import fr.lacassinauteur.site.identity.application.command.DemanderReinitialisationMotDePasseCommand;
import fr.lacassinauteur.site.identity.domain.model.Email;
import fr.lacassinauteur.site.identity.domain.model.JetonReinitialisation;
import fr.lacassinauteur.site.identity.domain.model.MotDePasseHache;
import fr.lacassinauteur.site.identity.domain.model.Role;
import fr.lacassinauteur.site.identity.domain.model.Utilisateur;
import fr.lacassinauteur.site.identity.domain.port.EnvoiEmailIdentityPort;
import fr.lacassinauteur.site.identity.domain.port.FakeEnvoiEmailIdentityPort;
import fr.lacassinauteur.site.identity.domain.port.FakeJetonReinitialisationMotDePassePort;
import fr.lacassinauteur.site.identity.domain.port.FakeJetonReinitialisationRepository;
import fr.lacassinauteur.site.identity.domain.port.FakeUtilisateurRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class DemanderReinitialisationMotDePasseUseCaseTest {

    private static final String URL_BASE = "http://localhost:8080";
    private static final long DUREE_MINUTES = 15;

    @Test
    void envoie_un_lien_si_le_compte_existe_et_est_actif() {
        FakeUtilisateurRepository utilisateurRepository = new FakeUtilisateurRepository();
        FakeJetonReinitialisationRepository jetonRepository = new FakeJetonReinitialisationRepository();
        FakeJetonReinitialisationMotDePassePort jetonPort = new FakeJetonReinitialisationMotDePassePort();
        FakeEnvoiEmailIdentityPort envoiEmailIdentityPort = new FakeEnvoiEmailIdentityPort();
        Utilisateur utilisateur = Utilisateur.creer(new Email("alice@example.com"), new MotDePasseHache("hache"), Role.ADMIN);
        utilisateurRepository.save(utilisateur);

        new DemanderReinitialisationMotDePasseUseCase(
                utilisateurRepository, jetonRepository, jetonPort, envoiEmailIdentityPort, URL_BASE, DUREE_MINUTES)
                .execute(new DemanderReinitialisationMotDePasseCommand("alice@example.com"));

        assertThat(envoiEmailIdentityPort.liensEnvoyes).hasSize(1);
        assertThat(envoiEmailIdentityPort.liensEnvoyes.getFirst().lien())
                .startsWith(URL_BASE + "/backoffice/reinitialiser-mot-de-passe?jeton=");
        assertThat(jetonRepository.findValidePourUtilisateur(utilisateur.id())).isPresent();
    }

    @Test
    void reutilise_le_jeton_existant_si_un_jeton_valide_est_deja_present() {
        FakeUtilisateurRepository utilisateurRepository = new FakeUtilisateurRepository();
        FakeJetonReinitialisationRepository jetonRepository = new FakeJetonReinitialisationRepository();
        FakeJetonReinitialisationMotDePassePort jetonPort = new FakeJetonReinitialisationMotDePassePort();
        FakeEnvoiEmailIdentityPort envoiEmailIdentityPort = new FakeEnvoiEmailIdentityPort();
        Utilisateur utilisateur = Utilisateur.creer(new Email("alice@example.com"), new MotDePasseHache("hache"), Role.ADMIN);
        utilisateurRepository.save(utilisateur);
        DemanderReinitialisationMotDePasseUseCase useCase = new DemanderReinitialisationMotDePasseUseCase(
                utilisateurRepository, jetonRepository, jetonPort, envoiEmailIdentityPort, URL_BASE, DUREE_MINUTES);

        useCase.execute(new DemanderReinitialisationMotDePasseCommand("alice@example.com"));
        useCase.execute(new DemanderReinitialisationMotDePasseCommand("alice@example.com"));

        assertThat(envoiEmailIdentityPort.liensEnvoyes).hasSize(2);
        assertThat(envoiEmailIdentityPort.liensEnvoyes.get(0).lien())
                .isEqualTo(envoiEmailIdentityPort.liensEnvoyes.get(1).lien());
    }

    @Test
    void genere_un_nouveau_jeton_si_lancien_a_expire() {
        FakeUtilisateurRepository utilisateurRepository = new FakeUtilisateurRepository();
        FakeJetonReinitialisationRepository jetonRepository = new FakeJetonReinitialisationRepository();
        FakeJetonReinitialisationMotDePassePort jetonPort = new FakeJetonReinitialisationMotDePassePort();
        FakeEnvoiEmailIdentityPort envoiEmailIdentityPort = new FakeEnvoiEmailIdentityPort();
        Utilisateur utilisateur = Utilisateur.creer(new Email("alice@example.com"), new MotDePasseHache("hache"), Role.ADMIN);
        utilisateurRepository.save(utilisateur);
        jetonRepository.save(new JetonReinitialisation(
                java.util.UUID.randomUUID(), utilisateur.id(), "jeton-expire", Instant.now().minus(1, ChronoUnit.MINUTES)));

        new DemanderReinitialisationMotDePasseUseCase(
                utilisateurRepository, jetonRepository, jetonPort, envoiEmailIdentityPort, URL_BASE, DUREE_MINUTES)
                .execute(new DemanderReinitialisationMotDePasseCommand("alice@example.com"));

        assertThat(envoiEmailIdentityPort.liensEnvoyes).hasSize(1);
        assertThat(envoiEmailIdentityPort.liensEnvoyes.getFirst().lien()).doesNotContain("jeton-expire");
    }

    @Test
    void nenvoie_rien_si_le_compte_nexiste_pas() {
        FakeUtilisateurRepository utilisateurRepository = new FakeUtilisateurRepository();
        FakeJetonReinitialisationRepository jetonRepository = new FakeJetonReinitialisationRepository();
        FakeJetonReinitialisationMotDePassePort jetonPort = new FakeJetonReinitialisationMotDePassePort();
        FakeEnvoiEmailIdentityPort envoiEmailIdentityPort = new FakeEnvoiEmailIdentityPort();

        new DemanderReinitialisationMotDePasseUseCase(
                utilisateurRepository, jetonRepository, jetonPort, envoiEmailIdentityPort, URL_BASE, DUREE_MINUTES)
                .execute(new DemanderReinitialisationMotDePasseCommand("inconnu@example.com"));

        assertThat(envoiEmailIdentityPort.liensEnvoyes).isEmpty();
    }

    @Test
    void nenvoie_rien_si_le_compte_est_desactive() {
        FakeUtilisateurRepository utilisateurRepository = new FakeUtilisateurRepository();
        FakeJetonReinitialisationRepository jetonRepository = new FakeJetonReinitialisationRepository();
        FakeJetonReinitialisationMotDePassePort jetonPort = new FakeJetonReinitialisationMotDePassePort();
        FakeEnvoiEmailIdentityPort envoiEmailIdentityPort = new FakeEnvoiEmailIdentityPort();
        Utilisateur utilisateur = Utilisateur.creer(new Email("bob@example.com"), new MotDePasseHache("hache"), Role.ADMIN);
        utilisateur.desactiver();
        utilisateurRepository.save(utilisateur);

        new DemanderReinitialisationMotDePasseUseCase(
                utilisateurRepository, jetonRepository, jetonPort, envoiEmailIdentityPort, URL_BASE, DUREE_MINUTES)
                .execute(new DemanderReinitialisationMotDePasseCommand("bob@example.com"));

        assertThat(envoiEmailIdentityPort.liensEnvoyes).isEmpty();
    }

    @Test
    void un_echec_denvoi_email_ne_fait_pas_echouer_la_demande() {
        FakeUtilisateurRepository utilisateurRepository = new FakeUtilisateurRepository();
        FakeJetonReinitialisationRepository jetonRepository = new FakeJetonReinitialisationRepository();
        FakeJetonReinitialisationMotDePassePort jetonPort = new FakeJetonReinitialisationMotDePassePort();
        Utilisateur utilisateur = Utilisateur.creer(new Email("alice@example.com"), new MotDePasseHache("hache"), Role.ADMIN);
        utilisateurRepository.save(utilisateur);
        EnvoiEmailIdentityPort envoiEnPanne = (destinataire, lien) -> {
            throw new RuntimeException("ESP indisponible");
        };

        new DemanderReinitialisationMotDePasseUseCase(
                utilisateurRepository, jetonRepository, jetonPort, envoiEnPanne, URL_BASE, DUREE_MINUTES)
                .execute(new DemanderReinitialisationMotDePasseCommand("alice@example.com"));
        // Aucune exception ne doit remonter.
    }
}
