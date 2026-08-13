package fr.lacassinauteur.site.contact.infrastructure.persistence.adapter;

import fr.lacassinauteur.site.contact.domain.model.MessageContact;
import fr.lacassinauteur.site.contact.domain.port.MessageContactRepository;
import fr.lacassinauteur.site.contact.infrastructure.persistence.mapper.MessageContactEntityMapper;
import fr.lacassinauteur.site.contact.infrastructure.persistence.repository.SpringDataMessageContactRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaMessageContactRepository implements MessageContactRepository {

    private final SpringDataMessageContactRepository springDataRepository;
    private final MessageContactEntityMapper mapper;

    public JpaMessageContactRepository(SpringDataMessageContactRepository springDataRepository, MessageContactEntityMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public MessageContact save(MessageContact message) {
        return mapper.versDomaine(springDataRepository.save(mapper.versEntite(message)));
    }

    @Override
    public Optional<MessageContact> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::versDomaine);
    }

    @Override
    public List<MessageContact> findAllOrderByDateReceptionDesc() {
        return springDataRepository.findAllByOrderByDateReceptionDesc().stream()
                .map(mapper::versDomaine)
                .toList();
    }
}
