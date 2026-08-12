package fr.lacassinauteur.site.identity.application.usecase;

import fr.lacassinauteur.site.identity.application.command.ReactiverUtilisateurCommand;
import fr.lacassinauteur.site.identity.application.result.UtilisateurResult;
import fr.lacassinauteur.site.identity.domain.model.Email;
import fr.lacassinauteur.site.identity.domain.model.MotDePasseHache;
import fr.lacassinauteur.site.identity.domain.model.Role;
import fr.lacassinauteur.site.identity.domain.model.Utilisateur;
import fr.lacassinauteur.site.identity.domain.port.FakeUtilisateurRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReactiverUtilisateurUseCaseTest {

    @Test
    void reactive_un_utilisateur_desactive() {
        FakeUtilisateurRepository utilisateurRepository = new FakeUtilisateurRepository();
        Utilisateur utilisateur = Utilisateur.creer(new Email("thierry@lacassinauteur.local"), new MotDePasseHache("hache"), Role.AUTEUR);
        utilisateur.desactiver();
        utilisateurRepository.save(utilisateur);

        UtilisateurResult result = new ReactiverUtilisateurUseCase(utilisateurRepository)
                .execute(new ReactiverUtilisateurCommand(utilisateur.id()));

        assertThat(result.actif()).isTrue();
    }
}
