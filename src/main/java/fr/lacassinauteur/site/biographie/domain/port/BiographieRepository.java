package fr.lacassinauteur.site.biographie.domain.port;

import fr.lacassinauteur.site.biographie.domain.model.Biographie;

import java.util.Optional;

public interface BiographieRepository {

    /**
     * Charge l'unique biographie. {@link Optional#empty()} tant que le seeder n'a
     * pas tourné — pas d'identifiant à passer, il n'y a jamais qu'un enregistrement
     * (cf. {@link Biographie}).
     */
    Optional<Biographie> charger();

    Biographie save(Biographie biographie);
}
