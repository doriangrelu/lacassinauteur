package fr.lacassinauteur.site.biographie.application.usecase;

import fr.lacassinauteur.site.biographie.application.result.BiographieResult;
import fr.lacassinauteur.site.biographie.domain.exception.BiographieIntrouvableException;
import fr.lacassinauteur.site.biographie.domain.port.BiographieRepository;
import org.springframework.stereotype.Component;

@Component
public class ConsulterBiographieUseCase {

    private final BiographieRepository biographieRepository;

    public ConsulterBiographieUseCase(BiographieRepository biographieRepository) {
        this.biographieRepository = biographieRepository;
    }

    public BiographieResult execute() {
        return biographieRepository.charger()
                .map(BiographieResult::depuis)
                .orElseThrow(BiographieIntrouvableException::new);
    }
}
