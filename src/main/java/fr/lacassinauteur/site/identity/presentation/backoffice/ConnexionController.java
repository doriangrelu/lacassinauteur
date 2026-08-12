package fr.lacassinauteur.site.identity.presentation.backoffice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConnexionController {

    @GetMapping("/backoffice/connexion")
    public String connexion() {
        return "backoffice/connexion";
    }
}
