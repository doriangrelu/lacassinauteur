package fr.lacassinauteur.site.catalogue.presentation.viewmodel;

import java.util.UUID;

public record LivreViewModel(UUID id, UUID collectionId, String collectionNom, String titre, String sousTitre,
                              String couvertureUrl, String pitchCourt, String resume, String lienAchatUrl,
                              String lienAchatLibelle, boolean disponible, boolean derniereParution, int ordre) {
}
