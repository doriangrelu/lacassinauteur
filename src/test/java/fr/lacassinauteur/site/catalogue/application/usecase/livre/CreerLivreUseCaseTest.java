package fr.lacassinauteur.site.catalogue.application.usecase.livre;

import fr.lacassinauteur.site.catalogue.application.command.CreerLivreCommand;
import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.domain.port.FakeLivreRepository;
import fr.lacassinauteur.site.shared.domain.port.FakeStockageFichierPort;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CreerLivreUseCaseTest {

    @Test
    void cree_un_livre_non_disponible_par_defaut_et_sans_couverture() {
        FakeLivreRepository livreRepository = new FakeLivreRepository();
        UUID collectionId = UUID.randomUUID();

        LivreResult result = new CreerLivreUseCase(livreRepository, new FakeStockageFichierPort()).execute(
                new CreerLivreCommand(collectionId, "Les liens du crime", null, null, null, "Pitch", "Résumé", 1));

        assertThat(result.titre()).isEqualTo("Les liens du crime");
        assertThat(result.couvertureUrl()).isNull();
        assertThat(result.disponible()).isFalse();
        assertThat(result.derniereParution()).isFalse();
        assertThat(livreRepository.findByCollectionIdOrderByOrdre(collectionId)).hasSize(1);
    }

    @Test
    void enregistre_la_couverture_fournie_via_le_stockage() {
        FakeLivreRepository livreRepository = new FakeLivreRepository();
        FakeStockageFichierPort stockage = new FakeStockageFichierPort();
        UUID collectionId = UUID.randomUUID();

        LivreResult result = new CreerLivreUseCase(livreRepository, stockage).execute(new CreerLivreCommand(
                collectionId, "De Franck à Keller", null,
                "contenu-image".getBytes(StandardCharsets.UTF_8), "couverture.png", "Pitch", "Résumé", 2));

        assertThat(result.couvertureUrl()).isEqualTo("/media/couvertures/fichier-1");
    }
}
