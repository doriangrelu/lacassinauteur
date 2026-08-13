package fr.lacassinauteur.site.contact.application.usecase;

import fr.lacassinauteur.site.contact.domain.exception.MessageContactIntrouvableException;
import fr.lacassinauteur.site.contact.domain.model.MessageContact;
import fr.lacassinauteur.site.contact.domain.model.StatutMessage;
import fr.lacassinauteur.site.contact.domain.port.FakeMessageContactRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MarquerMessageTraiteUseCaseTest {

    @Test
    void marquer_traite_change_le_statut() {
        FakeMessageContactRepository messageContactRepository = new FakeMessageContactRepository();
        MessageContact message = MessageContact.creer("Camille B.", "camille@exemple.fr", "Objet", "Message");
        messageContactRepository.save(message);

        new MarquerMessageTraiteUseCase(messageContactRepository).execute(message.id());

        assertThat(messageContactRepository.findById(message.id()).orElseThrow().statut()).isEqualTo(StatutMessage.TRAITE);
    }

    @Test
    void marquer_traite_un_message_inconnu_leve_message_introuvable() {
        FakeMessageContactRepository messageContactRepository = new FakeMessageContactRepository();
        MarquerMessageTraiteUseCase useCase = new MarquerMessageTraiteUseCase(messageContactRepository);

        assertThatThrownBy(() -> useCase.execute(UUID.randomUUID()))
                .isInstanceOf(MessageContactIntrouvableException.class);
    }
}
