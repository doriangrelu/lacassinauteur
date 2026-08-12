package fr.lacassinauteur.site.catalogue.infrastructure.persistence.adapter;

import fr.lacassinauteur.site.catalogue.domain.model.Univers;
import fr.lacassinauteur.site.catalogue.domain.port.UniversRepository;
import fr.lacassinauteur.site.catalogue.infrastructure.persistence.mapper.UniversEntityMapper;
import fr.lacassinauteur.site.catalogue.infrastructure.persistence.repository.SpringDataUniversRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUniversRepository implements UniversRepository {

    private final SpringDataUniversRepository springDataRepository;
    private final UniversEntityMapper mapper;

    public JpaUniversRepository(SpringDataUniversRepository springDataRepository, UniversEntityMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Univers save(Univers univers) {
        return mapper.versDomaine(springDataRepository.save(mapper.versEntite(univers)));
    }

    @Override
    public Optional<Univers> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::versDomaine);
    }

    @Override
    public List<Univers> findAllOrderByOrdre() {
        return springDataRepository.findAllByOrderByOrdreAsc().stream()
                .map(mapper::versDomaine)
                .toList();
    }
}
