package fr.lacassinauteur.site.identity.application.usecase;

import fr.lacassinauteur.site.identity.application.command.DesactiverUtilisateurCommand;
import fr.lacassinauteur.site.identity.application.result.UtilisateurResult;
import fr.lacassinauteur.site.identity.domain.exception.UtilisateurIntrouvableException;
import fr.lacassinauteur.site.identity.domain.model.Utilisateur;
import fr.lacassinauteur.site.identity.domain.port.UtilisateurRepository;
import org.springframework.stereotype.Component;

@Component
public class DesactiverUtilisateurUseCase {

    private final UtilisateurRepository utilisateurRepository;

    public DesactiverUtilisateurUseCase(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public UtilisateurResult execute(DesactiverUtilisateurCommand command) {
        Utilisateur utilisateur = utilisateurRepository.findById(command.utilisateurId())
                .orElseThrow(() -> new UtilisateurIntrouvableException(command.utilisateurId()));

        utilisateur.desactiver();

        return UtilisateurResult.depuis(utilisateurRepository.save(utilisateur));
    }
}
