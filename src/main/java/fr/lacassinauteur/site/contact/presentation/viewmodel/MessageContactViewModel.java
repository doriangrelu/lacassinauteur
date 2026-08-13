package fr.lacassinauteur.site.contact.presentation.viewmodel;

import fr.lacassinauteur.site.contact.domain.model.StatutMessage;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageContactViewModel(UUID id, String nom, String email, String objet, String message,
                                       LocalDateTime dateReception, StatutMessage statut) {
}
