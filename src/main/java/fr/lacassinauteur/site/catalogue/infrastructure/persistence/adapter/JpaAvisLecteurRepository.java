package fr.lacassinauteur.site.catalogue.infrastructure.persistence.adapter;

import fr.lacassinauteur.site.catalogue.domain.model.AvisLecteur;
import fr.lacassinauteur.site.catalogue.domain.model.StatutAvis;
import fr.lacassinauteur.site.catalogue.domain.port.AvisLecteurRepository;
import fr.lacassinauteur.site.catalogue.infrastructure.persistence.mapper.AvisLecteurEntityMapper;
import fr.lacassinauteur.site.catalogue.infrastructure.persistence.repository.SpringDataAvisLecteurRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAvisLecteurRepository implements AvisLecteurRepository {

    private final SpringDataAvisLecteurRepository springDataRepository;
    private final AvisLecteurEntityMapper mapper;

    public JpaAvisLecteurRepository(SpringDataAvisLecteurRepository springDataRepository, AvisLecteurEntityMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AvisLecteur save(AvisLecteur avisLecteur) {
        return mapper.versDomaine(springDataRepository.save(mapper.versEntite(avisLecteur)));
    }

    @Override
    public Optional<AvisLecteur> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::versDomaine);
    }

    @Override
    public List<AvisLecteur> findAllOrderByDateSoumissionDesc() {
        return springDataRepository.findAllByOrderByDateSoumissionDesc().stream()
                .map(mapper::versDomaine)
                .toList();
    }

    @Override
    public List<AvisLecteur> findByLivreIdAndStatutOrderByDateSoumissionDesc(UUID livreId, StatutAvis statut) {
        return springDataRepository.findByLivreIdAndStatutOrderByDateSoumissionDesc(livreId, statut).stream()
                .map(mapper::versDomaine)
                .toList();
    }
}
