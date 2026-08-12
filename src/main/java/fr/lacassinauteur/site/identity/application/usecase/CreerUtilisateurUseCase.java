package fr.lacassinauteur.site.identity.application.usecase;

import fr.lacassinauteur.site.identity.application.command.CreerUtilisateurCommand;
import fr.lacassinauteur.site.identity.application.result.UtilisateurResult;
import fr.lacassinauteur.site.identity.domain.exception.EmailDejaUtiliseException;
import fr.lacassinauteur.site.identity.domain.model.Email;
import fr.lacassinauteur.site.identity.domain.model.MotDePasseHache;
import fr.lacassinauteur.site.identity.domain.model.Utilisateur;
import fr.lacassinauteur.site.identity.domain.port.HacheurMotDePasse;
import fr.lacassinauteur.site.identity.domain.port.UtilisateurRepository;
import org.springframework.stereotype.Component;

@Component
public class CreerUtilisateurUseCase {

    private final UtilisateurRepository utilisateurRepository;
    private final HacheurMotDePasse hacheurMotDePasse;

    public CreerUtilisateurUseCase(UtilisateurRepository utilisateurRepository, HacheurMotDePasse hacheurMotDePasse) {
        this.utilisateurRepository = utilisateurRepository;
        this.hacheurMotDePasse = hacheurMotDePasse;
    }

    public UtilisateurResult execute(CreerUtilisateurCommand command) {
        Email email = new Email(command.email());
        if (utilisateurRepository.existsByEmail(email)) {
            throw new EmailDejaUtiliseException(email);
        }

        MotDePasseHache motDePasseHache = hacheurMotDePasse.hacher(command.motDePasseClair());
        Utilisateur utilisateur = Utilisateur.creer(email, motDePasseHache, command.role());

        return UtilisateurResult.depuis(utilisateurRepository.save(utilisateur));
    }
}
