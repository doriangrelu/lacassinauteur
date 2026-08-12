package fr.lacassinauteur.site.newsletter.application.usecase;

import fr.lacassinauteur.site.newsletter.application.result.AbonneNewsletterResult;
import fr.lacassinauteur.site.newsletter.domain.exception.AbonneIntrouvableException;
import fr.lacassinauteur.site.newsletter.domain.model.AbonneNewsletter;
import fr.lacassinauteur.site.newsletter.domain.model.Email;
import fr.lacassinauteur.site.newsletter.domain.model.StatutAbonnement;
import fr.lacassinauteur.site.newsletter.domain.port.FakeAbonneNewsletterRepository;
import fr.lacassinauteur.site.newsletter.domain.port.FakeEnvoiEmailPort;
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
        AbonneNewsletter abonne = AbonneNewsletter.creer("Alice", new Email("alice@example.com"));
        repository.save(abonne);

        AbonneNewsletterResult result = new ConfirmerInscriptionUseCase(repository, envoiEmailPort, URL_BASE)
                .execute(abonne.jetonConfirmation());

        assertThat(result.statut()).isEqualTo(StatutAbonnement.CONFIRME);
        assertThat(result.dateConfirmation()).isNotNull();
        assertThat(envoiEmailPort.bienvenuesEnvoyees).hasSize(1);
        assertThat(envoiEmailPort.bienvenuesEnvoyees.getFirst().lienDesinscription())
                .isEqualTo(URL_BASE + "/newsletter/desinscrire?jeton=" + abonne.jetonConfirmation());
    }

    @Test
    void confirmer_deux_fois_est_idempotent_et_ne_renvoie_pas_lemail_de_bienvenue() {
        FakeAbonneNewsletterRepository repository = new FakeAbonneNewsletterRepository();
        FakeEnvoiEmailPort envoiEmailPort = new FakeEnvoiEmailPort();
        AbonneNewsletter abonne = AbonneNewsletter.creer("Alice", new Email("alice@example.com"));
        repository.save(abonne);
        ConfirmerInscriptionUseCase useCase = new ConfirmerInscriptionUseCase(repository, envoiEmailPort, URL_BASE);

        useCase.execute(abonne.jetonConfirmation());
        useCase.execute(abonne.jetonConfirmation());

        assertThat(envoiEmailPort.bienvenuesEnvoyees).hasSize(1);
    }

    @Test
    void confirmer_avec_un_jeton_inconnu_leve_abonne_introuvable() {
        FakeAbonneNewsletterRepository repository = new FakeAbonneNewsletterRepository();
        FakeEnvoiEmailPort envoiEmailPort = new FakeEnvoiEmailPort();
        ConfirmerInscriptionUseCase useCase = new ConfirmerInscriptionUseCase(repository, envoiEmailPort, URL_BASE);

        assertThatThrownBy(() -> useCase.execute(UUID.randomUUID()))
                .isInstanceOf(AbonneIntrouvableException.class);
    }
}
