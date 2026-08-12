package fr.lacassinauteur.site.identity.infrastructure.security;

import fr.lacassinauteur.site.identity.domain.model.MotDePasseHache;
import fr.lacassinauteur.site.identity.domain.port.HacheurMotDePasse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptHacheurMotDePasse implements HacheurMotDePasse {

    private final PasswordEncoder passwordEncoder;

    public BCryptHacheurMotDePasse(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public MotDePasseHache hacher(String motDePasseClair) {
        return new MotDePasseHache(passwordEncoder.encode(motDePasseClair));
    }

    @Override
    public boolean verifier(String motDePasseClair, MotDePasseHache motDePasseHache) {
        return passwordEncoder.matches(motDePasseClair, motDePasseHache.valeur());
    }
}
