package fr.lacassinauteur.site.newsletter.application.usecase;

import fr.lacassinauteur.site.newsletter.application.result.AbonneNewsletterResult;
import fr.lacassinauteur.site.newsletter.domain.exception.AbonneIntrouvableException;
import fr.lacassinauteur.site.newsletter.domain.model.AbonneNewsletter;
import fr.lacassinauteur.site.newsletter.domain.model.Email;
import fr.lacassinauteur.site.newsletter.domain.model.StatutAbonnement;
import fr.lacassinauteur.site.newsletter.domain.port.FakeAbonneNewsletterRepository;
import fr.lacassinauteur.site.newsletter.domain.port.FakeSynchronisationEspPort;
import fr.lacassinauteur.site.newsletter.domain.port.SynchronisationEspPort;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DesinscrireAbonneUseCaseTest {

    @Test
    void desinscrire_un_abonne_confirme_le_passe_au_statut_desinscrit() {
        FakeAbonneNewsletterRepository repository = new FakeAbonneNewsletterRepository();
        FakeSynchronisationEspPort synchronisationEspPort = new FakeSynchronisationEspPort();
        AbonneNewsletter abonne = AbonneNewsletter.creer("Alice", new Email("alice@example.com"));
        abonne.confirmer();
        repository.save(abonne);

        AbonneNewsletterResult result = new DesinscrireAbonneUseCase(repository, synchronisationEspPort)
                .execute(abonne.jetonConfirmation());

        assertThat(result.statut()).isEqualTo(StatutAbonnement.DESINSCRIT);
        assertThat(synchronisationEspPort.retires).hasSize(1);
    }

    @Test
    void un_echec_de_synchronisation_brevo_ne_fait_pas_echouer_la_desinscription() {
        FakeAbonneNewsletterRepository repository = new FakeAbonneNewsletterRepository();
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
        abonne.confirmer();
        repository.save(abonne);

        AbonneNewsletterResult result = new DesinscrireAbonneUseCase(repository, synchronisationEspEnPanne)
                .execute(abonne.jetonConfirmation());

        assertThat(result.statut()).isEqualTo(StatutAbonnement.DESINSCRIT);
    }

    @Test
    void desinscrire_avec_un_jeton_inconnu_leve_abonne_introuvable() {
        FakeAbonneNewsletterRepository repository = new FakeAbonneNewsletterRepository();
        FakeSynchronisationEspPort synchronisationEspPort = new FakeSynchronisationEspPort();

        assertThatThrownBy(() -> new DesinscrireAbonneUseCase(repository, synchronisationEspPort).execute(UUID.randomUUID()))
                .isInstanceOf(AbonneIntrouvableException.class);
    }
}
