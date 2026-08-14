package fr.lacassinauteur.site.catalogue.application.usecase.avis;

import fr.lacassinauteur.site.catalogue.application.result.AvisLecteurResult;
import fr.lacassinauteur.site.catalogue.domain.exception.AvisLecteurIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.model.AvisLecteur;
import fr.lacassinauteur.site.catalogue.domain.port.AvisLecteurRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ApprouverAvisLecteurUseCase {

    private final AvisLecteurRepository avisLecteurRepository;

    public ApprouverAvisLecteurUseCase(AvisLecteurRepository avisLecteurRepository) {
        this.avisLecteurRepository = avisLecteurRepository;
    }

    public AvisLecteurResult execute(UUID id) {
        AvisLecteur avisLecteur = avisLecteurRepository.findById(id)
                .orElseThrow(() -> new AvisLecteurIntrouvableException(id));

        avisLecteur.approuver();

        return AvisLecteurResult.depuis(avisLecteurRepository.save(avisLecteur));
    }
}
