package fr.lacassinauteur.site.shared.infrastructure.stockage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.stockage.images")
public class StockageImagesProperties {

    /** Dossier sur disque où sont écrits les fichiers uploadés. */
    private String chemin = "./data/images";

    /** Préfixe d'URL publique sous lequel ce dossier est exposé (cf. WebConfig). */
    private String prefixeUrl = "/media";

    public String getChemin() {
        return chemin;
    }

    public void setChemin(String chemin) {
        this.chemin = chemin;
    }

    public String getPrefixeUrl() {
        return prefixeUrl;
    }

    public void setPrefixeUrl(String prefixeUrl) {
        this.prefixeUrl = prefixeUrl;
    }
}
