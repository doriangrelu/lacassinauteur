package fr.lacassinauteur.site.biographie.infrastructure.persistence.adapter;

import fr.lacassinauteur.site.biographie.domain.model.Biographie;
import fr.lacassinauteur.site.biographie.domain.port.BiographieRepository;
import fr.lacassinauteur.site.biographie.infrastructure.persistence.mapper.BiographieEntityMapper;
import fr.lacassinauteur.site.biographie.infrastructure.persistence.repository.SpringDataBiographieRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaBiographieRepository implements BiographieRepository {

    private final SpringDataBiographieRepository springDataRepository;
    private final BiographieEntityMapper mapper;

    public JpaBiographieRepository(SpringDataBiographieRepository springDataRepository,
                                    BiographieEntityMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Biographie> charger() {
        // La table ne contient jamais plus d'une ligne (contrainte en base, cf.
        // migration V11) : findAll est donc borné, pas besoin de pagination.
        return springDataRepository.findAll().stream().findFirst().map(mapper::versDomaine);
    }

    @Override
    public Biographie save(Biographie biographie) {
        return mapper.versDomaine(springDataRepository.save(mapper.versEntite(biographie)));
    }
}
