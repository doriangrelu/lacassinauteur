package fr.lacassinauteur.site.identity.infrastructure.seed;

import fr.lacassinauteur.site.identity.application.command.CreerUtilisateurCommand;
import fr.lacassinauteur.site.identity.application.usecase.CreerUtilisateurUseCase;
import fr.lacassinauteur.site.identity.domain.model.Email;
import fr.lacassinauteur.site.identity.domain.model.Role;
import fr.lacassinauteur.site.identity.domain.port.UtilisateurRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Crée un compte ADMIN par défaut en développement local, pour ne pas dépendre d'un
 * seed manuel. Ne tourne jamais en profil prod (cf. application-dev.yml).
 */
@Component
@Profile("dev")
public class DevUtilisateurSeeder implements ApplicationRunner {

    private static final String EMAIL_DEV = "admin@lacassinauteur.local";
    private static final String MOT_DE_PASSE_DEV = "admin123";

    private final UtilisateurRepository utilisateurRepository;
    private final CreerUtilisateurUseCase creerUtilisateurUseCase;

    public DevUtilisateurSeeder(UtilisateurRepository utilisateurRepository, CreerUtilisateurUseCase creerUtilisateurUseCase) {
        this.utilisateurRepository = utilisateurRepository;
        this.creerUtilisateurUseCase = creerUtilisateurUseCase;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!utilisateurRepository.existsByEmail(new Email(EMAIL_DEV))) {
            creerUtilisateurUseCase.execute(new CreerUtilisateurCommand(EMAIL_DEV, MOT_DE_PASSE_DEV, Role.ADMIN));
        }
    }
}
