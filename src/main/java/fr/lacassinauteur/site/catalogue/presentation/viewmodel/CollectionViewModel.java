package fr.lacassinauteur.site.catalogue.presentation.viewmodel;

import java.util.UUID;

public record CollectionViewModel(UUID id, UUID universId, String universNom, String nom, String sousTitre,
                                   String texte, int ordre) {
}
