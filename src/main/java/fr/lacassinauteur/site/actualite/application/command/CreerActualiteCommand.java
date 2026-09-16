package fr.lacassinauteur.site.actualite.application.command;

import java.time.LocalDate;

public record CreerActualiteCommand(String titre, String texte, LocalDate date, String lieu, String lienBilletterie,
                                     byte[] imageContenu, String imageNomFichier, byte[] photoComplementaireContenu,
                                     String photoComplementaireNomFichier, String photoComplementaireLegende,
                                     boolean archiveeManuellement, boolean misEnAvant) {
}
