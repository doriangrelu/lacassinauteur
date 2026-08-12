package fr.lacassinauteur.site.actualite.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public class Actualite {

    private final UUID id;
    private String titre;
    private String texte;
    private LocalDate date;
    private String lieu;
    private String lienBilletterie;
    private String imageUrl;
    private boolean archiveeManuellement;
    private boolean misEnAvant;

    public Actualite(UUID id, String titre, String texte, LocalDate date, String lieu, String lienBilletterie,
                      String imageUrl, boolean archiveeManuellement, boolean misEnAvant) {
        this.id = id;
        this.titre = titre;
        this.texte = blancVersNull(texte);
        this.date = date;
        this.lieu = blancVersNull(lieu);
        this.lienBilletterie = blancVersNull(lienBilletterie);
        this.imageUrl = imageUrl;
        this.archiveeManuellement = archiveeManuellement;
        this.misEnAvant = misEnAvant;
    }

    /**
     * Un champ texte optionnel vide n'est pas une valeur significative : le
     * normaliser en null évite qu'un th:if de présentation (ex. affichage du lien
     * billetterie) considère une chaîne vide comme « renseignée ».
     */
    private static String blancVersNull(String valeur) {
        return (valeur == null || valeur.isBlank()) ? null : valeur;
    }

    public static Actualite creer(String titre, String texte, LocalDate date, String lieu, String lienBilletterie,
                                   String imageUrl, boolean archiveeManuellement, boolean misEnAvant) {
        return new Actualite(UUID.randomUUID(), titre, texte, date, lieu, lienBilletterie, imageUrl,
                archiveeManuellement, misEnAvant);
    }

    public void modifier(String titre, String texte, LocalDate date, String lieu, String lienBilletterie,
                          String imageUrl, boolean archiveeManuellement, boolean misEnAvant) {
        this.titre = titre;
        this.texte = blancVersNull(texte);
        this.date = date;
        this.lieu = blancVersNull(lieu);
        this.lienBilletterie = blancVersNull(lienBilletterie);
        this.imageUrl = imageUrl;
        this.archiveeManuellement = archiveeManuellement;
        this.misEnAvant = misEnAvant;
    }

    /**
     * Le type est dérivé de la date (passée une fois échue) sauf archivage manuel
     * (ex. un événement annulé avant sa date), qui force ACTUALITE_PASSEE.
     */
    public TypeActualite type(LocalDate aujourdHui) {
        if (archiveeManuellement || date.isBefore(aujourdHui)) {
            return TypeActualite.ACTUALITE_PASSEE;
        }
        return TypeActualite.EVENEMENT_A_VENIR;
    }

    public UUID id() {
        return id;
    }

    public String titre() {
        return titre;
    }

    public String texte() {
        return texte;
    }

    public LocalDate date() {
        return date;
    }

    public String lieu() {
        return lieu;
    }

    public String lienBilletterie() {
        return lienBilletterie;
    }

    public String imageUrl() {
        return imageUrl;
    }

    public boolean archiveeManuellement() {
        return archiveeManuellement;
    }

    public boolean misEnAvant() {
        return misEnAvant;
    }
}
