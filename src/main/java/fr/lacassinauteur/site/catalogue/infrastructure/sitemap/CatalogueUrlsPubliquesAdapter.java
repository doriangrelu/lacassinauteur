package fr.lacassinauteur.site.catalogue.infrastructure.sitemap;

import fr.lacassinauteur.site.catalogue.application.result.CollectionResult;
import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.application.result.UniversResult;
import fr.lacassinauteur.site.catalogue.application.usecase.collection.ListerToutesLesCollectionsUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.livre.ListerTousLesLivresUseCase;
import fr.lacassinauteur.site.catalogue.application.usecase.univers.ListerUniversUseCase;
import fr.lacassinauteur.site.shared.domain.port.FournisseurUrlsPubliquesPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Contribue au sitemap les pages du catalogue (univers, collections, livres).
 *
 * <p>Les fiches professionnelles {@code /livres/{slug}/pro} sont
 * <strong>volontairement absentes</strong> : elles sont en {@code noindex} et
 * diffusées uniquement par l'auteur via un QR code (cf. ADR-0028). Les lister ici
 * reviendrait à demander leur indexation, exactement l'inverse de l'intention.
 */
@Component
public class CatalogueUrlsPubliquesAdapter implements FournisseurUrlsPubliquesPort {

    private final ListerUniversUseCase listerUniversUseCase;
    private final ListerToutesLesCollectionsUseCase listerToutesLesCollectionsUseCase;
    private final ListerTousLesLivresUseCase listerTousLesLivresUseCase;

    public CatalogueUrlsPubliquesAdapter(ListerUniversUseCase listerUniversUseCase,
                                          ListerToutesLesCollectionsUseCase listerToutesLesCollectionsUseCase,
                                          ListerTousLesLivresUseCase listerTousLesLivresUseCase) {
        this.listerUniversUseCase = listerUniversUseCase;
        this.listerToutesLesCollectionsUseCase = listerToutesLesCollectionsUseCase;
        this.listerTousLesLivresUseCase = listerTousLesLivresUseCase;
    }

    @Override
    public List<String> urlsPubliques() {
        List<String> urls = new ArrayList<>();

        for (UniversResult univers : listerUniversUseCase.execute()) {
            urls.add("/univers/" + univers.slug());
        }
        for (CollectionResult collection : listerToutesLesCollectionsUseCase.execute()) {
            urls.add("/collections/" + collection.slug());
        }
        for (LivreResult livre : listerTousLesLivresUseCase.execute()) {
            urls.add("/livres/" + livre.slug());
        }

        return urls;
    }
}
