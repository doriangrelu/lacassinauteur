package fr.lacassinauteur.site.identity.domain.port;

import fr.lacassinauteur.site.identity.domain.model.MotDePasseHache;

public interface HacheurMotDePasse {

    MotDePasseHache hacher(String motDePasseClair);

    boolean verifier(String motDePasseClair, MotDePasseHache motDePasseHache);
}
