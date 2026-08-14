package fr.lacassinauteur.site.catalogue.application.result;

import fr.lacassinauteur.site.catalogue.domain.model.FicheProfessionnelle;
import fr.lacassinauteur.site.catalogue.domain.model.Livre;

import java.math.BigDecimal;
import java.util.UUID;

public record LivreResult(UUID id, String slug, UUID collectionId, String titre, String sousTitre, String couvertureUrl,
                           String pitchCourt, String resume, String lienAchatUrl, String lienAchatLibelle,
                           boolean disponible, boolean derniereParution, int ordre,
                           boolean ficheProfessionnelleRenseignee, String isbn, String format, Integer nombrePages,
                           BigDecimal prix, String lieuxDistribution, String pitchEditeur, String synopsisEditeur) {

    public static LivreResult depuis(Livre livre) {
        FicheProfessionnelle fiche = livre.ficheProfessionnelle().orElse(null);

        return new LivreResult(
                livre.id(), livre.slug(), livre.collectionId(), livre.titre(), livre.sousTitre(), livre.couvertureUrl(),
                livre.pitchCourt(), livre.resume(),
                livre.lienAchat().map(la -> la.url()).orElse(null),
                livre.lienAchat().map(la -> la.libelleMarchand()).orElse(null),
                livre.disponible(), livre.derniereParution(), livre.ordre(),
                fiche != null,
                fiche == null ? null : fiche.isbn(),
                fiche == null ? null : fiche.format(),
                fiche == null ? null : fiche.nombrePages(),
                fiche == null ? null : fiche.prix(),
                fiche == null ? null : fiche.lieuxDistribution(),
                fiche == null ? null : fiche.pitchEditeur(),
                fiche == null ? null : fiche.synopsisEditeur());
    }
}
