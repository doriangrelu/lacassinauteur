package fr.lacassinauteur.site.actualite.application.usecase;

import fr.lacassinauteur.site.actualite.domain.exception.ActualiteIntrouvableException;
import fr.lacassinauteur.site.actualite.domain.model.Actualite;
import fr.lacassinauteur.site.actualite.domain.port.ActualiteRepository;
import fr.lacassinauteur.site.shared.domain.port.StockageFichierPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SupprimerActualiteUseCase {

    private final ActualiteRepository actualiteRepository;
    private final StockageFichierPort stockageFichierPort;

    public SupprimerActualiteUseCase(ActualiteRepository actualiteRepository, StockageFichierPort stockageFichierPort) {
        this.actualiteRepository = actualiteRepository;
        this.stockageFichierPort = stockageFichierPort;
    }

    public void execute(UUID id) {
        Actualite actualite = actualiteRepository.findById(id)
                .orElseThrow(() -> new ActualiteIntrouvableException(id));

        stockageFichierPort.supprimerSiGere(actualite.imageUrl());
        actualiteRepository.supprimer(id);
    }
}
