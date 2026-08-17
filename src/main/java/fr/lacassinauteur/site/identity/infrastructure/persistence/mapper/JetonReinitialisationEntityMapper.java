package fr.lacassinauteur.site.identity.infrastructure.persistence.mapper;

import fr.lacassinauteur.site.identity.domain.model.JetonReinitialisation;
import fr.lacassinauteur.site.identity.infrastructure.persistence.entity.JetonReinitialisationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class JetonReinitialisationEntityMapper {

    public JetonReinitialisationJpaEntity versEntite(JetonReinitialisation jeton) {
        return new JetonReinitialisationJpaEntity(
                jeton.id(), jeton.utilisateurId(), jeton.jeton(), jeton.dateExpiration());
    }

    public JetonReinitialisation versDomaine(JetonReinitialisationJpaEntity entite) {
        return new JetonReinitialisation(
                entite.getId(), entite.getUtilisateurId(), entite.getJeton(), entite.getDateExpiration());
    }
}
