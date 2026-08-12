package fr.lacassinauteur.site.identity.application.usecase;

import fr.lacassinauteur.site.identity.application.result.UtilisateurResult;
import fr.lacassinauteur.site.identity.domain.port.UtilisateurRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListerUtilisateursUseCase {

    private final UtilisateurRepository utilisateurRepository;

    public ListerUtilisateursUseCase(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<UtilisateurResult> execute() {
        return utilisateurRepository.findAll().stream()
                .map(UtilisateurResult::depuis)
                .toList();
    }
}
