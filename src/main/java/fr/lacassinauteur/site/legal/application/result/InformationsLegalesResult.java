package fr.lacassinauteur.site.legal.application.result;

import fr.lacassinauteur.site.legal.domain.model.InformationsLegales;

public record InformationsLegalesResult(String editeurNom, String editeurStatut, String editeurAdresse,
                                         String editeurEmail, String directeurPublication, String hebergeurNom,
                                         String hebergeurAdresse, int conservationNewsletterMois,
                                         int conservationContactMois, boolean completes) {

    public static InformationsLegalesResult depuis(InformationsLegales informations) {
        return new InformationsLegalesResult(
                informations.editeurNom(), informations.editeurStatut(), informations.editeurAdresse(),
                informations.editeurEmail(), informations.directeurPublication(), informations.hebergeurNom(),
                informations.hebergeurAdresse(), informations.conservationNewsletterMois(),
                informations.conservationContactMois(), informations.completes());
    }
}
