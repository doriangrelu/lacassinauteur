package fr.lacassinauteur.site.actualite.infrastructure.persistence.mapper;

import fr.lacassinauteur.site.actualite.domain.model.Actualite;
import fr.lacassinauteur.site.actualite.domain.model.PhotoLegendee;
import fr.lacassinauteur.site.actualite.infrastructure.persistence.entity.ActualiteJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ActualiteEntityMapper {

    public ActualiteJpaEntity versEntite(Actualite actualite) {
        PhotoLegendee photoComplementaire = actualite.photoComplementaire().orElse(null);
        return new ActualiteJpaEntity(
                actualite.id(), actualite.titre(), actualite.texte(), actualite.date(), actualite.lieu(),
                actualite.lienBilletterie(), actualite.imageUrl(),
                photoComplementaire == null ? null : photoComplementaire.url(),
                photoComplementaire == null ? null : photoComplementaire.legende(),
                actualite.archiveeManuellement(), actualite.misEnAvant());
    }

    public Actualite versDomaine(ActualiteJpaEntity entite) {
        PhotoLegendee photoComplementaire = entite.getPhotoComplementaireUrl() == null
                ? null
                : new PhotoLegendee(entite.getPhotoComplementaireUrl(), entite.getPhotoComplementaireLegende());
        return new Actualite(
                entite.getId(), entite.getTitre(), entite.getTexte(), entite.getDate(), entite.getLieu(),
                entite.getLienBilletterie(), entite.getImageUrl(), photoComplementaire,
                entite.isArchiveeManuellement(), entite.isMisEnAvant());
    }
}
