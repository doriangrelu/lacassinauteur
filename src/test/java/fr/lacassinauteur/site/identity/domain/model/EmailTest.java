package fr.lacassinauteur.site.identity.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    void accepte_une_adresse_valide_et_la_normalise_en_minuscules() {
        Email email = new Email("Thierry@Lacassinauteur.local");

        assertThat(email.valeur()).isEqualTo("thierry@lacassinauteur.local");
    }

    @Test
    void refuse_une_adresse_sans_arobase() {
        assertThatThrownBy(() -> new Email("thierry-lacassinauteur.local"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
