package fr.lacassinauteur.site.identity.application.usecase;

import fr.lacassinauteur.site.identity.application.command.ReinitialiserMotDePasseCommand;
import fr.lacassinauteur.site.identity.domain.exception.JetonReinitialisationInvalideException;
import fr.lacassinauteur.site.identity.domain.model.Email;
import fr.lacassinauteur.site.identity.domain.model.JetonReinitialisation;
import fr.lacassinauteur.site.identity.domain.model.MotDePasseHache;
import fr.lacassinauteur.site.identity.domain.model.Role;
import fr.lacassinauteur.site.identity.domain.model.Utilisateur;
import fr.lacassinauteur.site.identity.domain.port.FakeHacheurMotDePasse;
import fr.lacassinauteur.site.identity.domain.port.FakeJetonReinitialisationMotDePassePort;
import fr.lacassinauteur.site.identity.domain.port.FakeJetonReinitialisationRepository;
import fr.lacassinauteur.site.identity.domain.port.FakeUtilisateurRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReinitialiserMotDePasseUseCaseTest {

    @Test
    void change_le_mot_de_passe_avec_un_jeton_valide_et_le_derefence() {
        FakeUtilisateurRepository utilisateurRepository = new FakeUtilisateurRepository();
        FakeJetonReinitialisationRepository jetonRepository = new FakeJetonReinitialisationRepository();
        FakeJetonReinitialisationMotDePassePort jetonPort = new FakeJetonReinitialisationMotDePassePort();
        FakeHacheurMotDePasse hacheurMotDePasse = new FakeHacheurMotDePasse();
        Utilisateur utilisateur = Utilisateur.creer(new Email("alice@example.com"), new MotDePasseHache("ancien-hache"), Role.ADMIN);
        utilisateurRepository.save(utilisateur);
        UUID jetonId = UUID.randomUUID();
        String jeton = jetonPort.genererJeton(utilisateur.id(), jetonId);
        jetonRepository.save(new JetonReinitialisation(jetonId, utilisateur.id(), jeton, Instant.now().plus(15, ChronoUnit.MINUTES)));

        new ReinitialiserMotDePasseUseCase(utilisateurRepository, jetonRepository, jetonPort, hacheurMotDePasse)
                .execute(new ReinitialiserMotDePasseCommand(jeton, "N0uveau!MotDePasse"));

        Utilisateur miseAJour = utilisateurRepository.findById(utilisateur.id()).orElseThrow();
        assertThat(miseAJour.motDePasseHache()).isEqualTo(hacheurMotDePasse.hacher("N0uveau!MotDePasse"));
        assertThat(jetonRepository.findById(jetonId)).isEmpty();
    }

    @Test
    void refuse_un_jeton_deja_utilise() {
        FakeUtilisateurRepository utilisateurRepository = new FakeUtilisateurRepository();
        FakeJetonReinitialisationRepository jetonRepository = new FakeJetonReinitialisationRepository();
        FakeJetonReinitialisationMotDePassePort jetonPort = new FakeJetonReinitialisationMotDePassePort();
        FakeHacheurMotDePasse hacheurMotDePasse = new FakeHacheurMotDePasse();
        Utilisateur utilisateur = Utilisateur.creer(new Email("alice@example.com"), new MotDePasseHache("ancien-hache"), Role.ADMIN);
        utilisateurRepository.save(utilisateur);
        UUID jetonId = UUID.randomUUID();
        String jeton = jetonPort.genererJeton(utilisateur.id(), jetonId);
        jetonRepository.save(new JetonReinitialisation(jetonId, utilisateur.id(), jeton, Instant.now().plus(15, ChronoUnit.MINUTES)));
        ReinitialiserMotDePasseUseCase useCase = new ReinitialiserMotDePasseUseCase(utilisateurRepository, jetonRepository, jetonPort, hacheurMotDePasse);
        useCase.execute(new ReinitialiserMotDePasseCommand(jeton, "N0uveau!MotDePasse"));

        assertThatThrownBy(() -> useCase.execute(new ReinitialiserMotDePasseCommand(jeton, "Autre!MotDePasse2")))
                .isInstanceOf(JetonReinitialisationInvalideException.class);
    }

    @Test
    void refuse_un_jeton_dont_lenregistrement_a_expire() {
        FakeUtilisateurRepository utilisateurRepository = new FakeUtilisateurRepository();
        FakeJetonReinitialisationRepository jetonRepository = new FakeJetonReinitialisationRepository();
        FakeJetonReinitialisationMotDePassePort jetonPort = new FakeJetonReinitialisationMotDePassePort();
        FakeHacheurMotDePasse hacheurMotDePasse = new FakeHacheurMotDePasse();
        Utilisateur utilisateur = Utilisateur.creer(new Email("alice@example.com"), new MotDePasseHache("ancien-hache"), Role.ADMIN);
        utilisateurRepository.save(utilisateur);
        UUID jetonId = UUID.randomUUID();
        String jeton = jetonPort.genererJeton(utilisateur.id(), jetonId);
        jetonRepository.save(new JetonReinitialisation(jetonId, utilisateur.id(), jeton, Instant.now().minus(1, ChronoUnit.MINUTES)));

        assertThatThrownBy(() -> new ReinitialiserMotDePasseUseCase(utilisateurRepository, jetonRepository, jetonPort, hacheurMotDePasse)
                .execute(new ReinitialiserMotDePasseCommand(jeton, "N0uveau!MotDePasse")))
                .isInstanceOf(JetonReinitialisationInvalideException.class);
    }

    @Test
    void refuse_un_jeton_inconnu() {
        FakeUtilisateurRepository utilisateurRepository = new FakeUtilisateurRepository();
        FakeJetonReinitialisationRepository jetonRepository = new FakeJetonReinitialisationRepository();
        FakeJetonReinitialisationMotDePassePort jetonPort = new FakeJetonReinitialisationMotDePassePort();
        FakeHacheurMotDePasse hacheurMotDePasse = new FakeHacheurMotDePasse();

        assertThatThrownBy(() -> new ReinitialiserMotDePasseUseCase(utilisateurRepository, jetonRepository, jetonPort, hacheurMotDePasse)
                .execute(new ReinitialiserMotDePasseCommand("jeton-inconnu", "N0uveau!MotDePasse")))
                .isInstanceOf(JetonReinitialisationInvalideException.class);
    }
}
