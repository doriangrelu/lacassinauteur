package fr.lacassinauteur.site.catalogue.application.result;

import fr.lacassinauteur.site.catalogue.domain.model.Collection;

import java.util.UUID;

public record CollectionResult(UUID id, UUID universId, String nom, String sousTitre, String texte, int ordre) {

    public static CollectionResult depuis(Collection collection) {
        return new CollectionResult(
                collection.id(), collection.universId(), collection.nom(), collection.sousTitre(),
                collection.texte(), collection.ordre());
    }
}
