package fr.lacassinauteur.site.actualite.infrastructure.persistence.adapter;

import fr.lacassinauteur.site.actualite.domain.model.Actualite;
import fr.lacassinauteur.site.actualite.domain.port.ActualiteRepository;
import fr.lacassinauteur.site.actualite.infrastructure.persistence.mapper.ActualiteEntityMapper;
import fr.lacassinauteur.site.actualite.infrastructure.persistence.repository.SpringDataActualiteRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaActualiteRepository implements ActualiteRepository {

    private final SpringDataActualiteRepository springDataRepository;
    private final ActualiteEntityMapper mapper;

    public JpaActualiteRepository(SpringDataActualiteRepository springDataRepository, ActualiteEntityMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Actualite save(Actualite actualite) {
        return mapper.versDomaine(springDataRepository.save(mapper.versEntite(actualite)));
    }

    @Override
    public Optional<Actualite> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::versDomaine);
    }

    @Override
    public void supprimer(UUID id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public List<Actualite> findAllOrderByDateDesc() {
        return springDataRepository.findAllByOrderByDateDesc().stream()
                .map(mapper::versDomaine)
                .toList();
    }
}
