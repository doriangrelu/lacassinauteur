package fr.lacassinauteur.site.actualite.domain.port;

import fr.lacassinauteur.site.actualite.domain.model.Actualite;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActualiteRepository {

    Actualite save(Actualite actualite);

    Optional<Actualite> findById(UUID id);

    void supprimer(UUID id);

    List<Actualite> findAllOrderByDateDesc();
}
