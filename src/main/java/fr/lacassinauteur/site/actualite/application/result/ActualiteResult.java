package fr.lacassinauteur.site.actualite.application.result;

import fr.lacassinauteur.site.actualite.domain.model.Actualite;
import fr.lacassinauteur.site.actualite.domain.model.PhotoLegendee;
import fr.lacassinauteur.site.actualite.domain.model.TypeActualite;

import java.time.LocalDate;
import java.util.UUID;

public record ActualiteResult(UUID id, String titre, String texte, LocalDate date, String lieu,
                               String lienBilletterie, String imageUrl, String photoComplementaireUrl,
                               String photoComplementaireLegende, boolean archiveeManuellement, boolean misEnAvant,
                               TypeActualite type) {

    public static ActualiteResult depuis(Actualite actualite) {
        return depuis(actualite, LocalDate.now());
    }

    public static ActualiteResult depuis(Actualite actualite, LocalDate aujourdHui) {
        PhotoLegendee photoComplementaire = actualite.photoComplementaire().orElse(null);
        return new ActualiteResult(
                actualite.id(), actualite.titre(), actualite.texte(), actualite.date(), actualite.lieu(),
                actualite.lienBilletterie(), actualite.imageUrl(),
                photoComplementaire == null ? null : photoComplementaire.url(),
                photoComplementaire == null ? null : photoComplementaire.legende(),
                actualite.archiveeManuellement(), actualite.misEnAvant(), actualite.type(aujourdHui));
    }
}
