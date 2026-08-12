package fr.lacassinauteur.site.catalogue.domain.port;

import fr.lacassinauteur.site.catalogue.domain.model.Livre;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LivreRepository {

    Livre save(Livre livre);

    Optional<Livre> findById(UUID id);

    Optional<Livre> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Livre> findByCollectionIdOrderByOrdre(UUID collectionId);

    List<Livre> findAllOrderByOrdre();

    Optional<Livre> findDerniereParution();
}
