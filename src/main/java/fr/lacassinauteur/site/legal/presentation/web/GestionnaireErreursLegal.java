package fr.lacassinauteur.site.legal.presentation.web;

import fr.lacassinauteur.site.legal.domain.exception.InformationsLegalesIntrouvablesException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Traduit l'absence d'informations légales en 404, côté public comme back-office
 * — même approche que les autres domaines (cf. ADR-0011), volontairement dupliquée
 * plutôt que remontée dans `shared`.
 */
@ControllerAdvice(basePackages = "fr.lacassinauteur.site.legal.presentation")
public class GestionnaireErreursLegal {

    @ExceptionHandler(InformationsLegalesIntrouvablesException.class)
    public String introuvables(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        boolean backoffice = request.getRequestURI().startsWith("/backoffice");
        return backoffice ? "backoffice/erreur-404" : "public/erreur-404";
    }
}
