package fr.lacassinauteur.site.legal.infrastructure.persistence.mapper;

import fr.lacassinauteur.site.legal.domain.model.InformationsLegales;
import fr.lacassinauteur.site.legal.infrastructure.persistence.entity.InformationsLegalesJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class InformationsLegalesEntityMapper {

    public InformationsLegalesJpaEntity versEntite(InformationsLegales informations) {
        return new InformationsLegalesJpaEntity(
                informations.id(), informations.editeurNom(), informations.editeurStatut(),
                informations.editeurAdresse(), informations.editeurEmail(), informations.directeurPublication(),
                informations.hebergeurNom(), informations.hebergeurAdresse(),
                informations.conservationNewsletterMois(), informations.conservationContactMois());
    }

    public InformationsLegales versDomaine(InformationsLegalesJpaEntity entite) {
        return new InformationsLegales(
                entite.getId(), entite.getEditeurNom(), entite.getEditeurStatut(), entite.getEditeurAdresse(),
                entite.getEditeurEmail(), entite.getDirecteurPublication(), entite.getHebergeurNom(),
                entite.getHebergeurAdresse(), entite.getConservationNewsletterMois(),
                entite.getConservationContactMois());
    }
}
