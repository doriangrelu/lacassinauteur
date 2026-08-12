package fr.lacassinauteur.site.catalogue.application.usecase.livre;

import fr.lacassinauteur.site.catalogue.application.command.DefinirDerniereParutionCommand;
import fr.lacassinauteur.site.catalogue.domain.model.Livre;
import fr.lacassinauteur.site.catalogue.domain.port.FakeLivreRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DefinirDerniereParutionUseCaseTest {

    @Test
    void une_seule_derniere_parution_a_la_fois() {
        FakeLivreRepository livreRepository = new FakeLivreRepository();
        Livre ancien = Livre.creer("ancien-livre", UUID.randomUUID(), "Ancien livre", null, "/a.png", "p", "r", 1);
        ancien.marquerCommeDerniereParution();
        livreRepository.save(ancien);

        Livre nouveau = Livre.creer("nouveau-livre", UUID.randomUUID(), "Nouveau livre", null, "/b.png", "p", "r", 2);
        livreRepository.save(nouveau);

        new DefinirDerniereParutionUseCase(livreRepository).execute(new DefinirDerniereParutionCommand(nouveau.id()));

        assertThat(livreRepository.findById(ancien.id()).orElseThrow().derniereParution()).isFalse();
        assertThat(livreRepository.findById(nouveau.id()).orElseThrow().derniereParution()).isTrue();
        assertThat(livreRepository.findDerniereParution()).map(Livre::id).contains(nouveau.id());
    }
}
