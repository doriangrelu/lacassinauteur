package fr.lacassinauteur.site.contact.application.result;

import fr.lacassinauteur.site.contact.domain.model.MessageContact;
import fr.lacassinauteur.site.contact.domain.model.StatutMessage;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageContactResult(UUID id, String nom, String email, String objet, String message,
                                    LocalDateTime dateReception, StatutMessage statut) {

    public static MessageContactResult depuis(MessageContact messageContact) {
        return new MessageContactResult(
                messageContact.id(), messageContact.nom(), messageContact.email(), messageContact.objet(),
                messageContact.message(), messageContact.dateReception(), messageContact.statut());
    }
}
