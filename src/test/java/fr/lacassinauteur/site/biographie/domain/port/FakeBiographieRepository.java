package fr.lacassinauteur.site.biographie.domain.port;

import fr.lacassinauteur.site.biographie.domain.model.Biographie;

import java.util.Optional;

/**
 * Reproduit l'invariant « une seule biographie » du vrai adaptateur : un unique
 * emplacement, écrasé à chaque save.
 */
public class FakeBiographieRepository implements BiographieRepository {

    private Biographie stockee;

    @Override
    public Optional<Biographie> charger() {
        return Optional.ofNullable(stockee);
    }

    @Override
    public Biographie save(Biographie biographie) {
        this.stockee = biographie;
        return biographie;
    }
}
