package fr.lacassinauteur.site.catalogue.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record ModifierLivreCommand(UUID livreId, UUID collectionId, String titre, String sousTitre,
                                    byte[] nouvelleCouvertureContenu, String nouvelleCouvertureNomFichier,
                                    String pitchCourt, String resume, int ordre,
                                    String isbn, String format, Integer nombrePages, BigDecimal prix,
                                    String lieuxDistribution, String pitchEditeur, String synopsisEditeur) {
}
