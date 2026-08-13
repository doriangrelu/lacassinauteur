package fr.lacassinauteur.site.contact.presentation.mapper;

import fr.lacassinauteur.site.contact.application.result.MessageContactResult;
import fr.lacassinauteur.site.contact.presentation.viewmodel.MessageContactViewModel;
import org.springframework.stereotype.Component;

@Component
public class MessageContactViewModelMapper {

    public MessageContactViewModel versViewModel(MessageContactResult result) {
        return new MessageContactViewModel(
                result.id(), result.nom(), result.email(), result.objet(), result.message(),
                result.dateReception(), result.statut());
    }
}
