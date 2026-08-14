package fr.lacassinauteur.site.catalogue.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LivreTest {

    @Test
    void un_livre_nouvellement_cree_na_pas_de_fiche_professionnelle() {
        Livre livre = Livre.creer("la-rame-et-la-rage", UUID.randomUUID(), "La rame et la rage", null, "/cover.png", "Pitch", "Résumé", 1);

        assertThat(livre.ficheProfessionnelle()).isEmpty();
    }

    @Test
    void renseignerFicheProfessionnelle_lassocie_au_livre() {
        Livre livre = Livre.creer("la-rame-et-la-rage", UUID.randomUUID(), "La rame et la rage", null, "/cover.png", "Pitch", "Résumé", 1);
        FicheProfessionnelle fiche = new FicheProfessionnelle(
                "978-2-1234-5678-9", "Broché", 240, new BigDecimal("19.90"),
                "Toutes librairies, FNAC, Amazon", "Pitch éditeur", "Synopsis éditeur");

        livre.renseignerFicheProfessionnelle(fiche);

        assertThat(livre.ficheProfessionnelle()).contains(fiche);
    }

    @Test
    void retirerFicheProfessionnelle_la_dissocie_du_livre() {
        Livre livre = Livre.creer("la-rame-et-la-rage", UUID.randomUUID(), "La rame et la rage", null, "/cover.png", "Pitch", "Résumé", 1);
        livre.renseignerFicheProfessionnelle(new FicheProfessionnelle(
                "978-2-1234-5678-9", "Broché", 240, new BigDecimal("19.90"),
                "Toutes librairies", "Pitch éditeur", "Synopsis éditeur"));

        livre.retirerFicheProfessionnelle();

        assertThat(livre.ficheProfessionnelle()).isEmpty();
    }
}
