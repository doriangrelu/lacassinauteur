package fr.lacassinauteur.site.actualite.application.usecase;

import fr.lacassinauteur.site.actualite.application.result.ActualiteResult;
import fr.lacassinauteur.site.actualite.domain.model.Actualite;
import fr.lacassinauteur.site.actualite.domain.port.FakeActualiteRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ListerEvenementsAVenirUseCaseTest {

    @Test
    void seuls_les_evenements_a_venir_sont_lites_tries_du_plus_proche_au_plus_lointain() {
        FakeActualiteRepository actualiteRepository = new FakeActualiteRepository();
        Actualite lointain = Actualite.creer("Salon lointain", "Texte", LocalDate.now().plusMonths(3), "Paris", null, null, false, false);
        Actualite proche = Actualite.creer("Salon proche", "Texte", LocalDate.now().plusDays(5), "Lyon", null, null, false, false);
        Actualite passee = Actualite.creer("Salon passé", "Texte", LocalDate.now().minusDays(5), "Nice", null, null, false, true);
        actualiteRepository.save(lointain);
        actualiteRepository.save(proche);
        actualiteRepository.save(passee);

        List<ActualiteResult> resultats = new ListerEvenementsAVenirUseCase(actualiteRepository).execute();

        assertThat(resultats).extracting(ActualiteResult::titre).containsExactly("Salon proche", "Salon lointain");
    }
}
