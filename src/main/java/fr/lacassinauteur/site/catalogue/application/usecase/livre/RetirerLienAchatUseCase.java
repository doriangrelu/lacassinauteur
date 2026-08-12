package fr.lacassinauteur.site.catalogue.application.usecase.livre;

import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.domain.exception.LivreIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.model.Livre;
import fr.lacassinauteur.site.catalogue.domain.port.LivreRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RetirerLienAchatUseCase {

    private final LivreRepository livreRepository;

    public RetirerLienAchatUseCase(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    public LivreResult execute(UUID livreId) {
        Livre livre = livreRepository.findById(livreId)
                .orElseThrow(() -> new LivreIntrouvableException(livreId));

        livre.retirerLienAchat();

        return LivreResult.depuis(livreRepository.save(livre));
    }
}
