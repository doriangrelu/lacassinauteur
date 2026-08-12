package fr.lacassinauteur.site.shared.web;

import fr.lacassinauteur.site.shared.domain.exception.FichierInvalideException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GestionnaireErreursGlobal {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String fichierTropVolumineux(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("erreurFichier", "Le fichier dépasse la taille maximale autorisée (5 Mo).");
        return redirectionVersOrigine(request);
    }

    @ExceptionHandler(FichierInvalideException.class)
    public String fichierInvalide(FichierInvalideException exception, HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("erreurFichier", exception.getMessage());
        return redirectionVersOrigine(request);
    }

    private String redirectionVersOrigine(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/backoffice/univers");
    }
}
