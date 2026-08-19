package fr.lacassinauteur.site.biographie.presentation.web;

import fr.lacassinauteur.site.biographie.domain.exception.BiographieIntrouvableException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Traduit l'absence de biographie en 404, côté public comme back-office — même
 * approche que GestionnaireErreursActualite/Catalogue (cf. ADR-0011), volontairement
 * dupliquée par domaine plutôt que remontée dans `shared`, pour ne pas y faire
 * remonter la connaissance d'exceptions métier précises.
 */
@ControllerAdvice(basePackages = "fr.lacassinauteur.site.biographie.presentation")
public class GestionnaireErreursBiographie {

    @ExceptionHandler(BiographieIntrouvableException.class)
    public String introuvable(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        boolean backoffice = request.getRequestURI().startsWith("/backoffice");
        return backoffice ? "backoffice/erreur-404" : "public/erreur-404";
    }
}
