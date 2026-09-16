package fr.lacassinauteur.site.catalogue.infrastructure.persistence.mapper;

import fr.lacassinauteur.site.catalogue.domain.model.PhotoLegendee;
import fr.lacassinauteur.site.catalogue.domain.model.Univers;
import fr.lacassinauteur.site.catalogue.infrastructure.persistence.entity.UniversJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UniversEntityMapper {

    public UniversJpaEntity versEntite(Univers univers) {
        PhotoLegendee photoComplementaire = univers.photoComplementaire().orElse(null);
        return new UniversJpaEntity(
                univers.id(), univers.slug(), univers.nom(), univers.sousTitre(), univers.texte(),
                univers.photoUrl(),
                photoComplementaire == null ? null : photoComplementaire.url(),
                photoComplementaire == null ? null : photoComplementaire.legende(),
                univers.ordre());
    }

    public Univers versDomaine(UniversJpaEntity entite) {
        PhotoLegendee photoComplementaire = entite.getPhotoComplementaireUrl() == null
                ? null
                : new PhotoLegendee(entite.getPhotoComplementaireUrl(), entite.getPhotoComplementaireLegende());
        return new Univers(
                entite.getId(), entite.getSlug(), entite.getNom(), entite.getSousTitre(), entite.getTexte(),
                entite.getPhotoUrl(), photoComplementaire, entite.getOrdre());
    }
}
