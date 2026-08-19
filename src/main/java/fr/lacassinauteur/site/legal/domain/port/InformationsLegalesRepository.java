package fr.lacassinauteur.site.legal.domain.port;

import fr.lacassinauteur.site.legal.domain.model.InformationsLegales;

import java.util.Optional;

public interface InformationsLegalesRepository {

    /**
     * Charge l'unique enregistrement. Pas d'identifiant à passer : il n'y en a
     * jamais qu'un (cf. {@link InformationsLegales}).
     */
    Optional<InformationsLegales> charger();

    InformationsLegales save(InformationsLegales informationsLegales);
}
