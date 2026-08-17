package fr.lacassinauteur.site.identity.infrastructure.persistence.repository;

import fr.lacassinauteur.site.identity.infrastructure.persistence.entity.JetonReinitialisationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataJetonReinitialisationRepository extends JpaRepository<JetonReinitialisationJpaEntity, UUID> {

    Optional<JetonReinitialisationJpaEntity> findByUtilisateurIdAndDateExpirationAfter(UUID utilisateurId, Instant maintenant);

    void deleteByUtilisateurId(UUID utilisateurId);
}
