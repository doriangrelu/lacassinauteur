package fr.lacassinauteur.site.catalogue.infrastructure.persistence.adapter;

import fr.lacassinauteur.site.catalogue.domain.model.Collection;
import fr.lacassinauteur.site.catalogue.domain.port.CollectionRepository;
import fr.lacassinauteur.site.catalogue.infrastructure.persistence.mapper.CollectionEntityMapper;
import fr.lacassinauteur.site.catalogue.infrastructure.persistence.repository.SpringDataCollectionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaCollectionRepository implements CollectionRepository {

    private final SpringDataCollectionRepository springDataRepository;
    private final CollectionEntityMapper mapper;

    public JpaCollectionRepository(SpringDataCollectionRepository springDataRepository, CollectionEntityMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Collection save(Collection collection) {
        return mapper.versDomaine(springDataRepository.save(mapper.versEntite(collection)));
    }

    @Override
    public Optional<Collection> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::versDomaine);
    }

    @Override
    public List<Collection> findByUniversIdOrderByOrdre(UUID universId) {
        return springDataRepository.findByUniversIdOrderByOrdreAsc(universId).stream()
                .map(mapper::versDomaine)
                .toList();
    }

    @Override
    public List<Collection> findAllOrderByOrdre() {
        return springDataRepository.findAllByOrderByOrdreAsc().stream()
                .map(mapper::versDomaine)
                .toList();
    }
}
