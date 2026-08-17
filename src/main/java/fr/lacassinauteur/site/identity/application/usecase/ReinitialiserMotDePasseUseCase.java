package fr.lacassinauteur.site.identity.application.usecase;

import fr.lacassinauteur.site.identity.application.command.ReinitialiserMotDePasseCommand;
import fr.lacassinauteur.site.identity.domain.exception.JetonReinitialisationInvalideException;
import fr.lacassinauteur.site.identity.domain.exception.UtilisateurIntrouvableException;
import fr.lacassinauteur.site.identity.domain.model.JetonReinitialisation;
import fr.lacassinauteur.site.identity.domain.model.MotDePasseHache;
import fr.lacassinauteur.site.identity.domain.model.Utilisateur;
import fr.lacassinauteur.site.identity.domain.port.HacheurMotDePasse;
import fr.lacassinauteur.site.identity.domain.port.JetonReinitialisationMotDePassePort;
import fr.lacassinauteur.site.identity.domain.port.JetonReinitialisationRepository;
import fr.lacassinauteur.site.identity.domain.port.UtilisateurRepository;
import org.springframework.stereotype.Component;

/**
 * Applique un nouveau mot de passe à partir d'un jeton de réinitialisation. Le
 * jeton est dé-référencé (supprimé de la table dédiée) dès qu'il est utilisé
 * avec succès, le rendant définitivement inutilisable même s'il n'a pas encore
 * atteint son expiration cryptographique — cf. ADR-0020.
 */
@Component
public class ReinitialiserMotDePasseUseCase {

    private final UtilisateurRepository utilisateurRepository;
    private final JetonReinitialisationRepository jetonReinitialisationRepository;
    private final JetonReinitialisationMotDePassePort jetonPort;
    private final HacheurMotDePasse hacheurMotDePasse;

    public ReinitialiserMotDePasseUseCase(
            UtilisateurRepository utilisateurRepository,
            JetonReinitialisationRepository jetonReinitialisationRepository,
            JetonReinitialisationMotDePassePort jetonPort,
            HacheurMotDePasse hacheurMotDePasse) {
        this.utilisateurRepository = utilisateurRepository;
        this.jetonReinitialisationRepository = jetonReinitialisationRepository;
        this.jetonPort = jetonPort;
        this.hacheurMotDePasse = hacheurMotDePasse;
    }

    public void execute(ReinitialiserMotDePasseCommand command) {
        JetonReinitialisationMotDePassePort.JetonDecode decode = jetonPort.validerEtExtraire(command.jeton());

        JetonReinitialisation jetonEnregistre = jetonReinitialisationRepository.findById(decode.jetonId())
                .filter(jeton -> jeton.utilisateurId().equals(decode.utilisateurId()))
                .filter(jeton -> !jeton.expire())
                .orElseThrow(() -> new JetonReinitialisationInvalideException("Jeton de réinitialisation invalide, expiré ou déjà utilisé"));

        Utilisateur utilisateur = utilisateurRepository.findById(jetonEnregistre.utilisateurId())
                .orElseThrow(() -> new UtilisateurIntrouvableException(jetonEnregistre.utilisateurId()));

        MotDePasseHache nouveauMotDePasseHache = hacheurMotDePasse.hacher(command.nouveauMotDePasseClair());
        utilisateur.changerMotDePasse(nouveauMotDePasseHache);
        utilisateurRepository.save(utilisateur);

        jetonReinitialisationRepository.deleteById(jetonEnregistre.id());
    }
}
