package fr.lacassinauteur.site.catalogue.application.usecase.univers;

import fr.lacassinauteur.site.catalogue.application.command.ModifierUniversCommand;
import fr.lacassinauteur.site.catalogue.application.result.UniversResult;
import fr.lacassinauteur.site.catalogue.domain.exception.UniversIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.model.Univers;
import fr.lacassinauteur.site.catalogue.domain.port.UniversRepository;
import org.springframework.stereotype.Component;

@Component
public class ModifierUniversUseCase {

    private final UniversRepository universRepository;

    public ModifierUniversUseCase(UniversRepository universRepository) {
        this.universRepository = universRepository;
    }

    public UniversResult execute(ModifierUniversCommand command) {
        Univers univers = universRepository.findById(command.universId())
                .orElseThrow(() -> new UniversIntrouvableException(command.universId()));

        univers.modifier(command.nom(), command.sousTitre(), command.texte(), command.photoUrl(), command.ordre());

        return UniversResult.depuis(universRepository.save(univers));
    }
}
