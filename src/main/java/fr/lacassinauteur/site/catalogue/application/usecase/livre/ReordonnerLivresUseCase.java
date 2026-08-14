package fr.lacassinauteur.site.catalogue.application.usecase.livre;

import fr.lacassinauteur.site.catalogue.domain.exception.LivreIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.model.Livre;
import fr.lacassinauteur.site.catalogue.domain.port.LivreRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ReordonnerLivresUseCase {

    private final LivreRepository livreRepository;

    public ReordonnerLivresUseCase(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    public void execute(List<UUID> idsOrdonnes) {
        for (int index = 0; index < idsOrdonnes.size(); index++) {
            UUID id = idsOrdonnes.get(index);
            Livre livre = livreRepository.findById(id).orElseThrow(() -> new LivreIntrouvableException(id));
            livre.changerOrdre(index + 1);
            livreRepository.save(livre);
        }
    }
}
