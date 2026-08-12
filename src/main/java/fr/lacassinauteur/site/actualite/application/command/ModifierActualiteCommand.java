package fr.lacassinauteur.site.actualite.application.command;

import java.time.LocalDate;
import java.util.UUID;

public record ModifierActualiteCommand(UUID actualiteId, String titre, String texte, LocalDate date, String lieu,
                                        String lienBilletterie, byte[] nouvelleImageContenu,
                                        String nouvelleImageNomFichier, boolean archiveeManuellement,
                                        boolean misEnAvant) {
}
