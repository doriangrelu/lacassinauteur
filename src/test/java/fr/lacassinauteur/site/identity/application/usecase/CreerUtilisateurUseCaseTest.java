package fr.lacassinauteur.site.identity.application.usecase;

import fr.lacassinauteur.site.identity.application.command.CreerUtilisateurCommand;
import fr.lacassinauteur.site.identity.application.result.UtilisateurResult;
import fr.lacassinauteur.site.identity.domain.exception.EmailDejaUtiliseException;
import fr.lacassinauteur.site.identity.domain.model.Role;
import fr.lacassinauteur.site.identity.domain.port.FakeHacheurMotDePasse;
import fr.lacassinauteur.site.identity.domain.port.FakeUtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreerUtilisateurUseCaseTest {

    private FakeUtilisateurRepository utilisateurRepository;
    private CreerUtilisateurUseCase useCase;

    @BeforeEach
    void setUp() {
        utilisateurRepository = new FakeUtilisateurRepository();
        useCase = new CreerUtilisateurUseCase(utilisateurRepository, new FakeHacheurMotDePasse());
    }

    @Test
    void cree_un_utilisateur_avec_le_role_demande() {
        UtilisateurResult result = useCase.execute(new CreerUtilisateurCommand("thierry@lacassinauteur.local", "motdepasse123", Role.AUTEUR));

        assertThat(result.email()).isEqualTo("thierry@lacassinauteur.local");
        assertThat(result.role()).isEqualTo(Role.AUTEUR);
        assertThat(result.actif()).isTrue();
        assertThat(utilisateurRepository.findAll()).hasSize(1);
    }

    @Test
    void refuse_de_creer_un_utilisateur_avec_un_email_deja_utilise() {
        useCase.execute(new CreerUtilisateurCommand("thierry@lacassinauteur.local", "motdepasse123", Role.AUTEUR));

        assertThatThrownBy(() -> useCase.execute(new CreerUtilisateurCommand("thierry@lacassinauteur.local", "autrepasse", Role.ADMIN)))
                .isInstanceOf(EmailDejaUtiliseException.class);

        assertThat(utilisateurRepository.findAll()).hasSize(1);
    }
}
