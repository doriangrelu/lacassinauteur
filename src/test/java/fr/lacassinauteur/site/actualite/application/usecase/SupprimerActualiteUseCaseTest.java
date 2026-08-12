package fr.lacassinauteur.site.actualite.application.usecase;

import fr.lacassinauteur.site.actualite.domain.model.Actualite;
import fr.lacassinauteur.site.actualite.domain.port.FakeActualiteRepository;
import fr.lacassinauteur.site.shared.domain.port.FakeStockageFichierPort;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class SupprimerActualiteUseCaseTest {

    @Test
    void supprimer_retire_lactualite_et_nettoie_son_image() {
        FakeActualiteRepository actualiteRepository = new FakeActualiteRepository();
        Actualite actualite = Actualite.creer("Titre", "Texte", LocalDate.now(), "Paris", null, "/media/photo.png", false, false);
        actualiteRepository.save(actualite);
        FakeStockageFichierPort stockage = new FakeStockageFichierPort();

        new SupprimerActualiteUseCase(actualiteRepository, stockage).execute(actualite.id());

        assertThat(actualiteRepository.findById(actualite.id())).isEmpty();
        assertThat(stockage.urlsSupprimees).containsExactly("/media/photo.png");
    }
}
