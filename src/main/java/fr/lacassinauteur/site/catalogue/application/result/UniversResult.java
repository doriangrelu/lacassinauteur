package fr.lacassinauteur.site.catalogue.application.result;

import fr.lacassinauteur.site.catalogue.domain.model.PhotoLegendee;
import fr.lacassinauteur.site.catalogue.domain.model.Univers;

import java.util.UUID;

public record UniversResult(UUID id, String slug, String nom, String sousTitre, String texte, String photoUrl,
                             String photoComplementaireUrl, String photoComplementaireLegende, int ordre) {

    public static UniversResult depuis(Univers univers) {
        PhotoLegendee photoComplementaire = univers.photoComplementaire().orElse(null);
        return new UniversResult(
                univers.id(), univers.slug(), univers.nom(), univers.sousTitre(), univers.texte(),
                univers.photoUrl(),
                photoComplementaire == null ? null : photoComplementaire.url(),
                photoComplementaire == null ? null : photoComplementaire.legende(),
                univers.ordre());
    }
}
