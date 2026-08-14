package fr.lacassinauteur.site.newsletter.application.usecase;

import fr.lacassinauteur.site.newsletter.application.result.AbonneNewsletterResult;
import fr.lacassinauteur.site.newsletter.domain.exception.AbonneIntrouvableException;
import fr.lacassinauteur.site.newsletter.domain.model.AbonneNewsletter;
import fr.lacassinauteur.site.newsletter.domain.model.Email;
import fr.lacassinauteur.site.newsletter.domain.model.StatutAbonnement;
import fr.lacassinauteur.site.newsletter.domain.port.FakeAbonneNewsletterRepository;
import fr.lacassinauteur.site.newsletter.domain.port.FakeEnvoiEmailPort;
import fr.lacassinauteur.site.newsletter.domain.port.FakeSynchronisationEspPort;
import fr.lacassinauteur.site.newsletter.domain.port.SynchronisationEspPort;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConfirmerInscriptionUseCaseTest {

    private static final String URL_BASE = "http://localhost:8080";

    @Test
    void confirmer_avec_un_jeton_valide_passe_labonne_a_confirme_et_envoie_lemail_de_bienvenue() {
        FakeAbonneNewsletterRepository repository = new FakeAbonneNewsletterRepository();
        FakeEnvoiEmailPort envoiEmailPort = new FakeEnvoiEmailPort();
        FakeSynchronisationEspPort synchronisationEspPort = new FakeSynchronisationEspPort();
        AbonneNewsletter abonne = AbonneNewsletter.creer("Alice", new Email("alice@example.com"));
        repository.save(abonne);

        AbonneNewsletterResult result = new ConfirmerInscriptionUseCase(repository, envoiEmailPort, synchronisationEspPort, URL_BASE)
                .execute(abonne.jetonConfirmation());

        assertThat(result.statut()).isEqualTo(StatutAbonnement.CONFIRME);
        assertThat(result.dateConfirmation()).isNotNull();
        assertThat(envoiEmailPort.bienvenuesEnvoyees).hasSize(1);
        assertThat(envoiEmailPort.bienvenuesEnvoyees.getFirst().lienDesinscription())
                .isEqualTo(URL_BASE + "/newsletter/desinscrire?jeton=" + abonne.jetonConfirmation());
    }

    @Test
    void confirmer_synchronise_labonne_vers_brevo() {
        FakeAbonneNewsletterRepository repository = new FakeAbonneNewsletterRepository();
        FakeEnvoiEmailPort envoiEmailPort = new FakeEnvoiEmailPort();
        FakeSynchronisationEspPort synchronisationEspPort = new FakeSynchronisationEspPort();
        AbonneNewsletter abonne = AbonneNewsletter.creer("Alice", new Email("alice@example.com"));
        repository.save(abonne);

        new ConfirmerInscriptionUseCase(repository, envoiEmailPort, synchronisationEspPort, URL_BASE)
                .execute(abonne.jetonConfirmation());

        assertThat(synchronisationEspPort.ajoutes).hasSize(1);
        assertThat(synchronisationEspPort.ajoutes.getFirst().email()).isEqualTo(new Email("alice@example.com"));
    }

    @Test
    void un_echec_de_synchronisation_brevo_ne_fait_pas_echouer_la_confirmation() {
        FakeAbonneNewsletterRepository repository = new FakeAbonneNewsletterRepository();
        FakeEnvoiEmailPort envoiEmailPort = new FakeEnvoiEmailPort();
        SynchronisationEspPort synchronisationEspEnPanne = new SynchronisationEspPort() {
            @Override
            public void ajouterOuMettreAJour(AbonneNewsletter abonne) {
                throw new RuntimeException("Brevo indisponible");
            }

            @Override
            public void retirer(AbonneNewsletter abonne) {
                throw new RuntimeException("Brevo indisponible");
            }
        };
        AbonneNewsletter abonne = AbonneNewsletter.creer("Alice", new Email("alice@example.com"));
        repository.save(abonne);

        AbonneNewsletterResult result = new ConfirmerInscriptionUseCase(repository, envoiEmailPort, synchronisationEspEnPanne, URL_BASE)
                .execute(abonne.jetonConfirmation());

        assertThat(result.statut()).isEqualTo(StatutAbonnement.CONFIRME);
    }

    @Test
    void confirmer_deux_fois_est_idempotent_et_ne_renvoie_pas_lemail_de_bienvenue() {
        FakeAbonneNewsletterRepository repository = new FakeAbonneNewsletterRepository();
        FakeEnvoiEmailPort envoiEmailPort = new FakeEnvoiEmailPort();
        FakeSynchronisationEspPort synchronisationEspPort = new FakeSynchronisationEspPort();
        AbonneNewsletter abonne = AbonneNewsletter.creer("Alice", new Email("alice@example.com"));
        repository.save(abonne);
        ConfirmerInscriptionUseCase useCase = new ConfirmerInscriptionUseCase(repository, envoiEmailPort, synchronisationEspPort, URL_BASE);

        useCase.execute(abonne.jetonConfirmation());
        useCase.execute(abonne.jetonConfirmation());

        assertThat(envoiEmailPort.bienvenuesEnvoyees).hasSize(1);
    }

    @Test
    void confirmer_avec_un_jeton_inconnu_leve_abonne_introuvable() {
        FakeAbonneNewsletterRepository repository = new FakeAbonneNewsletterRepository();
        FakeEnvoiEmailPort envoiEmailPort = new FakeEnvoiEmailPort();
        FakeSynchronisationEspPort synchronisationEspPort = new FakeSynchronisationEspPort();
        ConfirmerInscriptionUseCase useCase = new ConfirmerInscriptionUseCase(repository, envoiEmailPort, synchronisationEspPort, URL_BASE);

        assertThatThrownBy(() -> useCase.execute(UUID.randomUUID()))
                .isInstanceOf(AbonneIntrouvableException.class);
    }
}
