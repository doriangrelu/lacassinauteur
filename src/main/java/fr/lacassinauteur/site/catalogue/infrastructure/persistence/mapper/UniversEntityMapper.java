package fr.lacassinauteur.site.catalogue.infrastructure.persistence.mapper;

import fr.lacassinauteur.site.catalogue.domain.model.Univers;
import fr.lacassinauteur.site.catalogue.infrastructure.persistence.entity.UniversJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UniversEntityMapper {

    public UniversJpaEntity versEntite(Univers univers) {
        return new UniversJpaEntity(
                univers.id(), univers.nom(), univers.sousTitre(), univers.texte(), univers.photoUrl(), univers.ordre());
    }

    public Univers versDomaine(UniversJpaEntity entite) {
        return new Univers(
                entite.getId(), entite.getNom(), entite.getSousTitre(), entite.getTexte(), entite.getPhotoUrl(), entite.getOrdre());
    }
}
