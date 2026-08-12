package fr.lacassinauteur.site.actualite.application.usecase;

import fr.lacassinauteur.site.actualite.application.command.CreerActualiteCommand;
import fr.lacassinauteur.site.actualite.application.result.ActualiteResult;
import fr.lacassinauteur.site.actualite.domain.model.Actualite;
import fr.lacassinauteur.site.actualite.domain.port.ActualiteRepository;
import fr.lacassinauteur.site.shared.domain.port.StockageFichierPort;
import org.springframework.stereotype.Component;

@Component
public class CreerActualiteUseCase {

    private static final String SOUS_DOSSIER = "actualites";

    private final ActualiteRepository actualiteRepository;
    private final StockageFichierPort stockageFichierPort;

    public CreerActualiteUseCase(ActualiteRepository actualiteRepository, StockageFichierPort stockageFichierPort) {
        this.actualiteRepository = actualiteRepository;
        this.stockageFichierPort = stockageFichierPort;
    }

    public ActualiteResult execute(CreerActualiteCommand command) {
        String imageUrl = null;
        if (command.imageContenu() != null && command.imageContenu().length > 0) {
            imageUrl = stockageFichierPort.enregistrer(command.imageContenu(), command.imageNomFichier(), SOUS_DOSSIER);
        }

        Actualite actualite = Actualite.creer(
                command.titre(), command.texte(), command.date(), command.lieu(), command.lienBilletterie(),
                imageUrl, command.archiveeManuellement(), command.misEnAvant());

        return ActualiteResult.depuis(actualiteRepository.save(actualite));
    }
}
