package fr.lacassinauteur.site.actualite.presentation.viewmodel;

import fr.lacassinauteur.site.actualite.domain.model.TypeActualite;

import java.time.LocalDate;
import java.util.UUID;

public record ActualiteViewModel(UUID id, String titre, String texte, LocalDate date, String dateAffichage,
                                  String lieu, String lienBilletterie, String imageUrl, boolean archiveeManuellement,
                                  boolean misEnAvant, TypeActualite type) {
}
