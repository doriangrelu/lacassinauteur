package fr.lacassinauteur.site.identity.application.usecase;

import fr.lacassinauteur.site.identity.application.command.ReinitialiserMotDePasseCommand;
import fr.lacassinauteur.site.identity.domain.exception.UtilisateurIntrouvableException;
import fr.lacassinauteur.site.identity.domain.model.MotDePasseHache;
import fr.lacassinauteur.site.identity.domain.model.Utilisateur;
import fr.lacassinauteur.site.identity.domain.port.HacheurMotDePasse;
import fr.lacassinauteur.site.identity.domain.port.JetonReinitialisationMotDePassePort;
import fr.lacassinauteur.site.identity.domain.port.UtilisateurRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReinitialiserMotDePasseUseCase {

    private final UtilisateurRepository utilisateurRepository;
    private final JetonReinitialisationMotDePassePort jetonPort;
    private final HacheurMotDePasse hacheurMotDePasse;

    public ReinitialiserMotDePasseUseCase(
            UtilisateurRepository utilisateurRepository,
            JetonReinitialisationMotDePassePort jetonPort,
            HacheurMotDePasse hacheurMotDePasse) {
        this.utilisateurRepository = utilisateurRepository;
        this.jetonPort = jetonPort;
        this.hacheurMotDePasse = hacheurMotDePasse;
    }

    public void execute(ReinitialiserMotDePasseCommand command) {
        UUID utilisateurId = jetonPort.validerEtExtraireUtilisateurId(command.jeton());

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new UtilisateurIntrouvableException(utilisateurId));

        MotDePasseHache nouveauMotDePasseHache = hacheurMotDePasse.hacher(command.nouveauMotDePasseClair());
        utilisateur.changerMotDePasse(nouveauMotDePasseHache);
        utilisateurRepository.save(utilisateur);
    }
}
