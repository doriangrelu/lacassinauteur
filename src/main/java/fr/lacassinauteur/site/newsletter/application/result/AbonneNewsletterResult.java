package fr.lacassinauteur.site.newsletter.application.result;

import fr.lacassinauteur.site.newsletter.domain.model.AbonneNewsletter;
import fr.lacassinauteur.site.newsletter.domain.model.StatutAbonnement;

import java.time.LocalDateTime;
import java.util.UUID;

public record AbonneNewsletterResult(UUID id, String prenom, String email, StatutAbonnement statut,
                                      LocalDateTime dateInscription, LocalDateTime dateConfirmation,
                                      UUID jetonConfirmation) {

    public static AbonneNewsletterResult depuis(AbonneNewsletter abonne) {
        return new AbonneNewsletterResult(
                abonne.id(), abonne.prenom(), abonne.email().valeur(), abonne.statut(),
                abonne.dateInscription(), abonne.dateConfirmation(), abonne.jetonConfirmation());
    }
}
