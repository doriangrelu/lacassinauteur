package fr.lacassinauteur.site.shared.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SlugTest {

    @Test
    void normalise_accents_majuscules_et_ponctuation() {
        assertThat(Slug.depuis("De Franck à Keller").valeur()).isEqualTo("de-franck-a-keller");
        assertThat(Slug.depuis("L'Homme de Sarenne").valeur()).isEqualTo("l-homme-de-sarenne");
        assertThat(Slug.depuis("Moi, Camille B. Journaliste").valeur()).isEqualTo("moi-camille-b-journaliste");
    }

    @Test
    void genere_un_suffixe_en_cas_de_collision() {
        Slug slug = Slug.genererUnique("De Franck à Keller", s -> s.equals("de-franck-a-keller"));

        assertThat(slug.valeur()).isEqualTo("de-franck-a-keller-2");
    }

    @Test
    void ne_genere_pas_de_suffixe_si_le_slug_est_libre() {
        Slug slug = Slug.genererUnique("De Franck à Keller", s -> false);

        assertThat(slug.valeur()).isEqualTo("de-franck-a-keller");
    }
}
