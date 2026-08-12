package fr.lacassinauteur.site.catalogue.infrastructure.persistence.repository;

import fr.lacassinauteur.site.catalogue.infrastructure.persistence.entity.CollectionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataCollectionRepository extends JpaRepository<CollectionJpaEntity, UUID> {

    Optional<CollectionJpaEntity> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<CollectionJpaEntity> findByUniversIdOrderByOrdreAsc(UUID universId);

    List<CollectionJpaEntity> findAllByOrderByOrdreAsc();
}
