package fr.lacassinauteur.site.contact.infrastructure.persistence.mapper;

import fr.lacassinauteur.site.contact.domain.model.MessageContact;
import fr.lacassinauteur.site.contact.infrastructure.persistence.entity.MessageContactJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class MessageContactEntityMapper {

    public MessageContactJpaEntity versEntite(MessageContact messageContact) {
        return new MessageContactJpaEntity(
                messageContact.id(), messageContact.nom(), messageContact.email(), messageContact.objet(),
                messageContact.message(), messageContact.dateReception(), messageContact.statut());
    }

    public MessageContact versDomaine(MessageContactJpaEntity entite) {
        return new MessageContact(
                entite.getId(), entite.getNom(), entite.getEmail(), entite.getObjet(), entite.getMessage(),
                entite.getDateReception(), entite.getStatut());
    }
}
