package fr.lacassinauteur.site.catalogue.application.usecase.livre;

import fr.lacassinauteur.site.catalogue.application.command.CreerLivreCommand;
import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.domain.model.Livre;
import fr.lacassinauteur.site.catalogue.domain.port.LivreRepository;
import fr.lacassinauteur.site.shared.domain.port.StockageFichierPort;
import org.springframework.stereotype.Component;

@Component
public class CreerLivreUseCase {

    private static final String SOUS_DOSSIER = "couvertures";

    private final LivreRepository livreRepository;
    private final StockageFichierPort stockageFichierPort;

    public CreerLivreUseCase(LivreRepository livreRepository, StockageFichierPort stockageFichierPort) {
        this.livreRepository = livreRepository;
        this.stockageFichierPort = stockageFichierPort;
    }

    public LivreResult execute(CreerLivreCommand command) {
        String couvertureUrl = null;
        if (command.couvertureContenu() != null && command.couvertureContenu().length > 0) {
            couvertureUrl = stockageFichierPort.enregistrer(
                    command.couvertureContenu(), command.couvertureNomFichier(), SOUS_DOSSIER);
        }

        Livre livre = Livre.creer(
                command.collectionId(), command.titre(), command.sousTitre(), couvertureUrl,
                command.pitchCourt(), command.resume(), command.ordre());
        return LivreResult.depuis(livreRepository.save(livre));
    }
}
