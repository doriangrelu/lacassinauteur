package fr.lacassinauteur.site.biographie.application.usecase;

import fr.lacassinauteur.site.biographie.application.result.BiographieResult;
import fr.lacassinauteur.site.biographie.domain.exception.BiographieIntrouvableException;
import fr.lacassinauteur.site.biographie.domain.model.Biographie;
import fr.lacassinauteur.site.biographie.domain.port.FakeBiographieRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsulterBiographieUseCaseTest {

    @Test
    void consulter_retourne_la_biographie_enregistree() {
        FakeBiographieRepository repository = new FakeBiographieRepository();
        repository.save(Biographie.initiale("Thierry Lacassin écrit des romans noirs.", "/images/auteur/photo.jpg"));

        BiographieResult result = new ConsulterBiographieUseCase(repository).execute();

        assertThat(result.texte()).isEqualTo("Thierry Lacassin écrit des romans noirs.");
        assertThat(result.photoUrl()).isEqualTo("/images/auteur/photo.jpg");
    }

    @Test
    void consulter_echoue_si_aucune_biographie_nexiste() {
        assertThatThrownBy(() -> new ConsulterBiographieUseCase(new FakeBiographieRepository()).execute())
                .isInstanceOf(BiographieIntrouvableException.class);
    }
}
