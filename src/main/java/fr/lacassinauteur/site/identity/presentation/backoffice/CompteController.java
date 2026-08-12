package fr.lacassinauteur.site.identity.presentation.backoffice;

import fr.lacassinauteur.site.identity.application.command.ChangerRoleCommand;
import fr.lacassinauteur.site.identity.application.command.CreerUtilisateurCommand;
import fr.lacassinauteur.site.identity.application.command.DesactiverUtilisateurCommand;
import fr.lacassinauteur.site.identity.application.command.ReactiverUtilisateurCommand;
import fr.lacassinauteur.site.identity.application.usecase.ChangerRoleUtilisateurUseCase;
import fr.lacassinauteur.site.identity.application.usecase.CreerUtilisateurUseCase;
import fr.lacassinauteur.site.identity.application.usecase.DesactiverUtilisateurUseCase;
import fr.lacassinauteur.site.identity.application.usecase.ListerUtilisateursUseCase;
import fr.lacassinauteur.site.identity.application.usecase.ReactiverUtilisateurUseCase;
import fr.lacassinauteur.site.identity.domain.exception.EmailDejaUtiliseException;
import fr.lacassinauteur.site.identity.domain.model.Role;
import fr.lacassinauteur.site.identity.presentation.form.CreerUtilisateurForm;
import fr.lacassinauteur.site.identity.presentation.mapper.UtilisateurViewModelMapper;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class CompteController {

    private final ListerUtilisateursUseCase listerUtilisateursUseCase;
    private final CreerUtilisateurUseCase creerUtilisateurUseCase;
    private final ChangerRoleUtilisateurUseCase changerRoleUtilisateurUseCase;
    private final DesactiverUtilisateurUseCase desactiverUtilisateurUseCase;
    private final ReactiverUtilisateurUseCase reactiverUtilisateurUseCase;
    private final UtilisateurViewModelMapper mapper;

    public CompteController(
            ListerUtilisateursUseCase listerUtilisateursUseCase,
            CreerUtilisateurUseCase creerUtilisateurUseCase,
            ChangerRoleUtilisateurUseCase changerRoleUtilisateurUseCase,
            DesactiverUtilisateurUseCase desactiverUtilisateurUseCase,
            ReactiverUtilisateurUseCase reactiverUtilisateurUseCase,
            UtilisateurViewModelMapper mapper) {
        this.listerUtilisateursUseCase = listerUtilisateursUseCase;
        this.creerUtilisateurUseCase = creerUtilisateurUseCase;
        this.changerRoleUtilisateurUseCase = changerRoleUtilisateurUseCase;
        this.desactiverUtilisateurUseCase = desactiverUtilisateurUseCase;
        this.reactiverUtilisateurUseCase = reactiverUtilisateurUseCase;
        this.mapper = mapper;
    }

    @ModelAttribute("emailConnecte")
    public String emailConnecte(Authentication authentication) {
        return authentication.getName();
    }

    @GetMapping("/backoffice/comptes")
    public String liste(Model model) {
        model.addAttribute("comptes", listerUtilisateursUseCase.execute().stream().map(mapper::versViewModel).toList());
        model.addAttribute("roles", Role.values());
        if (!model.containsAttribute("formulaire")) {
            model.addAttribute("formulaire", new CreerUtilisateurForm());
        }
        return "backoffice/comptes";
    }

    @PostMapping("/backoffice/comptes")
    public String creer(@Valid @ModelAttribute("formulaire") CreerUtilisateurForm formulaire, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return liste(model);
        }

        try {
            creerUtilisateurUseCase.execute(new CreerUtilisateurCommand(
                    formulaire.getEmail(), formulaire.getMotDePasse(), Role.valueOf(formulaire.getRole())));
        } catch (EmailDejaUtiliseException exception) {
            bindingResult.rejectValue("email", "email.dejaUtilise", exception.getMessage());
            return liste(model);
        }

        return "redirect:/backoffice/comptes";
    }

    @PostMapping("/backoffice/comptes/{id}/role")
    public String changerRole(@PathVariable UUID id, @RequestParam Role role) {
        changerRoleUtilisateurUseCase.execute(new ChangerRoleCommand(id, role));
        return "redirect:/backoffice/comptes";
    }

    @PostMapping("/backoffice/comptes/{id}/desactiver")
    public String desactiver(@PathVariable UUID id) {
        desactiverUtilisateurUseCase.execute(new DesactiverUtilisateurCommand(id));
        return "redirect:/backoffice/comptes";
    }

    @PostMapping("/backoffice/comptes/{id}/reactiver")
    public String reactiver(@PathVariable UUID id) {
        reactiverUtilisateurUseCase.execute(new ReactiverUtilisateurCommand(id));
        return "redirect:/backoffice/comptes";
    }
}
