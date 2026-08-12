package fr.lacassinauteur.site.catalogue.application.usecase.univers;

import fr.lacassinauteur.site.catalogue.application.command.CreerUniversCommand;
import fr.lacassinauteur.site.catalogue.application.result.UniversResult;
import fr.lacassinauteur.site.catalogue.domain.model.Univers;
import fr.lacassinauteur.site.catalogue.domain.port.UniversRepository;
import fr.lacassinauteur.site.shared.domain.port.StockageFichierPort;
import org.springframework.stereotype.Component;

@Component
public class CreerUniversUseCase {

    private static final String SOUS_DOSSIER = "univers";

    private final UniversRepository universRepository;
    private final StockageFichierPort stockageFichierPort;

    public CreerUniversUseCase(UniversRepository universRepository, StockageFichierPort stockageFichierPort) {
        this.universRepository = universRepository;
        this.stockageFichierPort = stockageFichierPort;
    }

    public UniversResult execute(CreerUniversCommand command) {
        String photoUrl = null;
        if (command.photoContenu() != null && command.photoContenu().length > 0) {
            photoUrl = stockageFichierPort.enregistrer(command.photoContenu(), command.photoNomFichier(), SOUS_DOSSIER);
        }

        Univers univers = Univers.creer(command.nom(), command.sousTitre(), command.texte(), photoUrl, command.ordre());
        return UniversResult.depuis(universRepository.save(univers));
    }
}
