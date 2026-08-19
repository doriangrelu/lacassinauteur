package fr.lacassinauteur.site.legal.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InformationsLegalesTest {

    @Test
    void les_valeurs_vides_sont_normalisees_en_null() {
        InformationsLegales informations = new InformationsLegales(
                java.util.UUID.randomUUID(), "  ", "", null, "  ", "", "OVH SAS", "Roubaix", 36, 12);

        assertThat(informations.editeurNom()).isNull();
        assertThat(informations.editeurStatut()).isNull();
        assertThat(informations.editeurAdresse()).isNull();
        assertThat(informations.editeurEmail()).isNull();
        assertThat(informations.directeurPublication()).isNull();
        assertThat(informations.hebergeurNom()).isEqualTo("OVH SAS");
    }

    @Test
    void les_informations_initiales_ne_sont_pas_completes() {
        // Le seeder ne connaît que l'hébergeur : l'identité de l'éditeur relève
        // d'informations juridiques réelles, que seul l'auteur peut fournir.
        InformationsLegales informations = InformationsLegales.initiales("OVH SAS", "Roubaix", 36, 12);

        assertThat(informations.completes()).isFalse();
    }

    @Test
    void les_informations_sont_completes_quand_toutes_les_mentions_obligatoires_sont_la() {
        InformationsLegales informations = InformationsLegales.initiales("OVH SAS", "Roubaix", 36, 12);

        informations.modifier("Thierry Lacassin", "Auteur auto-édité", "1 rue des Livres, Lyon",
                "contact@exemple.fr", "Thierry Lacassin", "OVH SAS", "Roubaix", 36, 12);

        assertThat(informations.completes()).isTrue();
    }

    @Test
    void il_manque_une_seule_mention_obligatoire_pour_etre_incomplet() {
        InformationsLegales informations = InformationsLegales.initiales("OVH SAS", "Roubaix", 36, 12);

        // Tout sauf le directeur de publication.
        informations.modifier("Thierry Lacassin", null, "1 rue des Livres, Lyon",
                "contact@exemple.fr", null, "OVH SAS", "Roubaix", 36, 12);

        assertThat(informations.completes()).isFalse();
    }
}
