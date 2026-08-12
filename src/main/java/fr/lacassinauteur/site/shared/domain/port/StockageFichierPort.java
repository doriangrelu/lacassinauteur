package fr.lacassinauteur.site.shared.domain.port;

public interface StockageFichierPort {

    /**
     * Enregistre le contenu dans le sous-dossier donné et retourne l'URL publique
     * relative permettant de le récupérer.
     */
    String enregistrer(byte[] contenu, String nomOriginalFichier, String sousDossier);

    /**
     * Supprime le fichier correspondant à cette URL si, et seulement si, elle est
     * gérée par ce stockage (best-effort, ne fait rien et ne lève rien sinon —
     * par exemple pour les chemins d'images fournies au démarrage, hors stockage).
     */
    void supprimerSiGere(String url);
}
