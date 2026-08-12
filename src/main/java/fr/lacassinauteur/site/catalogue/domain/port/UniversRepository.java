package fr.lacassinauteur.site.catalogue.domain.port;

import fr.lacassinauteur.site.catalogue.domain.model.Univers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UniversRepository {

    Univers save(Univers univers);

    Optional<Univers> findById(UUID id);

    Optional<Univers> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Univers> findAllOrderByOrdre();
}
