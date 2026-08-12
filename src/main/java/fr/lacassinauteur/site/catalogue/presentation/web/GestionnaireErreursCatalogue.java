package fr.lacassinauteur.site.catalogue.presentation.web;

import fr.lacassinauteur.site.catalogue.domain.exception.CollectionIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.exception.LivreIntrouvableException;
import fr.lacassinauteur.site.catalogue.domain.exception.UniversIntrouvableException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Traduit les exceptions « introuvable » du catalogue (slug/URL invalide) en une
 * vraie réponse 404, cohérente avec l'espace (public ou back-office) d'où vient la
 * requête, plutôt qu'une erreur 500 générique.
 */
@ControllerAdvice(basePackages = "fr.lacassinauteur.site.catalogue.presentation")
public class GestionnaireErreursCatalogue {

    @ExceptionHandler({UniversIntrouvableException.class, CollectionIntrouvableException.class, LivreIntrouvableException.class})
    public String contenuIntrouvable(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        boolean backoffice = request.getRequestURI().startsWith("/backoffice");
        return backoffice ? "backoffice/erreur-404" : "public/erreur-404";
    }
}
