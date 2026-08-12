package fr.lacassinauteur.site.newsletter.application.usecase;

import fr.lacassinauteur.site.newsletter.application.command.InscrireAbonneCommand;
import fr.lacassinauteur.site.newsletter.application.result.AbonneNewsletterResult;
import fr.lacassinauteur.site.newsletter.domain.model.AbonneNewsletter;
import fr.lacassinauteur.site.newsletter.domain.model.Email;
import fr.lacassinauteur.site.newsletter.domain.model.StatutAbonnement;
import fr.lacassinauteur.site.newsletter.domain.port.FakeAbonneNewsletterRepository;
import fr.lacassinauteur.site.newsletter.domain.port.FakeEnvoiEmailPort;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InscrireAbonneUseCaseTest {

    private static final String URL_BASE = "http://localhost:8080";

    @Test
    void inscription_nouvelle_cree_labonne_en_attente_et_envoie_lemail_de_confirmation() {
        FakeAbonneNewsletterRepository repository = new FakeAbonneNewsletterRepository();
        FakeEnvoiEmailPort envoiEmailPort = new FakeEnvoiEmailPort();

        AbonneNewsletterResult result = new InscrireAbonneUseCase(repository, envoiEmailPort, URL_BASE)
                .execute(new InscrireAbonneCommand("Alice", "alice@example.com"));

        assertThat(result.statut()).isEqualTo(StatutAbonnement.EN_ATTENTE_CONFIRMATION);
        assertThat(result.prenom()).isEqualTo("Alice");
        assertThat(result.email()).isEqualTo("alice@example.com");
        assertThat(envoiEmailPort.confirmationsEnvoyees).hasSize(1);
        assertThat(envoiEmailPort.confirmationsEnvoyees.getFirst().lienConfirmation())
                .isEqualTo(URL_BASE + "/newsletter/confirmer?jeton=" + result.jetonConfirmation());
    }

    @Test
    void inscription_dun_abonne_deja_confirme_ne_renvoie_pas_demail_et_ne_leve_pas_derreur() {
        FakeAbonneNewsletterRepository repository = new FakeAbonneNewsletterRepository();
        FakeEnvoiEmailPort envoiEmailPort = new FakeEnvoiEmailPort();
        AbonneNewsletter abonneConfirme = AbonneNewsletter.creer("Bob", new Email("bob@example.com"));
        abonneConfirme.confirmer();
        repository.save(abonneConfirme);

        AbonneNewsletterResult result = new InscrireAbonneUseCase(repository, envoiEmailPort, URL_BASE)
                .execute(new InscrireAbonneCommand("Bob", "bob@example.com"));

        assertThat(result.statut()).isEqualTo(StatutAbonnement.CONFIRME);
        assertThat(envoiEmailPort.confirmationsEnvoyees).isEmpty();
    }

    @Test
    void reinscription_dun_abonne_desinscrit_relance_le_parcours_avec_un_nouveau_jeton() {
        FakeAbonneNewsletterRepository repository = new FakeAbonneNewsletterRepository();
        FakeEnvoiEmailPort envoiEmailPort = new FakeEnvoiEmailPort();
        AbonneNewsletter abonneDesinscrit = AbonneNewsletter.creer("Chloé", new Email("chloe@example.com"));
        abonneDesinscrit.confirmer();
        abonneDesinscrit.desinscrire();
        var ancienJeton = abonneDesinscrit.jetonConfirmation();
        repository.save(abonneDesinscrit);

        AbonneNewsletterResult result = new InscrireAbonneUseCase(repository, envoiEmailPort, URL_BASE)
                .execute(new InscrireAbonneCommand("Chloé", "chloe@example.com"));

        assertThat(result.statut()).isEqualTo(StatutAbonnement.EN_ATTENTE_CONFIRMATION);
        assertThat(result.jetonConfirmation()).isNotEqualTo(ancienJeton);
        assertThat(envoiEmailPort.confirmationsEnvoyees).hasSize(1);
    }
}
