package fr.lacassinauteur.site.identity.application.usecase;

import fr.lacassinauteur.site.identity.application.command.ChangerRoleCommand;
import fr.lacassinauteur.site.identity.application.result.UtilisateurResult;
import fr.lacassinauteur.site.identity.domain.exception.UtilisateurIntrouvableException;
import fr.lacassinauteur.site.identity.domain.model.Utilisateur;
import fr.lacassinauteur.site.identity.domain.port.UtilisateurRepository;
import org.springframework.stereotype.Component;

@Component
public class ChangerRoleUtilisateurUseCase {

    private final UtilisateurRepository utilisateurRepository;

    public ChangerRoleUtilisateurUseCase(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public UtilisateurResult execute(ChangerRoleCommand command) {
        Utilisateur utilisateur = utilisateurRepository.findById(command.utilisateurId())
                .orElseThrow(() -> new UtilisateurIntrouvableException(command.utilisateurId()));

        utilisateur.changerRole(command.nouveauRole());

        return UtilisateurResult.depuis(utilisateurRepository.save(utilisateur));
    }
}
