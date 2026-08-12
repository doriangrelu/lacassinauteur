package fr.lacassinauteur.site.catalogue.application.usecase.livre;

import fr.lacassinauteur.site.catalogue.application.command.DefinirDerniereParutionCommand;
import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.domain.exception.LivreIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.model.Livre;
import fr.lacassinauteur.site.catalogue.domain.port.LivreRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DefinirDerniereParutionUseCase {

    private final LivreRepository livreRepository;

    public DefinirDerniereParutionUseCase(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    @Transactional
    public LivreResult execute(DefinirDerniereParutionCommand command) {
        livreRepository.findDerniereParution().ifPresent(actuel -> {
            actuel.retirerDerniereParution();
            livreRepository.save(actuel);
        });

        Livre nouveau = livreRepository.findById(command.livreId())
                .orElseThrow(() -> new LivreIntrouvableException(command.livreId()));
        nouveau.marquerCommeDerniereParution();

        return LivreResult.depuis(livreRepository.save(nouveau));
    }
}
