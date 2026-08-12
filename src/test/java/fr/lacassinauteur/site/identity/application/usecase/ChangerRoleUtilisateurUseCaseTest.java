package fr.lacassinauteur.site.identity.application.usecase;

import fr.lacassinauteur.site.identity.application.command.ChangerRoleCommand;
import fr.lacassinauteur.site.identity.application.result.UtilisateurResult;
import fr.lacassinauteur.site.identity.domain.exception.UtilisateurIntrouvableException;
import fr.lacassinauteur.site.identity.domain.model.Email;
import fr.lacassinauteur.site.identity.domain.model.MotDePasseHache;
import fr.lacassinauteur.site.identity.domain.model.Role;
import fr.lacassinauteur.site.identity.domain.model.Utilisateur;
import fr.lacassinauteur.site.identity.domain.port.FakeUtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChangerRoleUtilisateurUseCaseTest {

    private FakeUtilisateurRepository utilisateurRepository;
    private ChangerRoleUtilisateurUseCase useCase;

    @BeforeEach
    void setUp() {
        utilisateurRepository = new FakeUtilisateurRepository();
        useCase = new ChangerRoleUtilisateurUseCase(utilisateurRepository);
    }

    @Test
    void change_le_role_dun_utilisateur_existant() {
        Utilisateur utilisateur = Utilisateur.creer(new Email("thierry@lacassinauteur.local"), new MotDePasseHache("hache"), Role.AUTEUR);
        utilisateurRepository.save(utilisateur);

        UtilisateurResult result = useCase.execute(new ChangerRoleCommand(utilisateur.id(), Role.ADMIN));

        assertThat(result.role()).isEqualTo(Role.ADMIN);
    }

    @Test
    void leve_une_exception_si_lutilisateur_est_introuvable() {
        assertThatThrownBy(() -> useCase.execute(new ChangerRoleCommand(UUID.randomUUID(), Role.ADMIN)))
                .isInstanceOf(UtilisateurIntrouvableException.class);
    }
}
