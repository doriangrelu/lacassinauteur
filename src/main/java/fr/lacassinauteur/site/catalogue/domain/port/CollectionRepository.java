package fr.lacassinauteur.site.catalogue.domain.port;

import fr.lacassinauteur.site.catalogue.domain.model.Collection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CollectionRepository {

    Collection save(Collection collection);

    Optional<Collection> findById(UUID id);

    List<Collection> findByUniversIdOrderByOrdre(UUID universId);

    List<Collection> findAllOrderByOrdre();
}
