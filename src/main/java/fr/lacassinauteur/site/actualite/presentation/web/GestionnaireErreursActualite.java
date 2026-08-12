package fr.lacassinauteur.site.actualite.presentation.web;

import fr.lacassinauteur.site.actualite.domain.exception.ActualiteIntrouvableException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Traduit les exceptions « introuvable » du domaine actualité en une vraie réponse
 * 404, cohérente avec l'espace (public ou back-office) d'où vient la requête — même
 * approche que GestionnaireErreursCatalogue (cf. ADR-0011), volontairement dupliquée
 * plutôt que remontée dans `shared` pour ne pas coupler `shared` à un domaine précis.
 */
@ControllerAdvice(basePackages = "fr.lacassinauteur.site.actualite.presentation")
public class GestionnaireErreursActualite {

    @ExceptionHandler(ActualiteIntrouvableException.class)
    public String introuvable(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        boolean backoffice = request.getRequestURI().startsWith("/backoffice");
        return backoffice ? "backoffice/erreur-404" : "public/erreur-404";
    }
}
