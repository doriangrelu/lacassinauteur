package fr.lacassinauteur.site.newsletter.application.usecase;

import fr.lacassinauteur.site.newsletter.application.result.AbonneNewsletterResult;
import fr.lacassinauteur.site.newsletter.domain.exception.AbonneIntrouvableException;
import fr.lacassinauteur.site.newsletter.domain.model.AbonneNewsletter;
import fr.lacassinauteur.site.newsletter.domain.model.Email;
import fr.lacassinauteur.site.newsletter.domain.model.StatutAbonnement;
import fr.lacassinauteur.site.newsletter.domain.port.FakeAbonneNewsletterRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DesinscrireAbonneUseCaseTest {

    @Test
    void desinscrire_un_abonne_confirme_le_passe_au_statut_desinscrit() {
        FakeAbonneNewsletterRepository repository = new FakeAbonneNewsletterRepository();
        AbonneNewsletter abonne = AbonneNewsletter.creer("Alice", new Email("alice@example.com"));
        abonne.confirmer();
        repository.save(abonne);

        AbonneNewsletterResult result = new DesinscrireAbonneUseCase(repository).execute(abonne.jetonConfirmation());

        assertThat(result.statut()).isEqualTo(StatutAbonnement.DESINSCRIT);
    }

    @Test
    void desinscrire_avec_un_jeton_inconnu_leve_abonne_introuvable() {
        FakeAbonneNewsletterRepository repository = new FakeAbonneNewsletterRepository();

        assertThatThrownBy(() -> new DesinscrireAbonneUseCase(repository).execute(UUID.randomUUID()))
                .isInstanceOf(AbonneIntrouvableException.class);
    }
}
