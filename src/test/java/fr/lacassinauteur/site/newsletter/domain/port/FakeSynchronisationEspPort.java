package fr.lacassinauteur.site.newsletter.domain.port;

import fr.lacassinauteur.site.newsletter.domain.model.AbonneNewsletter;

import java.util.ArrayList;
import java.util.List;

public class FakeSynchronisationEspPort implements SynchronisationEspPort {

    public final List<AbonneNewsletter> ajoutes = new ArrayList<>();
    public final List<AbonneNewsletter> retires = new ArrayList<>();

    @Override
    public void ajouterOuMettreAJour(AbonneNewsletter abonne) {
        ajoutes.add(abonne);
    }

    @Override
    public void retirer(AbonneNewsletter abonne) {
        retires.add(abonne);
    }
}
