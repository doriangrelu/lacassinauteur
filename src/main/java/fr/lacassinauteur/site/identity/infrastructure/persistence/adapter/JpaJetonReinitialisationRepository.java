package fr.lacassinauteur.site.identity.infrastructure.persistence.adapter;

import fr.lacassinauteur.site.identity.domain.model.JetonReinitialisation;
import fr.lacassinauteur.site.identity.domain.port.JetonReinitialisationRepository;
import fr.lacassinauteur.site.identity.infrastructure.persistence.mapper.JetonReinitialisationEntityMapper;
import fr.lacassinauteur.site.identity.infrastructure.persistence.repository.SpringDataJetonReinitialisationRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaJetonReinitialisationRepository implements JetonReinitialisationRepository {

    private final SpringDataJetonReinitialisationRepository springDataRepository;
    private final JetonReinitialisationEntityMapper mapper;

    public JpaJetonReinitialisationRepository(
            SpringDataJetonReinitialisationRepository springDataRepository, JetonReinitialisationEntityMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public JetonReinitialisation save(JetonReinitialisation jeton) {
        return mapper.versDomaine(springDataRepository.save(mapper.versEntite(jeton)));
    }

    @Override
    public Optional<JetonReinitialisation> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::versDomaine);
    }

    @Override
    public Optional<JetonReinitialisation> findValidePourUtilisateur(UUID utilisateurId) {
        return springDataRepository.findByUtilisateurIdAndDateExpirationAfter(utilisateurId, Instant.now())
                .map(mapper::versDomaine);
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public void deleteParUtilisateur(UUID utilisateurId) {
        springDataRepository.deleteByUtilisateurId(utilisateurId);
    }
}
