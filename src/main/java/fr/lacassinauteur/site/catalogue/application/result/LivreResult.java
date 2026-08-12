package fr.lacassinauteur.site.catalogue.application.result;

import fr.lacassinauteur.site.catalogue.domain.model.Livre;

import java.util.UUID;

public record LivreResult(UUID id, UUID collectionId, String titre, String sousTitre, String couvertureUrl,
                           String pitchCourt, String resume, String lienAchatUrl, String lienAchatLibelle,
                           boolean disponible, boolean derniereParution, int ordre) {

    public static LivreResult depuis(Livre livre) {
        return new LivreResult(
                livre.id(), livre.collectionId(), livre.titre(), livre.sousTitre(), livre.couvertureUrl(),
                livre.pitchCourt(), livre.resume(),
                livre.lienAchat().map(la -> la.url()).orElse(null),
                livre.lienAchat().map(la -> la.libelleMarchand()).orElse(null),
                livre.disponible(), livre.derniereParution(), livre.ordre());
    }
}
