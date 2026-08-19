package fr.lacassinauteur.site.legal.application.usecase;

import fr.lacassinauteur.site.legal.application.command.ModifierInformationsLegalesCommand;
import fr.lacassinauteur.site.legal.application.result.InformationsLegalesResult;
import fr.lacassinauteur.site.legal.domain.exception.InformationsLegalesIntrouvablesException;
import fr.lacassinauteur.site.legal.domain.model.InformationsLegales;
import fr.lacassinauteur.site.legal.domain.port.FakeInformationsLegalesRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModifierInformationsLegalesUseCaseTest {

    @Test
    void modifier_remplace_les_valeurs_et_recalcule_la_completude() {
        FakeInformationsLegalesRepository repository = new FakeInformationsLegalesRepository();
        repository.save(InformationsLegales.initiales("OVH SAS", "Roubaix", 36, 12));

        InformationsLegalesResult result = new ModifierInformationsLegalesUseCase(repository)
                .execute(new ModifierInformationsLegalesCommand("Thierry Lacassin", "Auteur auto-édité",
                        "1 rue des Livres, Lyon", "contact@exemple.fr", "Thierry Lacassin",
                        "OVH SAS", "Roubaix", 24, 6));

        assertThat(result.editeurNom()).isEqualTo("Thierry Lacassin");
        assertThat(result.conservationNewsletterMois()).isEqualTo(24);
        assertThat(result.conservationContactMois()).isEqualTo(6);
        assertThat(result.completes()).isTrue();
    }

    @Test
    void modifier_echoue_si_aucun_enregistrement_nexiste() {
        assertThatThrownBy(() -> new ModifierInformationsLegalesUseCase(new FakeInformationsLegalesRepository())
                .execute(new ModifierInformationsLegalesCommand("X", null, "Y", "z@exemple.fr", "X",
                        "OVH SAS", "Roubaix", 36, 12)))
                .isInstanceOf(InformationsLegalesIntrouvablesException.class);
    }
}
