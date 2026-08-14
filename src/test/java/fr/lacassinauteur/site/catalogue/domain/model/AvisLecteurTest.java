package fr.lacassinauteur.site.catalogue.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AvisLecteurTest {

    @Test
    void un_avis_soumis_est_en_attente() {
        AvisLecteur avis = AvisLecteur.soumettre(UUID.randomUUID(), "Camille", "Superbe roman.", 5);

        assertThat(avis.statut()).isEqualTo(StatutAvis.EN_ATTENTE);
        assertThat(avis.note()).contains(5);
    }

    @Test
    void approuver_passe_lavis_au_statut_publie() {
        AvisLecteur avis = AvisLecteur.soumettre(UUID.randomUUID(), "Camille", "Superbe roman.", null);

        avis.approuver();

        assertThat(avis.statut()).isEqualTo(StatutAvis.PUBLIE);
        assertThat(avis.note()).isEmpty();
    }

    @Test
    void rejeter_passe_lavis_au_statut_rejete() {
        AvisLecteur avis = AvisLecteur.soumettre(UUID.randomUUID(), "Camille", "Avis limite.", null);

        avis.rejeter();

        assertThat(avis.statut()).isEqualTo(StatutAvis.REJETE);
    }
}
