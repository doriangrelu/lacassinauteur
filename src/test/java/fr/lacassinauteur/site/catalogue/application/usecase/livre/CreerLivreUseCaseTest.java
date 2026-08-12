package fr.lacassinauteur.site.catalogue.application.usecase.livre;

import fr.lacassinauteur.site.catalogue.application.command.CreerLivreCommand;
import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.domain.port.FakeLivreRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CreerLivreUseCaseTest {

    @Test
    void cree_un_livre_non_disponible_par_defaut() {
        FakeLivreRepository livreRepository = new FakeLivreRepository();
        UUID collectionId = UUID.randomUUID();

        LivreResult result = new CreerLivreUseCase(livreRepository).execute(
                new CreerLivreCommand(collectionId, "Les liens du crime", null, "/cover.png", "Pitch", "Résumé", 1));

        assertThat(result.titre()).isEqualTo("Les liens du crime");
        assertThat(result.disponible()).isFalse();
        assertThat(result.derniereParution()).isFalse();
        assertThat(livreRepository.findByCollectionIdOrderByOrdre(collectionId)).hasSize(1);
    }
}
