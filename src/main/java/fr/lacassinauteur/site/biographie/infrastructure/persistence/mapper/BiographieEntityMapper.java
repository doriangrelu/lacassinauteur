package fr.lacassinauteur.site.biographie.infrastructure.persistence.mapper;

import fr.lacassinauteur.site.biographie.domain.model.Biographie;
import fr.lacassinauteur.site.biographie.infrastructure.persistence.entity.BiographieJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class BiographieEntityMapper {

    public BiographieJpaEntity versEntite(Biographie biographie) {
        return new BiographieJpaEntity(biographie.id(), biographie.texte(), biographie.photoUrl());
    }

    public Biographie versDomaine(BiographieJpaEntity entite) {
        return new Biographie(entite.getId(), entite.getTexte(), entite.getPhotoUrl());
    }
}
