package fr.lacassinauteur.site.catalogue.infrastructure.persistence.repository;

import fr.lacassinauteur.site.catalogue.domain.model.StatutAvis;
import fr.lacassinauteur.site.catalogue.infrastructure.persistence.entity.AvisLecteurJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataAvisLecteurRepository extends JpaRepository<AvisLecteurJpaEntity, UUID> {

    List<AvisLecteurJpaEntity> findAllByOrderByDateSoumissionDesc();

    List<AvisLecteurJpaEntity> findByLivreIdAndStatutOrderByDateSoumissionDesc(UUID livreId, StatutAvis statut);
}
