package fr.lacassinauteur.site.identity.infrastructure.persistence.adapter;

import fr.lacassinauteur.site.identity.domain.model.Email;
import fr.lacassinauteur.site.identity.domain.model.Utilisateur;
import fr.lacassinauteur.site.identity.domain.port.UtilisateurRepository;
import fr.lacassinauteur.site.identity.infrastructure.persistence.entity.UtilisateurJpaEntity;
import fr.lacassinauteur.site.identity.infrastructure.persistence.mapper.UtilisateurEntityMapper;
import fr.lacassinauteur.site.identity.infrastructure.persistence.repository.SpringDataUtilisateurRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUtilisateurRepository implements UtilisateurRepository {

    private final SpringDataUtilisateurRepository springDataRepository;
    private final UtilisateurEntityMapper mapper;

    public JpaUtilisateurRepository(SpringDataUtilisateurRepository springDataRepository, UtilisateurEntityMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Utilisateur save(Utilisateur utilisateur) {
        UtilisateurJpaEntity sauvegarde = springDataRepository.save(mapper.versEntite(utilisateur));
        return mapper.versDomaine(sauvegarde);
    }

    @Override
    public Optional<Utilisateur> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::versDomaine);
    }

    @Override
    public Optional<Utilisateur> findByEmail(Email email) {
        return springDataRepository.findByEmail(email.valeur()).map(mapper::versDomaine);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return springDataRepository.existsByEmail(email.valeur());
    }

    @Override
    public List<Utilisateur> findAll() {
        return springDataRepository.findAll().stream()
                .map(mapper::versDomaine)
                .toList();
    }
}
