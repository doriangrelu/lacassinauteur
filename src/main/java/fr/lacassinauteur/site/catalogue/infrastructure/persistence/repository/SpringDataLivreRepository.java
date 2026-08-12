package fr.lacassinauteur.site.catalogue.infrastructure.persistence.repository;

import fr.lacassinauteur.site.catalogue.infrastructure.persistence.entity.LivreJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataLivreRepository extends JpaRepository<LivreJpaEntity, UUID> {

    Optional<LivreJpaEntity> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<LivreJpaEntity> findByCollectionIdOrderByOrdreAsc(UUID collectionId);

    List<LivreJpaEntity> findAllByOrderByOrdreAsc();

    Optional<LivreJpaEntity> findFirstByDerniereParutionTrue();
}
