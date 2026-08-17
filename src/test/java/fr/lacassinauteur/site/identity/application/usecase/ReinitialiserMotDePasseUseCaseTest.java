package fr.lacassinauteur.site.identity.application.usecase;

import fr.lacassinauteur.site.identity.application.command.ReinitialiserMotDePasseCommand;
import fr.lacassinauteur.site.identity.domain.exception.JetonReinitialisationInvalideException;
import fr.lacassinauteur.site.identity.domain.model.Email;
import fr.lacassinauteur.site.identity.domain.model.MotDePasseHache;
import fr.lacassinauteur.site.identity.domain.model.Role;
import fr.lacassinauteur.site.identity.domain.model.Utilisateur;
import fr.lacassinauteur.site.identity.domain.port.FakeHacheurMotDePasse;
import fr.lacassinauteur.site.identity.domain.port.FakeJetonReinitialisationMotDePassePort;
import fr.lacassinauteur.site.identity.domain.port.FakeUtilisateurRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReinitialiserMotDePasseUseCaseTest {

    @Test
    void change_le_mot_de_passe_avec_un_jeton_valide() {
        FakeUtilisateurRepository utilisateurRepository = new FakeUtilisateurRepository();
        FakeJetonReinitialisationMotDePassePort jetonPort = new FakeJetonReinitialisationMotDePassePort();
        FakeHacheurMotDePasse hacheurMotDePasse = new FakeHacheurMotDePasse();
        Utilisateur utilisateur = Utilisateur.creer(new Email("alice@example.com"), new MotDePasseHache("ancien-hache"), Role.ADMIN);
        utilisateurRepository.save(utilisateur);
        String jeton = jetonPort.genererJeton(utilisateur.id());

        new ReinitialiserMotDePasseUseCase(utilisateurRepository, jetonPort, hacheurMotDePasse)
                .execute(new ReinitialiserMotDePasseCommand(jeton, "N0uveau!MotDePasse"));

        Utilisateur miseAJour = utilisateurRepository.findById(utilisateur.id()).orElseThrow();
        assertThat(miseAJour.motDePasseHache()).isEqualTo(hacheurMotDePasse.hacher("N0uveau!MotDePasse"));
    }

    @Test
    void refuse_un_jeton_invalide() {
        FakeUtilisateurRepository utilisateurRepository = new FakeUtilisateurRepository();
        FakeJetonReinitialisationMotDePassePort jetonPort = new FakeJetonReinitialisationMotDePassePort();
        FakeHacheurMotDePasse hacheurMotDePasse = new FakeHacheurMotDePasse();

        assertThatThrownBy(() -> new ReinitialiserMotDePasseUseCase(utilisateurRepository, jetonPort, hacheurMotDePasse)
                .execute(new ReinitialiserMotDePasseCommand("jeton-inconnu", "N0uveau!MotDePasse")))
                .isInstanceOf(JetonReinitialisationInvalideException.class);
    }
}
