package fr.lacassinauteur.site.catalogue.infrastructure.persistence.repository;

import fr.lacassinauteur.site.catalogue.infrastructure.persistence.entity.UniversJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataUniversRepository extends JpaRepository<UniversJpaEntity, UUID> {

    List<UniversJpaEntity> findAllByOrderByOrdreAsc();
}
