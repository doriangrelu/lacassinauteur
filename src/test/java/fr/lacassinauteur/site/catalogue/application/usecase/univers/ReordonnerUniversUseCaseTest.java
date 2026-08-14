package fr.lacassinauteur.site.catalogue.application.usecase.univers;

import fr.lacassinauteur.site.catalogue.domain.model.Univers;
import fr.lacassinauteur.site.catalogue.domain.port.FakeUniversRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReordonnerUniversUseCaseTest {

    @Test
    void reordonne_les_univers_selon_la_liste_dids_fournie() {
        FakeUniversRepository universRepository = new FakeUniversRepository();
        Univers premier = Univers.creer("premier", "Premier", null, null, null, 1);
        Univers second = Univers.creer("second", "Second", null, null, null, 2);
        universRepository.save(premier);
        universRepository.save(second);

        new ReordonnerUniversUseCase(universRepository).execute(List.of(second.id(), premier.id()));

        assertThat(universRepository.findById(second.id()).orElseThrow().ordre()).isEqualTo(1);
        assertThat(universRepository.findById(premier.id()).orElseThrow().ordre()).isEqualTo(2);
    }
}
