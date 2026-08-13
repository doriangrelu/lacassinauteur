package fr.lacassinauteur.site.contact.domain.port;

import fr.lacassinauteur.site.contact.domain.model.MessageContact;

import java.util.ArrayList;
import java.util.List;

public class FakeEnvoiEmailContactPort implements EnvoiEmailContactPort {

    public final List<MessageContact> notificationsEnvoyees = new ArrayList<>();

    @Override
    public void envoyerNotification(MessageContact message) {
        notificationsEnvoyees.add(message);
    }
}
