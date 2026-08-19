package fr.lacassinauteur.site.legal.application.command;

public record ModifierInformationsLegalesCommand(String editeurNom, String editeurStatut, String editeurAdresse,
                                                  String editeurEmail, String directeurPublication,
                                                  String hebergeurNom, String hebergeurAdresse,
                                                  int conservationNewsletterMois, int conservationContactMois) {
}
