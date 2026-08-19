package fr.lacassinauteur.site.legal.domain.exception;

/**
 * Levée quand les informations légales n'existent pas encore en base. Anormal en
 * fonctionnement nominal (le seeder les crée au premier démarrage) : traduit en
 * 404 plutôt qu'en page vide, pour que l'incident soit visible.
 */
public class InformationsLegalesIntrouvablesException extends RuntimeException {

    public InformationsLegalesIntrouvablesException() {
        super("Aucune information légale enregistrée");
    }
}
