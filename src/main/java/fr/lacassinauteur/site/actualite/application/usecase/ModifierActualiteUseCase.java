package fr.lacassinauteur.site.actualite.application.usecase;

import fr.lacassinauteur.site.actualite.application.command.ModifierActualiteCommand;
import fr.lacassinauteur.site.actualite.application.result.ActualiteResult;
import fr.lacassinauteur.site.actualite.domain.exception.ActualiteIntrouvableException;
import fr.lacassinauteur.site.actualite.domain.model.Actualite;
import fr.lacassinauteur.site.actualite.domain.port.ActualiteRepository;
import fr.lacassinauteur.site.shared.domain.port.StockageFichierPort;
import org.springframework.stereotype.Component;

@Component
public class ModifierActualiteUseCase {

    private static final String SOUS_DOSSIER = "actualites";

    private final ActualiteRepository actualiteRepository;
    private final StockageFichierPort stockageFichierPort;

    public ModifierActualiteUseCase(ActualiteRepository actualiteRepository, StockageFichierPort stockageFichierPort) {
        this.actualiteRepository = actualiteRepository;
        this.stockageFichierPort = stockageFichierPort;
    }

    public ActualiteResult execute(ModifierActualiteCommand command) {
        Actualite actualite = actualiteRepository.findById(command.actualiteId())
                .orElseThrow(() -> new ActualiteIntrouvableException(command.actualiteId()));

        String imageUrl = actualite.imageUrl();
        if (command.nouvelleImageContenu() != null && command.nouvelleImageContenu().length > 0) {
            stockageFichierPort.supprimerSiGere(actualite.imageUrl());
            imageUrl = stockageFichierPort.enregistrer(
                    command.nouvelleImageContenu(), command.nouvelleImageNomFichier(), SOUS_DOSSIER);
        }

        actualite.modifier(
                command.titre(), command.texte(), command.date(), command.lieu(), command.lienBilletterie(),
                imageUrl, command.archiveeManuellement(), command.misEnAvant());

        return ActualiteResult.depuis(actualiteRepository.save(actualite));
    }
}
