package fr.lacassinauteur.site.catalogue.application.usecase.collection;

import fr.lacassinauteur.site.catalogue.domain.model.Collection;
import fr.lacassinauteur.site.catalogue.domain.port.FakeCollectionRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReordonnerCollectionsUseCaseTest {

    @Test
    void reordonne_les_collections_selon_la_liste_dids_fournie() {
        FakeCollectionRepository collectionRepository = new FakeCollectionRepository();
        UUID universId = UUID.randomUUID();
        Collection premiere = Collection.creer("premiere", universId, "Première", null, null, 1);
        Collection seconde = Collection.creer("seconde", universId, "Seconde", null, null, 2);
        collectionRepository.save(premiere);
        collectionRepository.save(seconde);

        new ReordonnerCollectionsUseCase(collectionRepository).execute(List.of(seconde.id(), premiere.id()));

        assertThat(collectionRepository.findById(seconde.id()).orElseThrow().ordre()).isEqualTo(1);
        assertThat(collectionRepository.findById(premiere.id()).orElseThrow().ordre()).isEqualTo(2);
    }
}
