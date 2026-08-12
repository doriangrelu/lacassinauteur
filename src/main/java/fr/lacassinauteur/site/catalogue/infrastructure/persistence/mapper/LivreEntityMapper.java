package fr.lacassinauteur.site.catalogue.infrastructure.persistence.mapper;

import fr.lacassinauteur.site.catalogue.domain.model.LienAchat;
import fr.lacassinauteur.site.catalogue.domain.model.Livre;
import fr.lacassinauteur.site.catalogue.infrastructure.persistence.entity.LivreJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class LivreEntityMapper {

    public LivreJpaEntity versEntite(Livre livre) {
        return new LivreJpaEntity(
                livre.id(), livre.slug(), livre.collectionId(), livre.titre(), livre.sousTitre(), livre.couvertureUrl(),
                livre.pitchCourt(), livre.resume(),
                livre.lienAchat().map(LienAchat::url).orElse(null),
                livre.lienAchat().map(LienAchat::libelleMarchand).orElse(null),
                livre.ordre(), livre.derniereParution());
    }

    public Livre versDomaine(LivreJpaEntity entite) {
        LienAchat lienAchat = entite.getLienAchatUrl() == null
                ? null
                : new LienAchat(entite.getLienAchatUrl(), entite.getLienAchatLibelle());

        return new Livre(
                entite.getId(), entite.getSlug(), entite.getCollectionId(), entite.getTitre(), entite.getSousTitre(),
                entite.getCouvertureUrl(), entite.getPitchCourt(), entite.getResume(), lienAchat,
                entite.getOrdre(), entite.isDerniereParution());
    }
}
