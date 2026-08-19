package fr.lacassinauteur.site.biographie.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BiographieTest {

    @Test
    void une_photo_vide_est_normalisee_en_null() {
        // Sans cette normalisation, un th:if de présentation considérerait la chaîne
        // vide comme « photo renseignée » et afficherait une image cassée — même bug
        // que celui rencontré sur Actualite.lienBilletterie.
        assertThat(Biographie.initiale("Texte", "   ").photoUrl()).isNull();
        assertThat(Biographie.initiale("Texte", "").photoUrl()).isNull();
        assertThat(Biographie.initiale("Texte", null).photoUrl()).isNull();
    }

    @Test
    void la_normalisation_sapplique_aussi_a_la_modification() {
        Biographie biographie = Biographie.initiale("Texte", "/media/photo.jpg");

        biographie.modifier("Nouveau texte", "  ");

        assertThat(biographie.photoUrl()).isNull();
        assertThat(biographie.texte()).isEqualTo("Nouveau texte");
    }
}
