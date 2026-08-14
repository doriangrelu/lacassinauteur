package fr.lacassinauteur.site.catalogue.infrastructure.persistence.mapper;

import fr.lacassinauteur.site.catalogue.domain.model.FicheProfessionnelle;
import fr.lacassinauteur.site.catalogue.domain.model.LienAchat;
import fr.lacassinauteur.site.catalogue.domain.model.Livre;
import fr.lacassinauteur.site.catalogue.infrastructure.persistence.entity.LivreJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class LivreEntityMapper {

    public LivreJpaEntity versEntite(Livre livre) {
        FicheProfessionnelle fiche = livre.ficheProfessionnelle().orElse(null);

        return new LivreJpaEntity(
                livre.id(), livre.slug(), livre.collectionId(), livre.titre(), livre.sousTitre(), livre.couvertureUrl(),
                livre.pitchCourt(), livre.resume(),
                livre.lienAchat().map(LienAchat::url).orElse(null),
                livre.lienAchat().map(LienAchat::libelleMarchand).orElse(null),
                livre.ordre(), livre.derniereParution(),
                fiche == null ? null : fiche.isbn(),
                fiche == null ? null : fiche.format(),
                fiche == null ? null : fiche.nombrePages(),
                fiche == null ? null : fiche.prix(),
                fiche == null ? null : fiche.lieuxDistribution(),
                fiche == null ? null : fiche.pitchEditeur(),
                fiche == null ? null : fiche.synopsisEditeur());
    }

    public Livre versDomaine(LivreJpaEntity entite) {
        LienAchat lienAchat = entite.getLienAchatUrl() == null
                ? null
                : new LienAchat(entite.getLienAchatUrl(), entite.getLienAchatLibelle());

        FicheProfessionnelle ficheProfessionnelle = ficheProfessionnelleVide(entite)
                ? null
                : new FicheProfessionnelle(entite.getIsbn(), entite.getFormat(), entite.getNombrePages(),
                        entite.getPrix(), entite.getLieuxDistribution(), entite.getPitchEditeur(), entite.getSynopsisEditeur());

        return new Livre(
                entite.getId(), entite.getSlug(), entite.getCollectionId(), entite.getTitre(), entite.getSousTitre(),
                entite.getCouvertureUrl(), entite.getPitchCourt(), entite.getResume(), lienAchat,
                entite.getOrdre(), entite.isDerniereParution(), ficheProfessionnelle);
    }

    private boolean ficheProfessionnelleVide(LivreJpaEntity entite) {
        return entite.getIsbn() == null && entite.getFormat() == null && entite.getNombrePages() == null
                && entite.getPrix() == null && entite.getLieuxDistribution() == null
                && entite.getPitchEditeur() == null && entite.getSynopsisEditeur() == null;
    }
}
