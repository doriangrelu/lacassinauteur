package fr.lacassinauteur.site.catalogue.domain.model;

import java.math.BigDecimal;

/**
 * Fiche technique d'un livre à destination des professionnels du livre
 * (libraires, presse) : informations éditoriales complémentaires à la fiche
 * grand public. Value object optionnel sur {@link Livre} — tous les champs
 * sont facultatifs.
 */
public record FicheProfessionnelle(String isbn, String format, Integer nombrePages, BigDecimal prix,
                                    String lieuxDistribution, String pitchEditeur, String synopsisEditeur) {
}
