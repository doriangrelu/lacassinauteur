package fr.lacassinauteur.site.catalogue.infrastructure.persistence.mapper;

import fr.lacassinauteur.site.catalogue.domain.model.AvisLecteur;
import fr.lacassinauteur.site.catalogue.infrastructure.persistence.entity.AvisLecteurJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AvisLecteurEntityMapper {

    public AvisLecteurJpaEntity versEntite(AvisLecteur avisLecteur) {
        return new AvisLecteurJpaEntity(
                avisLecteur.id(), avisLecteur.livreId(), avisLecteur.nomAuteurAvis(), avisLecteur.texte(),
                avisLecteur.note().orElse(null), avisLecteur.statut(), avisLecteur.dateSoumission());
    }

    public AvisLecteur versDomaine(AvisLecteurJpaEntity entite) {
        return new AvisLecteur(
                entite.getId(), entite.getLivreId(), entite.getNomAuteurAvis(), entite.getTexte(), entite.getNote(),
                entite.getStatut(), entite.getDateSoumission());
    }
}
