package fr.lacassinauteur.site.catalogue.presentation.viewmodel;

import java.util.UUID;

public record UniversViewModel(UUID id, String nom, String sousTitre, String texte, String photoUrl, int ordre) {
}
