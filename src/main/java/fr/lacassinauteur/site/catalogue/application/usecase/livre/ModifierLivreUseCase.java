package fr.lacassinauteur.site.catalogue.application.usecase.livre;

import fr.lacassinauteur.site.catalogue.application.command.ModifierLivreCommand;
import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.domain.exception.LivreIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.model.FicheProfessionnelle;
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

        if (ficheProfessionnelleRenseignee(command)) {
            livre.renseignerFicheProfessionnelle(new FicheProfessionnelle(
                    command.isbn(), command.format(), command.nombrePages(), command.prix(),
                    command.lieuxDistribution(), command.pitchEditeur(), command.synopsisEditeur()));
        } else {
            livre.retirerFicheProfessionnelle();
        }

        return LivreResult.depuis(livreRepository.save(livre));
    }

    private boolean ficheProfessionnelleRenseignee(ModifierLivreCommand command) {
        return estRenseigne(command.isbn()) || estRenseigne(command.format()) || command.nombrePages() != null
                || command.prix() != null || estRenseigne(command.lieuxDistribution())
                || estRenseigne(command.pitchEditeur()) || estRenseigne(command.synopsisEditeur());
    }

    private boolean estRenseigne(String valeur) {
        return valeur != null && !valeur.isBlank();
    }
}
