package fr.lacassinauteur.site.actualite.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ActualiteTest {

    private static final LocalDate AUJOURDHUI = LocalDate.of(2026, 8, 12);

    @Test
    void une_date_future_est_un_evenement_a_venir() {
        Actualite actualite = Actualite.creer("Salon du livre", "Texte", AUJOURDHUI.plusDays(10), "Paris", null, null, false, false);

        assertThat(actualite.type(AUJOURDHUI)).isEqualTo(TypeActualite.EVENEMENT_A_VENIR);
    }

    @Test
    void une_date_passee_est_une_actualite_passee() {
        Actualite actualite = Actualite.creer("Salon du livre", "Texte", AUJOURDHUI.minusDays(10), "Paris", null, null, false, false);

        assertThat(actualite.type(AUJOURDHUI)).isEqualTo(TypeActualite.ACTUALITE_PASSEE);
    }

    @Test
    void larchivage_manuel_force_le_type_passee_meme_pour_une_date_future() {
        Actualite actualite = Actualite.creer("Événement annulé", "Texte", AUJOURDHUI.plusDays(10), "Paris", null, null, true, false);

        assertThat(actualite.type(AUJOURDHUI)).isEqualTo(TypeActualite.ACTUALITE_PASSEE);
    }

    @Test
    void les_champs_optionnels_vides_sont_normalises_en_null() {
        Actualite actualite = Actualite.creer("Titre", "", AUJOURDHUI, "", "", null, false, false);

        assertThat(actualite.texte()).isNull();
        assertThat(actualite.lieu()).isNull();
        assertThat(actualite.lienBilletterie()).isNull();
    }
}
