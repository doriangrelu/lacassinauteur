package fr.lacassinauteur.site.identity.domain.port;

import fr.lacassinauteur.site.identity.domain.model.JetonReinitialisation;

import java.util.Optional;
import java.util.UUID;

public interface JetonReinitialisationRepository {

    JetonReinitialisation save(JetonReinitialisation jeton);

    Optional<JetonReinitialisation> findById(UUID id);

    /** Jeton non expiré du compte, s'il en existe un — au plus un à la fois (cf. ADR-0020). */
    Optional<JetonReinitialisation> findValidePourUtilisateur(UUID utilisateurId);

    void deleteById(UUID id);

    void deleteParUtilisateur(UUID utilisateurId);
}
