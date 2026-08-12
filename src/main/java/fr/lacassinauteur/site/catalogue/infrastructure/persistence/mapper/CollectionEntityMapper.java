package fr.lacassinauteur.site.catalogue.infrastructure.persistence.mapper;

import fr.lacassinauteur.site.catalogue.domain.model.Collection;
import fr.lacassinauteur.site.catalogue.infrastructure.persistence.entity.CollectionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CollectionEntityMapper {

    public CollectionJpaEntity versEntite(Collection collection) {
        return new CollectionJpaEntity(
                collection.id(), collection.slug(), collection.universId(), collection.nom(),
                collection.sousTitre(), collection.texte(), collection.ordre());
    }

    public Collection versDomaine(CollectionJpaEntity entite) {
        return new Collection(
                entite.getId(), entite.getSlug(), entite.getUniversId(), entite.getNom(),
                entite.getSousTitre(), entite.getTexte(), entite.getOrdre());
    }
}
