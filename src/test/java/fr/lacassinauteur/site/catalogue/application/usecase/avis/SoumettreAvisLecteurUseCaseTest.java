package fr.lacassinauteur.site.catalogue.application.usecase.avis;

import fr.lacassinauteur.site.catalogue.application.command.SoumettreAvisLecteurCommand;
import fr.lacassinauteur.site.catalogue.application.result.AvisLecteurResult;
import fr.lacassinauteur.site.catalogue.domain.exception.LivreIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.model.Livre;
import fr.lacassinauteur.site.catalogue.domain.model.StatutAvis;
import fr.lacassinauteur.site.catalogue.domain.port.FakeAvisLecteurRepository;
import fr.lacassinauteur.site.catalogue.domain.port.FakeLivreRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SoumettreAvisLecteurUseCaseTest {

    @Test
    void soumettre_enregistre_lavis_en_attente() {
        FakeLivreRepository livreRepository = new FakeLivreRepository();
        Livre livre = Livre.creer("la-rame-et-la-rage", UUID.randomUUID(), "La rame et la rage", null, "/cover.png", "Pitch", "Résumé", 1);
        livreRepository.save(livre);

        FakeAvisLecteurRepository avisLecteurRepository = new FakeAvisLecteurRepository();
        SoumettreAvisLecteurUseCase useCase = new SoumettreAvisLecteurUseCase(avisLecteurRepository, livreRepository);

        AvisLecteurResult result = useCase.execute(new SoumettreAvisLecteurCommand(livre.id(), "Camille", "Superbe roman.", 5));

        assertThat(result.statut()).isEqualTo(StatutAvis.EN_ATTENTE);
        assertThat(result.nomAuteurAvis()).isEqualTo("Camille");
        assertThat(result.note()).isEqualTo(5);
        assertThat(avisLecteurRepository.findById(result.id())).isPresent();
    }

    @Test
    void soumettre_leve_une_exception_si_le_livre_nexiste_pas() {
        FakeLivreRepository livreRepository = new FakeLivreRepository();
        FakeAvisLecteurRepository avisLecteurRepository = new FakeAvisLecteurRepository();
        SoumettreAvisLecteurUseCase useCase = new SoumettreAvisLecteurUseCase(avisLecteurRepository, livreRepository);

        UUID livreIdInconnu = UUID.randomUUID();

        assertThatThrownBy(() -> useCase.execute(new SoumettreAvisLecteurCommand(livreIdInconnu, "Camille", "Texte", null)))
                .isInstanceOf(LivreIntrouvableException.class);
    }
}
