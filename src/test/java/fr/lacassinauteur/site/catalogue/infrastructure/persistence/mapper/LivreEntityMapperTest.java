package fr.lacassinauteur.site.catalogue.infrastructure.persistence.mapper;

import fr.lacassinauteur.site.catalogue.domain.model.FicheProfessionnelle;
import fr.lacassinauteur.site.catalogue.domain.model.Livre;
import fr.lacassinauteur.site.catalogue.infrastructure.persistence.entity.LivreJpaEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LivreEntityMapperTest {

    private final LivreEntityMapper mapper = new LivreEntityMapper();

    @Test
    void roundtrip_conserve_la_fiche_professionnelle_quand_elle_est_renseignee() {
        Livre livre = Livre.creer("la-rame-et-la-rage", UUID.randomUUID(), "La rame et la rage", null, "/cover.png", "Pitch", "Résumé", 1);
        livre.renseignerFicheProfessionnelle(new FicheProfessionnelle(
                "978-2-1234-5678-9", "Broché", 240, new BigDecimal("19.90"),
                "Toutes librairies, FNAC, Amazon", "Pitch éditeur", "Synopsis éditeur"));

        LivreJpaEntity entite = mapper.versEntite(livre);
        Livre roundtrip = mapper.versDomaine(entite);

        assertThat(roundtrip.ficheProfessionnelle()).contains(new FicheProfessionnelle(
                "978-2-1234-5678-9", "Broché", 240, new BigDecimal("19.90"),
                "Toutes librairies, FNAC, Amazon", "Pitch éditeur", "Synopsis éditeur"));
    }

    @Test
    void roundtrip_ne_cree_pas_de_fiche_professionnelle_quand_toutes_les_colonnes_sont_nulles() {
        Livre livre = Livre.creer("la-rame-et-la-rage", UUID.randomUUID(), "La rame et la rage", null, "/cover.png", "Pitch", "Résumé", 1);

        LivreJpaEntity entite = mapper.versEntite(livre);
        Livre roundtrip = mapper.versDomaine(entite);

        assertThat(roundtrip.ficheProfessionnelle()).isEmpty();
    }
}
