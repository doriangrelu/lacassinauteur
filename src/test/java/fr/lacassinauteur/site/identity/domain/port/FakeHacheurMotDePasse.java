package fr.lacassinauteur.site.identity.domain.port;

import fr.lacassinauteur.site.identity.domain.model.MotDePasseHache;

public class FakeHacheurMotDePasse implements HacheurMotDePasse {

    @Override
    public MotDePasseHache hacher(String motDePasseClair) {
        return new MotDePasseHache("hache:" + motDePasseClair);
    }

    @Override
    public boolean verifier(String motDePasseClair, MotDePasseHache motDePasseHache) {
        return hacher(motDePasseClair).equals(motDePasseHache);
    }
}
