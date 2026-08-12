package fr.lacassinauteur.site.identity.infrastructure.security;

import fr.lacassinauteur.site.identity.domain.model.Email;
import fr.lacassinauteur.site.identity.domain.model.Utilisateur;
import fr.lacassinauteur.site.identity.domain.port.UtilisateurRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UtilisateurDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(new Email(email))
                .orElseThrow(() -> new UsernameNotFoundException("Aucun compte pour " + email));

        return User.withUsername(utilisateur.email().valeur())
                .password(utilisateur.motDePasseHache().valeur())
                .roles(utilisateur.role().name())
                .disabled(!utilisateur.actif())
                .build();
    }
}
