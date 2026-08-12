package fr.lacassinauteur.site.catalogue.application.usecase.livre;

import fr.lacassinauteur.site.catalogue.application.command.PublierLivreCommand;
import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.domain.exception.LivreIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.model.LienAchat;
import fr.lacassinauteur.site.catalogue.domain.model.Livre;
import fr.lacassinauteur.site.catalogue.domain.port.LivreRepository;
import org.springframework.stereotype.Component;

@Component
public class PublierLivreUseCase {

    private final LivreRepository livreRepository;

    public PublierLivreUseCase(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    public LivreResult execute(PublierLivreCommand command) {
        Livre livre = livreRepository.findById(command.livreId())
                .orElseThrow(() -> new LivreIntrouvableException(command.livreId()));

        livre.publier(new LienAchat(command.url(), command.libelleMarchand()));

        return LivreResult.depuis(livreRepository.save(livre));
    }
}
