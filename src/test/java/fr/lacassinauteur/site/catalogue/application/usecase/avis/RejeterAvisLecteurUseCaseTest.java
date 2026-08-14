package fr.lacassinauteur.site.catalogue.application.usecase.avis;

import fr.lacassinauteur.site.catalogue.application.result.AvisLecteurResult;
import fr.lacassinauteur.site.catalogue.domain.exception.AvisLecteurIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.model.AvisLecteur;
import fr.lacassinauteur.site.catalogue.domain.model.StatutAvis;
import fr.lacassinauteur.site.catalogue.domain.port.FakeAvisLecteurRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RejeterAvisLecteurUseCaseTest {

    @Test
    void rejeter_passe_lavis_au_statut_rejete() {
        FakeAvisLecteurRepository avisLecteurRepository = new FakeAvisLecteurRepository();
        AvisLecteur avis = AvisLecteur.soumettre(UUID.randomUUID(), "Camille", "Avis limite.", null);
        avisLecteurRepository.save(avis);

        AvisLecteurResult result = new RejeterAvisLecteurUseCase(avisLecteurRepository).execute(avis.id());

        assertThat(result.statut()).isEqualTo(StatutAvis.REJETE);
    }

    @Test
    void rejeter_leve_une_exception_si_lavis_nexiste_pas() {
        FakeAvisLecteurRepository avisLecteurRepository = new FakeAvisLecteurRepository();
        RejeterAvisLecteurUseCase useCase = new RejeterAvisLecteurUseCase(avisLecteurRepository);

        UUID idInconnu = UUID.randomUUID();

        assertThatThrownBy(() -> useCase.execute(idInconnu)).isInstanceOf(AvisLecteurIntrouvableException.class);
    }
}
