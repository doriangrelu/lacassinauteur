package fr.lacassinauteur.site.contact.presentation.web;

import fr.lacassinauteur.site.contact.domain.exception.MessageContactIntrouvableException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Traduit les exceptions « introuvable » du domaine contact en une vraie réponse
 * 404 — même approche que GestionnaireErreursCatalogue/GestionnaireErreursActualite
 * (cf. ADR-0011), dupliquée par domaine plutôt que remontée dans `shared`.
 */
@ControllerAdvice(basePackages = "fr.lacassinauteur.site.contact.presentation")
public class GestionnaireErreursContact {

    @ExceptionHandler(MessageContactIntrouvableException.class)
    public String introuvable(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        boolean backoffice = request.getRequestURI().startsWith("/backoffice");
        return backoffice ? "backoffice/erreur-404" : "public/erreur-404";
    }
}
