package fr.lacassinauteur.site.catalogue.application.usecase.univers;

import fr.lacassinauteur.site.catalogue.domain.exception.UniversIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.model.Univers;
import fr.lacassinauteur.site.catalogue.domain.port.UniversRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ReordonnerUniversUseCase {

    private final UniversRepository universRepository;

    public ReordonnerUniversUseCase(UniversRepository universRepository) {
        this.universRepository = universRepository;
    }

    public void execute(List<UUID> idsOrdonnes) {
        for (int index = 0; index < idsOrdonnes.size(); index++) {
            UUID id = idsOrdonnes.get(index);
            Univers univers = universRepository.findById(id).orElseThrow(() -> new UniversIntrouvableException(id));
            univers.changerOrdre(index + 1);
            universRepository.save(univers);
        }
    }
}
