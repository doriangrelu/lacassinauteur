package fr.lacassinauteur.site.actualite.application.usecase;

import fr.lacassinauteur.site.actualite.application.command.ModifierActualiteCommand;
import fr.lacassinauteur.site.actualite.application.result.ActualiteResult;
import fr.lacassinauteur.site.actualite.domain.model.Actualite;
import fr.lacassinauteur.site.actualite.domain.port.FakeActualiteRepository;
import fr.lacassinauteur.site.shared.domain.port.FakeStockageFichierPort;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ModifierActualiteUseCaseTest {

    @Test
    void modifier_remplace_les_champs_et_conserve_lancienne_image_si_aucune_nouvelle() {
        FakeActualiteRepository actualiteRepository = new FakeActualiteRepository();
        Actualite actualite = Actualite.creer("Titre initial", "Texte", LocalDate.now().plusDays(1), "Paris", null, "/media/ancienne.png", false, false);
        actualiteRepository.save(actualite);
        FakeStockageFichierPort stockage = new FakeStockageFichierPort();

        ActualiteResult result = new ModifierActualiteUseCase(actualiteRepository, stockage).execute(new ModifierActualiteCommand(
                actualite.id(), "Titre modifié", "Nouveau texte", LocalDate.now().plusDays(2), "Lyon", null,
                null, null, false, true));

        assertThat(result.titre()).isEqualTo("Titre modifié");
        assertThat(result.imageUrl()).isEqualTo("/media/ancienne.png");
        assertThat(result.misEnAvant()).isTrue();
        assertThat(stockage.urlsSupprimees).isEmpty();
    }

    @Test
    void modifier_remplace_limage_et_supprime_lancienne() {
        FakeActualiteRepository actualiteRepository = new FakeActualiteRepository();
        Actualite actualite = Actualite.creer("Titre", "Texte", LocalDate.now().plusDays(1), "Paris", null, "/media/ancienne.png", false, false);
        actualiteRepository.save(actualite);
        FakeStockageFichierPort stockage = new FakeStockageFichierPort();

        ActualiteResult result = new ModifierActualiteUseCase(actualiteRepository, stockage).execute(new ModifierActualiteCommand(
                actualite.id(), "Titre", "Texte", LocalDate.now().plusDays(1), "Paris", null,
                "contenu".getBytes(), "nouvelle.png", false, false));

        assertThat(result.imageUrl()).isEqualTo("/media/actualites/fichier-1");
        assertThat(stockage.urlsSupprimees).containsExactly("/media/ancienne.png");
    }
}
