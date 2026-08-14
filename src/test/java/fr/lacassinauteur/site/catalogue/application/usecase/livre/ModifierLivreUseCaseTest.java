package fr.lacassinauteur.site.catalogue.application.usecase.livre;

import fr.lacassinauteur.site.catalogue.application.command.ModifierLivreCommand;
import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.domain.model.Livre;
import fr.lacassinauteur.site.catalogue.domain.port.FakeLivreRepository;
import fr.lacassinauteur.site.shared.domain.port.FakeStockageFichierPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ModifierLivreUseCaseTest {

    @Test
    void renseigne_la_fiche_professionnelle_quand_au_moins_un_champ_est_fourni() {
        FakeLivreRepository livreRepository = new FakeLivreRepository();
        UUID collectionId = UUID.randomUUID();
        Livre livre = Livre.creer("la-rame-et-la-rage", collectionId, "La rame et la rage", null, "/cover.png", "Pitch", "Résumé", 1);
        livreRepository.save(livre);

        LivreResult result = new ModifierLivreUseCase(livreRepository, new FakeStockageFichierPort()).execute(
                new ModifierLivreCommand(livre.id(), collectionId, "La rame et la rage", null, null, null,
                        "Pitch", "Résumé", 1,
                        "978-2-1234-5678-9", "Broché", 240, new BigDecimal("19.90"),
                        "Toutes librairies, FNAC, Amazon", "Pitch éditeur", "Synopsis éditeur"));

        assertThat(result.ficheProfessionnelleRenseignee()).isTrue();
        assertThat(result.isbn()).isEqualTo("978-2-1234-5678-9");
        assertThat(result.format()).isEqualTo("Broché");
        assertThat(result.nombrePages()).isEqualTo(240);
        assertThat(result.prix()).isEqualByComparingTo("19.90");
        assertThat(result.lieuxDistribution()).isEqualTo("Toutes librairies, FNAC, Amazon");
        assertThat(result.pitchEditeur()).isEqualTo("Pitch éditeur");
        assertThat(result.synopsisEditeur()).isEqualTo("Synopsis éditeur");
    }

    @Test
    void ne_renseigne_pas_de_fiche_professionnelle_quand_tous_les_champs_sont_vides() {
        FakeLivreRepository livreRepository = new FakeLivreRepository();
        UUID collectionId = UUID.randomUUID();
        Livre livre = Livre.creer("la-rame-et-la-rage", collectionId, "La rame et la rage", null, "/cover.png", "Pitch", "Résumé", 1);
        livreRepository.save(livre);

        LivreResult result = new ModifierLivreUseCase(livreRepository, new FakeStockageFichierPort()).execute(
                new ModifierLivreCommand(livre.id(), collectionId, "La rame et la rage", null, null, null,
                        "Pitch", "Résumé", 1,
                        null, null, null, null, null, null, null));

        assertThat(result.ficheProfessionnelleRenseignee()).isFalse();
    }

    @Test
    void retire_la_fiche_professionnelle_existante_quand_les_champs_sont_vidés() {
        FakeLivreRepository livreRepository = new FakeLivreRepository();
        UUID collectionId = UUID.randomUUID();
        Livre livre = Livre.creer("la-rame-et-la-rage", collectionId, "La rame et la rage", null, "/cover.png", "Pitch", "Résumé", 1);
        livre.renseignerFicheProfessionnelle(new fr.lacassinauteur.site.catalogue.domain.model.FicheProfessionnelle(
                "978-2-1234-5678-9", "Broché", 240, new BigDecimal("19.90"), "Toutes librairies", "Pitch éditeur", "Synopsis"));
        livreRepository.save(livre);

        LivreResult result = new ModifierLivreUseCase(livreRepository, new FakeStockageFichierPort()).execute(
                new ModifierLivreCommand(livre.id(), collectionId, "La rame et la rage", null, null, null,
                        "Pitch", "Résumé", 1,
                        "  ", null, null, null, null, null, null));

        assertThat(result.ficheProfessionnelleRenseignee()).isFalse();
        assertThat(result.isbn()).isNull();
    }
}
