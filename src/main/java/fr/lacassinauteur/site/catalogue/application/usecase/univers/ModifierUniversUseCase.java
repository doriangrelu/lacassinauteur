package fr.lacassinauteur.site.catalogue.application.usecase.univers;

import fr.lacassinauteur.site.catalogue.application.command.ModifierUniversCommand;
import fr.lacassinauteur.site.catalogue.application.result.UniversResult;
import fr.lacassinauteur.site.catalogue.domain.exception.UniversIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.model.Univers;
import fr.lacassinauteur.site.catalogue.domain.port.UniversRepository;
import fr.lacassinauteur.site.shared.domain.port.StockageFichierPort;
import org.springframework.stereotype.Component;

@Component
public class ModifierUniversUseCase {

    private static final String SOUS_DOSSIER = "univers";

    private final UniversRepository universRepository;
    private final StockageFichierPort stockageFichierPort;

    public ModifierUniversUseCase(UniversRepository universRepository, StockageFichierPort stockageFichierPort) {
        this.universRepository = universRepository;
        this.stockageFichierPort = stockageFichierPort;
    }

    public UniversResult execute(ModifierUniversCommand command) {
        Univers univers = universRepository.findById(command.universId())
                .orElseThrow(() -> new UniversIntrouvableException(command.universId()));

        String photoUrl = univers.photoUrl();
        if (command.nouvellePhotoContenu() != null && command.nouvellePhotoContenu().length > 0) {
            stockageFichierPort.supprimerSiGere(univers.photoUrl());
            photoUrl = stockageFichierPort.enregistrer(
                    command.nouvellePhotoContenu(), command.nouvellePhotoNomFichier(), SOUS_DOSSIER);
        }

        univers.modifier(command.nom(), command.sousTitre(), command.texte(), photoUrl, command.ordre());

        return UniversResult.depuis(universRepository.save(univers));
    }
}
