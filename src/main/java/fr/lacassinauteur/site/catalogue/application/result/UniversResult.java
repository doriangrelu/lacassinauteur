package fr.lacassinauteur.site.catalogue.application.result;

import fr.lacassinauteur.site.catalogue.domain.model.Univers;

import java.util.UUID;

public record UniversResult(UUID id, String nom, String sousTitre, String texte, String photoUrl, int ordre) {

    public static UniversResult depuis(Univers univers) {
        return new UniversResult(
                univers.id(), univers.nom(), univers.sousTitre(), univers.texte(), univers.photoUrl(), univers.ordre());
    }
}
