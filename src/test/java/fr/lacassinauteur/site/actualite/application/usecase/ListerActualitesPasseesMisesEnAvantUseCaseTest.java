package fr.lacassinauteur.site.actualite.application.usecase;

import fr.lacassinauteur.site.actualite.application.result.ActualiteResult;
import fr.lacassinauteur.site.actualite.domain.model.Actualite;
import fr.lacassinauteur.site.actualite.domain.port.FakeActualiteRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ListerActualitesPasseesMisesEnAvantUseCaseTest {

    @Test
    void seules_les_actualites_passees_et_mises_en_avant_sont_listees() {
        FakeActualiteRepository actualiteRepository = new FakeActualiteRepository();
        Actualite misEnAvant = Actualite.creer("Passée mise en avant", "Texte", LocalDate.now().minusDays(10), "Paris", null, null, false, true);
        Actualite nonMisEnAvant = Actualite.creer("Passée discrète", "Texte", LocalDate.now().minusDays(5), "Lyon", null, null, false, false);
        Actualite aVenir = Actualite.creer("À venir", "Texte", LocalDate.now().plusDays(5), "Nice", null, null, false, true);
        actualiteRepository.save(misEnAvant);
        actualiteRepository.save(nonMisEnAvant);
        actualiteRepository.save(aVenir);

        List<ActualiteResult> resultats = new ListerActualitesPasseesMisesEnAvantUseCase(actualiteRepository).execute();

        assertThat(resultats).extracting(ActualiteResult::titre).containsExactly("Passée mise en avant");
    }
}
