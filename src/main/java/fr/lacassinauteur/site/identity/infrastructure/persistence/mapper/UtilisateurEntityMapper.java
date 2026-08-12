package fr.lacassinauteur.site.identity.infrastructure.persistence.mapper;

import fr.lacassinauteur.site.identity.domain.model.Email;
import fr.lacassinauteur.site.identity.domain.model.MotDePasseHache;
import fr.lacassinauteur.site.identity.domain.model.Utilisateur;
import fr.lacassinauteur.site.identity.infrastructure.persistence.entity.UtilisateurJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UtilisateurEntityMapper {

    public UtilisateurJpaEntity versEntite(Utilisateur utilisateur) {
        return new UtilisateurJpaEntity(
                utilisateur.id(),
                utilisateur.email().valeur(),
                utilisateur.motDePasseHache().valeur(),
                utilisateur.role(),
                utilisateur.actif());
    }

    public Utilisateur versDomaine(UtilisateurJpaEntity entite) {
        return new Utilisateur(
                entite.getId(),
                new Email(entite.getEmail()),
                new MotDePasseHache(entite.getMotDePasseHache()),
                entite.getRole(),
                entite.isActif());
    }
}
