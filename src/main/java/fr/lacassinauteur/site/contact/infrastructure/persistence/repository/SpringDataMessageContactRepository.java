package fr.lacassinauteur.site.contact.infrastructure.persistence.repository;

import fr.lacassinauteur.site.contact.infrastructure.persistence.entity.MessageContactJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataMessageContactRepository extends JpaRepository<MessageContactJpaEntity, UUID> {

    // Plafonné : la liste back-office n'a pas vocation à afficher un historique
    // illimité (pas de pagination en v1, cf. relecture de code du 2026-08-13).
    List<MessageContactJpaEntity> findTop200ByOrderByDateReceptionDesc();
}
