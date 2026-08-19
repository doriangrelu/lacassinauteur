package fr.lacassinauteur.site.legal.infrastructure.persistence.repository;

import fr.lacassinauteur.site.legal.infrastructure.persistence.entity.InformationsLegalesJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataInformationsLegalesRepository
        extends JpaRepository<InformationsLegalesJpaEntity, UUID> {
}
