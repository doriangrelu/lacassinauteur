package fr.lacassinauteur.site.catalogue.application.result;

import fr.lacassinauteur.site.catalogue.domain.model.AvisLecteur;
import fr.lacassinauteur.site.catalogue.domain.model.StatutAvis;

import java.time.LocalDateTime;
import java.util.UUID;

public record AvisLecteurResult(UUID id, UUID livreId, String nomAuteurAvis, String texte, Integer note,
                                 StatutAvis statut, LocalDateTime dateSoumission) {

    public static AvisLecteurResult depuis(AvisLecteur avisLecteur) {
        return new AvisLecteurResult(
                avisLecteur.id(), avisLecteur.livreId(), avisLecteur.nomAuteurAvis(), avisLecteur.texte(),
                avisLecteur.note().orElse(null), avisLecteur.statut(), avisLecteur.dateSoumission());
    }
}
