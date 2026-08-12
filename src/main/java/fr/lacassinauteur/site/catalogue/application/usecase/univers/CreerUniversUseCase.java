package fr.lacassinauteur.site.catalogue.application.usecase.univers;

import fr.lacassinauteur.site.catalogue.application.command.CreerUniversCommand;
import fr.lacassinauteur.site.catalogue.application.result.UniversResult;
import fr.lacassinauteur.site.catalogue.domain.model.Univers;
import fr.lacassinauteur.site.catalogue.domain.port.UniversRepository;
import org.springframework.stereotype.Component;

@Component
public class CreerUniversUseCase {

    private final UniversRepository universRepository;

    public CreerUniversUseCase(UniversRepository universRepository) {
        this.universRepository = universRepository;
    }

    public UniversResult execute(CreerUniversCommand command) {
        Univers univers = Univers.creer(command.nom(), command.sousTitre(), command.texte(), command.photoUrl(), command.ordre());
        return UniversResult.depuis(universRepository.save(univers));
    }
}
