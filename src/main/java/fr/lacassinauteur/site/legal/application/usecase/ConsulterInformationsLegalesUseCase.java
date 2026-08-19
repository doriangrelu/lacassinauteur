package fr.lacassinauteur.site.legal.application.usecase;

import fr.lacassinauteur.site.legal.application.result.InformationsLegalesResult;
import fr.lacassinauteur.site.legal.domain.exception.InformationsLegalesIntrouvablesException;
import fr.lacassinauteur.site.legal.domain.port.InformationsLegalesRepository;
import org.springframework.stereotype.Component;

@Component
public class ConsulterInformationsLegalesUseCase {

    private final InformationsLegalesRepository informationsLegalesRepository;

    public ConsulterInformationsLegalesUseCase(InformationsLegalesRepository informationsLegalesRepository) {
        this.informationsLegalesRepository = informationsLegalesRepository;
    }

    public InformationsLegalesResult execute() {
        return informationsLegalesRepository.charger()
                .map(InformationsLegalesResult::depuis)
                .orElseThrow(InformationsLegalesIntrouvablesException::new);
    }
}
