package fr.lacassinauteur.site.shared.infrastructure.stockage;

import fr.lacassinauteur.site.shared.domain.exception.FichierInvalideException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockageFichierLocalTest {

    @TempDir
    Path dossierTemporaire;

    private StockageFichierLocal nouveauStockage() {
        StockageImagesProperties proprietes = new StockageImagesProperties();
        proprietes.setChemin(dossierTemporaire.toString());
        proprietes.setPrefixeUrl("/media");
        return new StockageFichierLocal(proprietes);
    }

    @Test
    void enregistre_le_fichier_sur_disque_et_retourne_une_url_publique() throws IOException {
        StockageFichierLocal stockage = nouveauStockage();
        byte[] contenu = "contenu-image".getBytes(StandardCharsets.UTF_8);

        String url = stockage.enregistrer(contenu, "photo.png", "univers");

        assertThat(url).startsWith("/media/univers/").endsWith(".png");
        Path fichierEcrit = dossierTemporaire.resolve("univers").resolve(url.substring("/media/univers/".length()));
        assertThat(Files.readAllBytes(fichierEcrit)).isEqualTo(contenu);
    }

    @Test
    void refuse_une_extension_non_supportee() {
        StockageFichierLocal stockage = nouveauStockage();

        assertThatThrownBy(() -> stockage.enregistrer("x".getBytes(StandardCharsets.UTF_8), "script.exe", "univers"))
                .isInstanceOf(FichierInvalideException.class);
    }

    @Test
    void supprime_un_fichier_gere_par_ce_stockage() {
        StockageFichierLocal stockage = nouveauStockage();
        String url = stockage.enregistrer("contenu".getBytes(StandardCharsets.UTF_8), "photo.jpg", "univers");

        stockage.supprimerSiGere(url);

        Path fichier = dossierTemporaire.resolve("univers").resolve(url.substring("/media/univers/".length()));
        assertThat(Files.exists(fichier)).isFalse();
    }

    @Test
    void ignore_silencieusement_les_urls_non_gerees() {
        StockageFichierLocal stockage = nouveauStockage();

        stockage.supprimerSiGere("/images/univers/sobriete.jpg");
        stockage.supprimerSiGere(null);
        // Ne doit lever aucune exception.
    }
}
