package fr.lacassinauteur.site.catalogue.presentation.viewmodel;

import fr.lacassinauteur.site.catalogue.domain.model.StatutAvis;

import java.time.LocalDateTime;
import java.util.UUID;

public record AvisLecteurViewModel(UUID id, UUID livreId, String livreTitre, String nomAuteurAvis, String texte,
                                    Integer note, StatutAvis statut, LocalDateTime dateSoumission) {
}
