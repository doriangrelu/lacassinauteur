package fr.lacassinauteur.site.contact.application.usecase;

import fr.lacassinauteur.site.contact.application.command.EnvoyerMessageContactCommand;
import fr.lacassinauteur.site.contact.application.result.MessageContactResult;
import fr.lacassinauteur.site.contact.domain.model.StatutMessage;
import fr.lacassinauteur.site.contact.domain.port.FakeEnvoiEmailContactPort;
import fr.lacassinauteur.site.contact.domain.port.FakeMessageContactRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnvoyerMessageContactUseCaseTest {

    @Test
    void envoyer_enregistre_le_message_et_notifie_lauteur() {
        FakeMessageContactRepository messageContactRepository = new FakeMessageContactRepository();
        FakeEnvoiEmailContactPort envoiEmailContactPort = new FakeEnvoiEmailContactPort();

        MessageContactResult result = new EnvoyerMessageContactUseCase(messageContactRepository, envoiEmailContactPort)
                .execute(new EnvoyerMessageContactCommand("Camille B.", "camille@exemple.fr", "Question sur un livre", "Bonjour, ..."));

        assertThat(result.statut()).isEqualTo(StatutMessage.NOUVEAU);
        assertThat(messageContactRepository.findById(result.id())).isPresent();
        assertThat(envoiEmailContactPort.notificationsEnvoyees).hasSize(1);
        assertThat(envoiEmailContactPort.notificationsEnvoyees.get(0).email()).isEqualTo("camille@exemple.fr");
    }
}
