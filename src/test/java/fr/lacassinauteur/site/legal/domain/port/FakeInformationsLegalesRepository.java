package fr.lacassinauteur.site.legal.domain.port;

import fr.lacassinauteur.site.legal.domain.model.InformationsLegales;

import java.util.Optional;

/** Reproduit l'invariant « un seul enregistrement » du vrai adaptateur. */
public class FakeInformationsLegalesRepository implements InformationsLegalesRepository {

    private InformationsLegales stockees;

    @Override
    public Optional<InformationsLegales> charger() {
        return Optional.ofNullable(stockees);
    }

    @Override
    public InformationsLegales save(InformationsLegales informationsLegales) {
        this.stockees = informationsLegales;
        return informationsLegales;
    }
}
