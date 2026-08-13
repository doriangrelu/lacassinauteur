package fr.lacassinauteur.site.contact.application.usecase;

import fr.lacassinauteur.site.contact.application.result.MessageContactResult;
import fr.lacassinauteur.site.contact.domain.model.MessageContact;
import fr.lacassinauteur.site.contact.domain.model.StatutMessage;
import fr.lacassinauteur.site.contact.domain.port.FakeMessageContactRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsulterMessageContactUseCaseTest {

    @Test
    void consulter_marque_un_message_nouveau_comme_lu() {
        FakeMessageContactRepository messageContactRepository = new FakeMessageContactRepository();
        MessageContact message = MessageContact.creer("Camille B.", "camille@exemple.fr", "Objet", "Message");
        messageContactRepository.save(message);

        MessageContactResult result = new ConsulterMessageContactUseCase(messageContactRepository).execute(message.id());

        assertThat(result.statut()).isEqualTo(StatutMessage.LU);
    }

    @Test
    void consulter_un_message_deja_traite_ne_le_retrograde_pas_en_lu() {
        FakeMessageContactRepository messageContactRepository = new FakeMessageContactRepository();
        MessageContact message = MessageContact.creer("Camille B.", "camille@exemple.fr", "Objet", "Message");
        message.marquerTraite();
        messageContactRepository.save(message);

        MessageContactResult result = new ConsulterMessageContactUseCase(messageContactRepository).execute(message.id());

        assertThat(result.statut()).isEqualTo(StatutMessage.TRAITE);
    }
}
