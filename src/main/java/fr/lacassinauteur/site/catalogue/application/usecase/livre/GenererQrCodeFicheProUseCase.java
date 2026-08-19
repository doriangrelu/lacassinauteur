package fr.lacassinauteur.site.catalogue.application.usecase.livre;

import fr.lacassinauteur.site.catalogue.application.result.LivreResult;
import fr.lacassinauteur.site.catalogue.domain.exception.LivreIntrouvableException;
import fr.lacassinauteur.site.shared.domain.port.GenerationQrCodePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Produit le QR code menant à la fiche professionnelle d'un livre, destiné à être
 * imprimé sur une plaquette et diffusé par l'auteur aux libraires et éditeurs (cf.
 * ADR-0028). La page visée reste non référencée et non listée dans les menus : le
 * QR code est le seul chemin d'accès prévu, et c'est l'auteur qui le distribue.
 */
@Component
public class GenererQrCodeFicheProUseCase {

    private final ConsulterLivreUseCase consulterLivreUseCase;
    private final GenerationQrCodePort generationQrCodePort;
    private final String urlBase;

    public GenererQrCodeFicheProUseCase(ConsulterLivreUseCase consulterLivreUseCase,
                                         GenerationQrCodePort generationQrCodePort,
                                         @Value("${app.catalogue.url-base}") String urlBase) {
        this.consulterLivreUseCase = consulterLivreUseCase;
        this.generationQrCodePort = generationQrCodePort;
        this.urlBase = urlBase;
    }

    public String execute(UUID livreId) {
        LivreResult livre = consulterLivreUseCase.execute(livreId);

        // Mêmes conditions que PageProfessionnelleController : sans elles, le QR
        // code mènerait à un 404 une fois imprimé — donc irrattrapable.
        if (!livre.disponible() || !livre.ficheProfessionnelleRenseignee()) {
            throw new LivreIntrouvableException(livre.slug());
        }

        return generationQrCodePort.genererSvg(urlFicheProfessionnelle(livre.slug()));
    }

    public String urlFicheProfessionnelle(String slug) {
        return urlBase + "/livres/" + slug + "/pro";
    }
}
