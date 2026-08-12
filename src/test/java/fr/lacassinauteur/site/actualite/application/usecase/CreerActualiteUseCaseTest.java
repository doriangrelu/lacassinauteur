package fr.lacassinauteur.site.actualite.application.usecase;

import fr.lacassinauteur.site.actualite.application.command.CreerActualiteCommand;
import fr.lacassinauteur.site.actualite.application.result.ActualiteResult;
import fr.lacassinauteur.site.actualite.domain.port.FakeActualiteRepository;
import fr.lacassinauteur.site.shared.domain.port.FakeStockageFichierPort;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CreerActualiteUseCaseTest {

    @Test
    void creer_enregistre_lactualite_sans_image() {
        FakeActualiteRepository actualiteRepository = new FakeActualiteRepository();
        CreerActualiteUseCase useCase = new CreerActualiteUseCase(actualiteRepository, new FakeStockageFichierPort());

        ActualiteResult result = useCase.execute(new CreerActualiteCommand(
                "Salon du livre de Paris", "Dédicace à 14h", LocalDate.now().plusMonths(1), "Paris",
                "https://billetterie.example", null, null, false, false));

        assertThat(result.titre()).isEqualTo("Salon du livre de Paris");
        assertThat(result.imageUrl()).isNull();
        assertThat(actualiteRepository.findById(result.id())).isPresent();
    }

    @Test
    void creer_enregistre_limage_via_le_stockage() {
        FakeActualiteRepository actualiteRepository = new FakeActualiteRepository();
        FakeStockageFichierPort stockage = new FakeStockageFichierPort();
        CreerActualiteUseCase useCase = new CreerActualiteUseCase(actualiteRepository, stockage);

        ActualiteResult result = useCase.execute(new CreerActualiteCommand(
                "Dédicace", "Texte", LocalDate.now().plusDays(5), "Lyon", null,
                "contenu".getBytes(), "photo.png", false, false));

        assertThat(result.imageUrl()).isEqualTo("/media/actualites/fichier-1");
    }
}
