package fr.lacassinauteur.site.shared.web;

import fr.lacassinauteur.site.shared.domain.port.FournisseurUrlsPubliquesPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Sert {@code /sitemap.xml}, généré à chaque appel depuis les pages fixes du site
 * et les URL contribuées par les domaines via {@link FournisseurUrlsPubliquesPort}.
 *
 * <p>Généré à la volée plutôt que stocké : le catalogue change depuis le
 * back-office, un fichier figé deviendrait faux sans que personne ne s'en
 * aperçoive. Le volume (quelques dizaines d'URL) rend le coût négligeable.
 *
 * <p>Ne sont volontairement pas listés : le back-office, les pages d'erreur, les
 * pages de confirmation/désinscription newsletter (à usage unique, avec jeton) et
 * les fiches professionnelles en {@code noindex} (cf. ADR-0028).
 */
@Controller
public class SitemapController {

    /**
     * Pages toujours présentes, indépendantes des données. Ce sont des constantes
     * de routage : les faire transiter par un port n'ajouterait rien.
     */
    private static final List<String> PAGES_FIXES = List.of(
            "/", "/auteur", "/actualites", "/newsletter", "/contact",
            "/mentions-legales", "/confidentialite");

    private final List<FournisseurUrlsPubliquesPort> fournisseurs;
    private final String urlBase;

    public SitemapController(List<FournisseurUrlsPubliquesPort> fournisseurs,
                              @Value("${app.site.url-base}") String urlBase) {
        this.fournisseurs = fournisseurs;
        // Un sitemap n'accepte que des URL absolues : on retire un éventuel "/"
        // final pour ne pas produire de "//" en concaténant.
        this.urlBase = urlBase.endsWith("/") ? urlBase.substring(0, urlBase.length() - 1) : urlBase;
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> sitemap() {
        // LinkedHashSet : ordre stable (utile pour diffposer/tester) et dédoublonnage
        // si deux fournisseurs contribuaient la même URL.
        Set<String> chemins = new LinkedHashSet<>(PAGES_FIXES);
        for (FournisseurUrlsPubliquesPort fournisseur : fournisseurs) {
            chemins.addAll(fournisseur.urlsPubliques());
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(construireXml(chemins));
    }

    private String construireXml(Set<String> chemins) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        for (String chemin : chemins) {
            xml.append("  <url><loc>").append(echapper(urlBase + chemin)).append("</loc></url>\n");
        }
        return xml.append("</urlset>\n").toString();
    }

    /**
     * Les slugs sont normalisés (cf. ADR-0011) donc sans caractère spécial, mais on
     * échappe quand même : un sitemap invalide est rejeté en bloc par les moteurs,
     * et rien ne garantit que la génération des slugs ne changera jamais.
     */
    private String echapper(String url) {
        return url.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
