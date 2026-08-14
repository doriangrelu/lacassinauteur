package fr.lacassinauteur.site.catalogue.application.usecase.avis;

import fr.lacassinauteur.site.catalogue.application.command.SoumettreAvisLecteurCommand;
import fr.lacassinauteur.site.catalogue.application.result.AvisLecteurResult;
import fr.lacassinauteur.site.catalogue.domain.exception.LivreIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.model.AvisLecteur;
import fr.lacassinauteur.site.catalogue.domain.port.AvisLecteurRepository;
import fr.lacassinauteur.site.catalogue.domain.port.LivreRepository;
import org.springframework.stereotype.Component;

/**
 * Soumission publique d'un avis lecteur : toujours créé en statut
 * {@code EN_ATTENTE}, modéré ensuite depuis le back-office (cf.
 * {@link ApprouverAvisLecteurUseCase} / {@link RejeterAvisLecteurUseCase}).
 */
@Component
public class SoumettreAvisLecteurUseCase {

    private final AvisLecteurRepository avisLecteurRepository;
    private final LivreRepository livreRepository;

    public SoumettreAvisLecteurUseCase(AvisLecteurRepository avisLecteurRepository, LivreRepository livreRepository) {
        this.avisLecteurRepository = avisLecteurRepository;
        this.livreRepository = livreRepository;
    }

    public AvisLecteurResult execute(SoumettreAvisLecteurCommand command) {
        livreRepository.findById(command.livreId())
                .orElseThrow(() -> new LivreIntrouvableException(command.livreId()));

        AvisLecteur avisLecteur = AvisLecteur.soumettre(
                command.livreId(), command.nomAuteurAvis(), command.texte(), command.note());

        return AvisLecteurResult.depuis(avisLecteurRepository.save(avisLecteur));
    }
}
