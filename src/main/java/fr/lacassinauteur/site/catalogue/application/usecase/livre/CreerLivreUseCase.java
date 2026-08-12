package fr.lacassinauteur.site.catalogue.application.usecase.livre;

import fr.lacassinauteur.site.catalogue.application.command.CreerLivreCommand;
import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.domain.model.Livre;
import fr.lacassinauteur.site.catalogue.domain.port.LivreRepository;
import org.springframework.stereotype.Component;

@Component
public class CreerLivreUseCase {

    private final LivreRepository livreRepository;

    public CreerLivreUseCase(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    public LivreResult execute(CreerLivreCommand command) {
        Livre livre = Livre.creer(
                command.collectionId(), command.titre(), command.sousTitre(), command.couvertureUrl(),
                command.pitchCourt(), command.resume(), command.ordre());
        return LivreResult.depuis(livreRepository.save(livre));
    }
}
