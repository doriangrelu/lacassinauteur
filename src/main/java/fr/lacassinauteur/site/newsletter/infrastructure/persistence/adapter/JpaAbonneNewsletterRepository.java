package fr.lacassinauteur.site.newsletter.infrastructure.persistence.adapter;

import fr.lacassinauteur.site.newsletter.domain.model.AbonneNewsletter;
import fr.lacassinauteur.site.newsletter.domain.model.Email;
import fr.lacassinauteur.site.newsletter.domain.model.StatutAbonnement;
import fr.lacassinauteur.site.newsletter.domain.port.AbonneNewsletterRepository;
import fr.lacassinauteur.site.newsletter.infrastructure.persistence.mapper.AbonneNewsletterEntityMapper;
import fr.lacassinauteur.site.newsletter.infrastructure.persistence.repository.SpringDataAbonneNewsletterRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAbonneNewsletterRepository implements AbonneNewsletterRepository {

    private final SpringDataAbonneNewsletterRepository springDataRepository;
    private final AbonneNewsletterEntityMapper mapper;

    public JpaAbonneNewsletterRepository(SpringDataAbonneNewsletterRepository springDataRepository,
                                          AbonneNewsletterEntityMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AbonneNewsletter save(AbonneNewsletter abonne) {
        return mapper.versDomaine(springDataRepository.save(mapper.versEntite(abonne)));
    }

    @Override
    public Optional<AbonneNewsletter> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::versDomaine);
    }

    @Override
    public Optional<AbonneNewsletter> findByEmail(Email email) {
        return springDataRepository.findByEmail(email.valeur()).map(mapper::versDomaine);
    }

    @Override
    public Optional<AbonneNewsletter> findByJeton(UUID jetonConfirmation) {
        return springDataRepository.findByJetonConfirmation(jetonConfirmation).map(mapper::versDomaine);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return springDataRepository.existsByEmail(email.valeur());
    }

    @Override
    public List<AbonneNewsletter> findAllOrderByDateInscriptionDesc() {
        return springDataRepository.findAllByOrderByDateInscriptionDesc().stream()
                .map(mapper::versDomaine)
                .toList();
    }

    @Override
    public List<AbonneNewsletter> findAllConfirmes() {
        return springDataRepository.findAllByStatut(StatutAbonnement.CONFIRME).stream()
                .map(mapper::versDomaine)
                .toList();
    }
}
