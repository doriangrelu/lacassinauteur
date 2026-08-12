package fr.lacassinauteur.site.newsletter.infrastructure.persistence.repository;

import fr.lacassinauteur.site.newsletter.domain.model.StatutAbonnement;
import fr.lacassinauteur.site.newsletter.infrastructure.persistence.entity.AbonneNewsletterJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataAbonneNewsletterRepository extends JpaRepository<AbonneNewsletterJpaEntity, UUID> {

    Optional<AbonneNewsletterJpaEntity> findByEmail(String email);

    Optional<AbonneNewsletterJpaEntity> findByJetonConfirmation(UUID jetonConfirmation);

    boolean existsByEmail(String email);

    List<AbonneNewsletterJpaEntity> findAllByOrderByDateInscriptionDesc();

    List<AbonneNewsletterJpaEntity> findAllByStatut(StatutAbonnement statut);
}
