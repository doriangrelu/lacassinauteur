package fr.lacassinauteur.site.contact.presentation.backoffice;

import fr.lacassinauteur.site.contact.application.usecase.ConsulterMessageContactUseCase;
import fr.lacassinauteur.site.contact.application.usecase.ListerMessagesContactUseCase;
import fr.lacassinauteur.site.contact.application.usecase.MarquerMessageTraiteUseCase;
import fr.lacassinauteur.site.contact.presentation.mapper.MessageContactViewModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

/**
 * Messages de contact reçus via le formulaire public : liste (pas de création
 * manuelle, ce sont des messages entrants), consultation (marque automatiquement
 * comme lu) et marquage traité.
 */
@Controller
@RequestMapping("/backoffice/messages")
public class BackofficeMessageContactController {

    private final ListerMessagesContactUseCase listerMessagesContactUseCase;
    private final ConsulterMessageContactUseCase consulterMessageContactUseCase;
    private final MarquerMessageTraiteUseCase marquerMessageTraiteUseCase;
    private final MessageContactViewModelMapper mapper;

    public BackofficeMessageContactController(
            ListerMessagesContactUseCase listerMessagesContactUseCase,
            ConsulterMessageContactUseCase consulterMessageContactUseCase,
            MarquerMessageTraiteUseCase marquerMessageTraiteUseCase,
            MessageContactViewModelMapper mapper) {
        this.listerMessagesContactUseCase = listerMessagesContactUseCase;
        this.consulterMessageContactUseCase = consulterMessageContactUseCase;
        this.marquerMessageTraiteUseCase = marquerMessageTraiteUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("messages", listerMessagesContactUseCase.execute().stream().map(mapper::versViewModel).toList());
        return "backoffice/contact/message-liste";
    }

    @GetMapping("/{id}")
    public String consulter(@PathVariable UUID id, Model model) {
        model.addAttribute("message", mapper.versViewModel(consulterMessageContactUseCase.execute(id)));
        return "backoffice/contact/message-detail";
    }

    @PostMapping("/{id}/traiter")
    public String traiter(@PathVariable UUID id) {
        marquerMessageTraiteUseCase.execute(id);
        return "redirect:/backoffice/messages/" + id;
    }
}
