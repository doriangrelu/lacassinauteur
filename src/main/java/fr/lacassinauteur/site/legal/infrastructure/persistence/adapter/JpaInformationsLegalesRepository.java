package fr.lacassinauteur.site.legal.infrastructure.persistence.adapter;

import fr.lacassinauteur.site.legal.domain.model.InformationsLegales;
import fr.lacassinauteur.site.legal.domain.port.InformationsLegalesRepository;
import fr.lacassinauteur.site.legal.infrastructure.persistence.mapper.InformationsLegalesEntityMapper;
import fr.lacassinauteur.site.legal.infrastructure.persistence.repository.SpringDataInformationsLegalesRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaInformationsLegalesRepository implements InformationsLegalesRepository {

    private final SpringDataInformationsLegalesRepository springDataRepository;
    private final InformationsLegalesEntityMapper mapper;

    public JpaInformationsLegalesRepository(SpringDataInformationsLegalesRepository springDataRepository,
                                             InformationsLegalesEntityMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<InformationsLegales> charger() {
        // Une seule ligne possible (contrainte en base, cf. migration V12) :
        // findAll est donc borné.
        return springDataRepository.findAll().stream().findFirst().map(mapper::versDomaine);
    }

    @Override
    public InformationsLegales save(InformationsLegales informationsLegales) {
        return mapper.versDomaine(springDataRepository.save(mapper.versEntite(informationsLegales)));
    }
}
