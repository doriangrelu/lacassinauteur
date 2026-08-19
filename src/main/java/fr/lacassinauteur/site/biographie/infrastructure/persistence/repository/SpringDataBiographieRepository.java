package fr.lacassinauteur.site.biographie.infrastructure.persistence.repository;

import fr.lacassinauteur.site.biographie.infrastructure.persistence.entity.BiographieJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataBiographieRepository extends JpaRepository<BiographieJpaEntity, UUID> {
}
