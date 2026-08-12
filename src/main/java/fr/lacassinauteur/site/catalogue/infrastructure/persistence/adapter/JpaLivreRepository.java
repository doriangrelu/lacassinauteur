package fr.lacassinauteur.site.catalogue.infrastructure.persistence.adapter;

import fr.lacassinauteur.site.catalogue.domain.model.Livre;
import fr.lacassinauteur.site.catalogue.domain.port.LivreRepository;
import fr.lacassinauteur.site.catalogue.infrastructure.persistence.mapper.LivreEntityMapper;
import fr.lacassinauteur.site.catalogue.infrastructure.persistence.repository.SpringDataLivreRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaLivreRepository implements LivreRepository {

    private final SpringDataLivreRepository springDataRepository;
    private final LivreEntityMapper mapper;

    public JpaLivreRepository(SpringDataLivreRepository springDataRepository, LivreEntityMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Livre save(Livre livre) {
        return mapper.versDomaine(springDataRepository.save(mapper.versEntite(livre)));
    }

    @Override
    public Optional<Livre> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::versDomaine);
    }

    @Override
    public Optional<Livre> findBySlug(String slug) {
        return springDataRepository.findBySlug(slug).map(mapper::versDomaine);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return springDataRepository.existsBySlug(slug);
    }

    @Override
    public List<Livre> findByCollectionIdOrderByOrdre(UUID collectionId) {
        return springDataRepository.findByCollectionIdOrderByOrdreAsc(collectionId).stream()
                .map(mapper::versDomaine)
                .toList();
    }

    @Override
    public List<Livre> findAllOrderByOrdre() {
        return springDataRepository.findAllByOrderByOrdreAsc().stream()
                .map(mapper::versDomaine)
                .toList();
    }

    @Override
    public Optional<Livre> findDerniereParution() {
        return springDataRepository.findFirstByDerniereParutionTrue().map(mapper::versDomaine);
    }
}
