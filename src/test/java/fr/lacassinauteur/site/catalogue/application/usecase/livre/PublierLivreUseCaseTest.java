package fr.lacassinauteur.site.catalogue.application.usecase.livre;

import fr.lacassinauteur.site.catalogue.application.command.PublierLivreCommand;
import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.domain.model.Livre;
import fr.lacassinauteur.site.catalogue.domain.port.FakeLivreRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PublierLivreUseCaseTest {

    @Test
    void publier_rend_le_livre_disponible_avec_son_lien_dachat() {
        FakeLivreRepository livreRepository = new FakeLivreRepository();
        Livre livre = Livre.creer("la-rame-et-la-rage", UUID.randomUUID(), "La rame et la rage", null, "/cover.png", "Pitch", "Résumé", 1);
        livreRepository.save(livre);

        LivreResult result = new PublierLivreUseCase(livreRepository)
                .execute(new PublierLivreCommand(livre.id(), "https://www.amazon.fr/dp/XXXX", "Amazon"));

        assertThat(result.disponible()).isTrue();
        assertThat(result.lienAchatUrl()).isEqualTo("https://www.amazon.fr/dp/XXXX");
        assertThat(result.lienAchatLibelle()).isEqualTo("Amazon");
    }
}
