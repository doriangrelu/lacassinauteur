package fr.lacassinauteur.site.identity.domain.port;

import fr.lacassinauteur.site.identity.domain.model.Email;
import fr.lacassinauteur.site.identity.domain.model.Utilisateur;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UtilisateurRepository {

    Utilisateur save(Utilisateur utilisateur);

    Optional<Utilisateur> findById(UUID id);

    Optional<Utilisateur> findByEmail(Email email);

    boolean existsByEmail(Email email);

    List<Utilisateur> findAll();
}
