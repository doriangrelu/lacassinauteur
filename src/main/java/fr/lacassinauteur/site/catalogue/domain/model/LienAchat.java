package fr.lacassinauteur.site.catalogue.domain.model;

public record LienAchat(String url, String libelleMarchand) {

    public LienAchat {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("L'URL du lien d'achat ne peut pas être vide");
        }
        if (libelleMarchand == null || libelleMarchand.isBlank()) {
            libelleMarchand = "Amazon";
        }
    }
}
