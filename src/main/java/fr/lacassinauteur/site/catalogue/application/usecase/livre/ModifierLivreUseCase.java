package fr.lacassinauteur.site.catalogue.application.usecase.livre;

import fr.lacassinauteur.site.catalogue.application.command.ModifierLivreCommand;
import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.domain.exception.LivreIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.model.Livre;
import fr.lacassinauteur.site.catalogue.domain.port.LivreRepository;
import org.springframework.stereotype.Component;

@Component
public class ModifierLivreUseCase {

    private final LivreRepository livreRepository;

    public ModifierLivreUseCase(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    public LivreResult execute(ModifierLivreCommand command) {
        Livre livre = livreRepository.findById(command.livreId())
                .orElseThrow(() -> new LivreIntrouvableException(command.livreId()));

        livre.modifier(command.collectionId(), command.titre(), command.sousTitre(), command.couvertureUrl(),
                command.pitchCourt(), command.resume(), command.ordre());

        return LivreResult.depuis(livreRepository.save(livre));
    }
}
