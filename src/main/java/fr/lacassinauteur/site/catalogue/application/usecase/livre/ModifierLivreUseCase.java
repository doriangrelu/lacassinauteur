package fr.lacassinauteur.site.catalogue.application.usecase.livre;

import fr.lacassinauteur.site.catalogue.application.command.ModifierLivreCommand;
import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.domain.exception.LivreIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.model.Livre;
import fr.lacassinauteur.site.catalogue.domain.port.LivreRepository;
import fr.lacassinauteur.site.shared.domain.port.StockageFichierPort;
import org.springframework.stereotype.Component;

@Component
public class ModifierLivreUseCase {

    private static final String SOUS_DOSSIER = "couvertures";

    private final LivreRepository livreRepository;
    private final StockageFichierPort stockageFichierPort;

    public ModifierLivreUseCase(LivreRepository livreRepository, StockageFichierPort stockageFichierPort) {
        this.livreRepository = livreRepository;
        this.stockageFichierPort = stockageFichierPort;
    }

    public LivreResult execute(ModifierLivreCommand command) {
        Livre livre = livreRepository.findById(command.livreId())
                .orElseThrow(() -> new LivreIntrouvableException(command.livreId()));

        String couvertureUrl = livre.couvertureUrl();
        if (command.nouvelleCouvertureContenu() != null && command.nouvelleCouvertureContenu().length > 0) {
            stockageFichierPort.supprimerSiGere(livre.couvertureUrl());
            couvertureUrl = stockageFichierPort.enregistrer(
                    command.nouvelleCouvertureContenu(), command.nouvelleCouvertureNomFichier(), SOUS_DOSSIER);
        }

        livre.modifier(command.collectionId(), command.titre(), command.sousTitre(), couvertureUrl,
                command.pitchCourt(), command.resume(), command.ordre());

        return LivreResult.depuis(livreRepository.save(livre));
    }
}
