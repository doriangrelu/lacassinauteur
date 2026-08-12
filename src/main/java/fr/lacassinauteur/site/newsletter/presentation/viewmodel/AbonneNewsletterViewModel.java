package fr.lacassinauteur.site.newsletter.presentation.viewmodel;

import fr.lacassinauteur.site.newsletter.domain.model.StatutAbonnement;

import java.time.LocalDateTime;
import java.util.UUID;

public record AbonneNewsletterViewModel(UUID id, String prenom, String email, StatutAbonnement statut,
                                         LocalDateTime dateInscription) {
}
