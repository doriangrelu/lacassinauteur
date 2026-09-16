package fr.lacassinauteur.site.actualite.application.usecase;

import fr.lacassinauteur.site.actualite.application.command.ModifierActualiteCommand;
import fr.lacassinauteur.site.actualite.application.result.ActualiteResult;
import fr.lacassinauteur.site.actualite.domain.exception.ActualiteIntrouvableException;
import fr.lacassinauteur.site.actualite.domain.model.Actualite;
import fr.lacassinauteur.site.actualite.domain.model.PhotoLegendee;
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

        String photoComplementaireUrl = actualite.photoComplementaire().map(PhotoLegendee::url).orElse(null);
        if (command.nouvellePhotoComplementaireContenu() != null && command.nouvellePhotoComplementaireContenu().length > 0) {
            actualite.photoComplementaire().ifPresent(photo -> stockageFichierPort.supprimerSiGere(photo.url()));
            photoComplementaireUrl = stockageFichierPort.enregistrer(
                    command.nouvellePhotoComplementaireContenu(), command.nouvellePhotoComplementaireNomFichier(), SOUS_DOSSIER);
        }
        PhotoLegendee photoComplementaire = photoComplementaireUrl == null
                ? null
                : new PhotoLegendee(photoComplementaireUrl, command.photoComplementaireLegende());

        actualite.modifier(
                command.titre(), command.texte(), command.date(), command.lieu(), command.lienBilletterie(),
                imageUrl, photoComplementaire, command.archiveeManuellement(), command.misEnAvant());

        return ActualiteResult.depuis(actualiteRepository.save(actualite));
    }
}
