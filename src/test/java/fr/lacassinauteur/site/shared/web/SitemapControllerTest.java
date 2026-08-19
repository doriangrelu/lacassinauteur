package fr.lacassinauteur.site.shared.web;

import fr.lacassinauteur.site.shared.domain.port.FournisseurUrlsPubliquesPort;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SitemapControllerTest {

    private String sitemap(String urlBase, List<FournisseurUrlsPubliquesPort> fournisseurs) {
        return new SitemapController(fournisseurs, urlBase).sitemap().getBody();
    }

    @Test
    void liste_les_pages_fixes_en_urls_absolues() {
        String xml = sitemap("https://exemple.fr", List.of());

        assertThat(xml).startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        assertThat(xml).contains("<loc>https://exemple.fr/</loc>");
        assertThat(xml).contains("<loc>https://exemple.fr/auteur</loc>");
        assertThat(xml).contains("<loc>https://exemple.fr/mentions-legales</loc>");
        assertThat(xml).contains("<loc>https://exemple.fr/confidentialite</loc>");
        assertThat(xml).endsWith("</urlset>\n");
    }

    @Test
    void integre_les_urls_contribuees_par_les_domaines() {
        String xml = sitemap("https://exemple.fr",
                List.of(() -> List.of("/livres/mon-livre", "/univers/mon-univers")));

        assertThat(xml).contains("<loc>https://exemple.fr/livres/mon-livre</loc>");
        assertThat(xml).contains("<loc>https://exemple.fr/univers/mon-univers</loc>");
    }

    /**
     * Le vrai risque : qu'une page volontairement non indexée finisse dans le
     * sitemap et soit donc proposée à l'indexation, à rebours de son {@code
     * noindex} (cf. ADR-0028).
     */
    @Test
    void nexpose_ni_le_backoffice_ni_les_fiches_professionnelles() {
        String xml = sitemap("https://exemple.fr", List.of(() -> List.of("/livres/mon-livre")));

        assertThat(xml).doesNotContain("/pro");
        assertThat(xml).doesNotContain("/backoffice");
        assertThat(xml).doesNotContain("erreur");
    }

    @Test
    void ne_produit_pas_de_double_slash_si_lurl_de_base_finit_par_un_slash() {
        String xml = sitemap("https://exemple.fr/", List.of());

        assertThat(xml).contains("<loc>https://exemple.fr/</loc>");
        assertThat(xml).doesNotContain("https://exemple.fr//");
    }

    @Test
    void dedoublonne_les_urls_contribuees_deux_fois() {
        String xml = sitemap("https://exemple.fr",
                List.of(() -> List.of("/auteur"), () -> List.of("/auteur")));

        assertThat(xml.split("<loc>https://exemple.fr/auteur</loc>", -1)).hasSize(2);
    }

    @Test
    void echappe_les_caracteres_speciaux_dans_les_urls() {
        String xml = sitemap("https://exemple.fr", List.of(() -> List.of("/livres/a&b")));

        assertThat(xml).contains("/livres/a&amp;b");
        assertThat(xml).doesNotContain("/livres/a&b");
    }
}
