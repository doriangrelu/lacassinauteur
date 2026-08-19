package fr.lacassinauteur.site.biographie.domain.exception;

/**
 * Levée quand la biographie n'existe pas encore en base. Situation anormale en
 * fonctionnement nominal (le seeder la crée au premier démarrage) : traduite en 404
 * plutôt qu'en page blanche, pour que l'incident soit visible au lieu d'être masqué.
 */
public class BiographieIntrouvableException extends RuntimeException {

    public BiographieIntrouvableException() {
        super("Aucune biographie enregistrée");
    }
}
