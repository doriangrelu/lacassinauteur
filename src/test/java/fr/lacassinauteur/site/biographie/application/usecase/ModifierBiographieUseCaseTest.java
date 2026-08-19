package fr.lacassinauteur.site.biographie.application.usecase;

import fr.lacassinauteur.site.biographie.application.command.ModifierBiographieCommand;
import fr.lacassinauteur.site.biographie.application.result.BiographieResult;
import fr.lacassinauteur.site.biographie.domain.exception.BiographieIntrouvableException;
import fr.lacassinauteur.site.biographie.domain.model.Biographie;
import fr.lacassinauteur.site.biographie.domain.port.FakeBiographieRepository;
import fr.lacassinauteur.site.shared.domain.port.FakeStockageFichierPort;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModifierBiographieUseCaseTest {

    @Test
    void modifier_remplace_le_texte_et_conserve_la_photo_si_aucune_nouvelle() {
        FakeBiographieRepository repository = new FakeBiographieRepository();
        repository.save(Biographie.initiale("Texte initial", "/images/auteur/thierry-lacassin.jpg"));
        FakeStockageFichierPort stockage = new FakeStockageFichierPort();

        BiographieResult result = new ModifierBiographieUseCase(repository, stockage)
                .execute(new ModifierBiographieCommand("Nouveau texte", null, null));

        assertThat(result.texte()).isEqualTo("Nouveau texte");
        assertThat(result.photoUrl()).isEqualTo("/images/auteur/thierry-lacassin.jpg");
        assertThat(stockage.urlsSupprimees).isEmpty();
    }

    @Test
    void modifier_remplace_la_photo_et_supprime_lancienne() {
        FakeBiographieRepository repository = new FakeBiographieRepository();
        repository.save(Biographie.initiale("Texte", "/media/auteur/ancienne.jpg"));
        FakeStockageFichierPort stockage = new FakeStockageFichierPort();

        BiographieResult result = new ModifierBiographieUseCase(repository, stockage)
                .execute(new ModifierBiographieCommand("Texte", "contenu".getBytes(), "nouvelle.jpg"));

        assertThat(result.photoUrl()).isEqualTo("/media/auteur/fichier-1");
        assertThat(stockage.urlsSupprimees).containsExactly("/media/auteur/ancienne.jpg");
    }

    @Test
    void modifier_echoue_si_aucune_biographie_nexiste() {
        FakeBiographieRepository repository = new FakeBiographieRepository();

        assertThatThrownBy(() -> new ModifierBiographieUseCase(repository, new FakeStockageFichierPort())
                .execute(new ModifierBiographieCommand("Texte", null, null)))
                .isInstanceOf(BiographieIntrouvableException.class);
    }
}
