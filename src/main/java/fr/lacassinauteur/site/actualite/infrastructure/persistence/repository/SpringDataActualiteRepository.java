package fr.lacassinauteur.site.actualite.infrastructure.persistence.repository;

import fr.lacassinauteur.site.actualite.infrastructure.persistence.entity.ActualiteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataActualiteRepository extends JpaRepository<ActualiteJpaEntity, UUID> {

    List<ActualiteJpaEntity> findAllByOrderByDateDesc();
}
