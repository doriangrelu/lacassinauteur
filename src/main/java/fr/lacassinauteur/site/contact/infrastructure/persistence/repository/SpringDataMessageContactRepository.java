package fr.lacassinauteur.site.contact.infrastructure.persistence.repository;

import fr.lacassinauteur.site.contact.infrastructure.persistence.entity.MessageContactJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataMessageContactRepository extends JpaRepository<MessageContactJpaEntity, UUID> {

    List<MessageContactJpaEntity> findAllByOrderByDateReceptionDesc();
}
