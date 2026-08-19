package fr.lacassinauteur.site.biographie.application.usecase;

import fr.lacassinauteur.site.biographie.application.command.ModifierBiographieCommand;
import fr.lacassinauteur.site.biographie.application.result.BiographieResult;
import fr.lacassinauteur.site.biographie.domain.exception.BiographieIntrouvableException;
import fr.lacassinauteur.site.biographie.domain.model.Biographie;
import fr.lacassinauteur.site.biographie.domain.port.BiographieRepository;
import fr.lacassinauteur.site.shared.domain.port.StockageFichierPort;
import org.springframework.stereotype.Component;

@Component
public class ModifierBiographieUseCase {

    private static final String SOUS_DOSSIER = "auteur";

    private final BiographieRepository biographieRepository;
    private final StockageFichierPort stockageFichierPort;

    public ModifierBiographieUseCase(BiographieRepository biographieRepository,
                                      StockageFichierPort stockageFichierPort) {
        this.biographieRepository = biographieRepository;
        this.stockageFichierPort = stockageFichierPort;
    }

    public BiographieResult execute(ModifierBiographieCommand command) {
        Biographie biographie = biographieRepository.charger()
                .orElseThrow(BiographieIntrouvableException::new);

        String photoUrl = biographie.photoUrl();
        if (command.nouvellePhotoContenu() != null && command.nouvellePhotoContenu().length > 0) {
            // Même séquence que ModifierUniversUseCase : on ne supprime l'ancienne
            // que si elle est gérée par le stockage (la photo initiale du seeder est
            // une ressource statique du jar, que supprimerSiGere laisse tranquille).
            stockageFichierPort.supprimerSiGere(biographie.photoUrl());
            photoUrl = stockageFichierPort.enregistrer(
                    command.nouvellePhotoContenu(), command.nouvellePhotoNomFichier(), SOUS_DOSSIER);
        }

        biographie.modifier(command.texte(), photoUrl);

        return BiographieResult.depuis(biographieRepository.save(biographie));
    }
}
