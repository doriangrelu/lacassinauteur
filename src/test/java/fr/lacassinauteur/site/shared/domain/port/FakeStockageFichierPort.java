package fr.lacassinauteur.site.shared.domain.port;

import java.util.ArrayList;
import java.util.List;

public class FakeStockageFichierPort implements StockageFichierPort {

    public final List<String> urlsSupprimees = new ArrayList<>();
    private int compteur = 0;

    @Override
    public String enregistrer(byte[] contenu, String nomOriginalFichier, String sousDossier) {
        compteur++;
        return "/media/" + sousDossier + "/fichier-" + compteur;
    }

    @Override
    public void supprimerSiGere(String url) {
        urlsSupprimees.add(url);
    }
}
