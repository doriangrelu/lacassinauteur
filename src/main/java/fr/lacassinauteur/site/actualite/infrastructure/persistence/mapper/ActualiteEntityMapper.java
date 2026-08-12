package fr.lacassinauteur.site.actualite.infrastructure.persistence.mapper;

import fr.lacassinauteur.site.actualite.domain.model.Actualite;
import fr.lacassinauteur.site.actualite.infrastructure.persistence.entity.ActualiteJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ActualiteEntityMapper {

    public ActualiteJpaEntity versEntite(Actualite actualite) {
        return new ActualiteJpaEntity(
                actualite.id(), actualite.titre(), actualite.texte(), actualite.date(), actualite.lieu(),
                actualite.lienBilletterie(), actualite.imageUrl(), actualite.archiveeManuellement(),
                actualite.misEnAvant());
    }

    public Actualite versDomaine(ActualiteJpaEntity entite) {
        return new Actualite(
                entite.getId(), entite.getTitre(), entite.getTexte(), entite.getDate(), entite.getLieu(),
                entite.getLienBilletterie(), entite.getImageUrl(), entite.isArchiveeManuellement(),
                entite.isMisEnAvant());
    }
}
