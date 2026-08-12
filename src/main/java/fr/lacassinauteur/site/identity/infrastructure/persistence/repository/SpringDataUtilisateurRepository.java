package fr.lacassinauteur.site.identity.infrastructure.persistence.repository;

import fr.lacassinauteur.site.identity.infrastructure.persistence.entity.UtilisateurJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataUtilisateurRepository extends JpaRepository<UtilisateurJpaEntity, UUID> {

    Optional<UtilisateurJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
