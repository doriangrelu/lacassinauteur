package fr.lacassinauteur.site.biographie.application.result;

import fr.lacassinauteur.site.biographie.domain.model.Biographie;

public record BiographieResult(String texte, String photoUrl) {

    public static BiographieResult depuis(Biographie biographie) {
        return new BiographieResult(biographie.texte(), biographie.photoUrl());
    }
}
