package fr.lacassinauteur.site.catalogue.application.usecase.livre;

import fr.lacassinauteur.site.catalogue.domain.model.Livre;
import fr.lacassinauteur.site.catalogue.domain.port.FakeLivreRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReordonnerLivresUseCaseTest {

    @Test
    void reordonne_les_livres_selon_la_liste_dids_fournie() {
        FakeLivreRepository livreRepository = new FakeLivreRepository();
        UUID collectionId = UUID.randomUUID();
        Livre premier = Livre.creer("premier", collectionId, "Premier", null, null, "p", "r", 1);
        Livre second = Livre.creer("second", collectionId, "Second", null, null, "p", "r", 2);
        livreRepository.save(premier);
        livreRepository.save(second);

        new ReordonnerLivresUseCase(livreRepository).execute(List.of(second.id(), premier.id()));

        assertThat(livreRepository.findById(second.id()).orElseThrow().ordre()).isEqualTo(1);
        assertThat(livreRepository.findById(premier.id()).orElseThrow().ordre()).isEqualTo(2);
    }
}
