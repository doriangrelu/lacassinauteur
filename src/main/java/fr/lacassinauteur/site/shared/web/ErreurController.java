package fr.lacassinauteur.site.shared.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Remplace la résolution d'erreur par défaut de Spring Boot (page Whitelabel) par des
 * pages cohérentes avec la charte de chaque espace (public / back-office), choisi
 * selon l'URL d'origine de la requête en erreur.
 */
@Controller
public class ErreurController implements ErrorController {

    @RequestMapping("/error")
    public String gerer(HttpServletRequest request, Model model) {
        int statut = extraireStatut(request);
        boolean backoffice = estBackoffice(request);

        model.addAttribute("statut", statut);

        if (statut == 404) {
            return backoffice ? "backoffice/erreur-404" : "public/erreur-404";
        }
        return backoffice ? "backoffice/erreur-generique" : "public/erreur-generique";
    }

    private int extraireStatut(HttpServletRequest request) {
        Object statut = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        return statut != null ? Integer.parseInt(statut.toString()) : 500;
    }

    private boolean estBackoffice(HttpServletRequest request) {
        String uri = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        return uri != null && uri.startsWith("/backoffice");
    }
}
