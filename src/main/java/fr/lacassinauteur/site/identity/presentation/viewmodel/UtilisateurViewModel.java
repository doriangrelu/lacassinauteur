package fr.lacassinauteur.site.identity.presentation.viewmodel;

import java.util.UUID;

public record UtilisateurViewModel(UUID id, String email, String role, boolean actif) {
}
