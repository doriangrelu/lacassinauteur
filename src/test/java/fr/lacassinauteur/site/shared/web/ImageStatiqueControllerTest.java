package fr.lacassinauteur.site.shared.web;

import fr.lacassinauteur.site.shared.domain.port.FakeConversionImageWebPPort;
import fr.lacassinauteur.site.shared.infrastructure.stockage.StockageImagesProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ImageStatiqueControllerTest {

    // Ressource réelle du seed, cf. src/main/resources/static/images/univers/sobriete.jpg.
    private static final String CHEMIN_EXISTANT = "univers/sobriete.jpg";

    @TempDir
    Path dossierTemporaire;

    private final FakeConversionImageWebPPort conversion = new FakeConversionImageWebPPort();

    private ImageStatiqueController nouveauControleur() {
        StockageImagesProperties proprietes = new StockageImagesProperties();
        proprietes.setChemin(dossierTemporaire.toString());
        return new ImageStatiqueController(conversion, proprietes);
    }

    @Test
    void sert_lorigine_telle_quelle_si_le_client_naccepte_pas_webp() {
        ImageStatiqueController controleur = nouveauControleur();

        ResponseEntity<Resource> reponse = controleur.servir("/" + CHEMIN_EXISTANT, "text/html");

        assertThat(reponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reponse.getHeaders().getContentType().toString()).startsWith("image/jpeg");
    }

    @Test
    void convertit_en_webp_et_met_en_cache_si_le_client_accepte_webp() throws IOException {
        ImageStatiqueController controleur = nouveauControleur();

        ResponseEntity<Resource> reponse = controleur.servir("/" + CHEMIN_EXISTANT, "image/avif,image/webp,*/*");

        assertThat(reponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reponse.getHeaders().getContentType().toString()).isEqualTo("image/webp");
        assertThat(reponse.getBody().getInputStream().readAllBytes())
                .isEqualTo("webp-simule".getBytes(StandardCharsets.UTF_8));
        assertThat(dossierTemporaire.resolve(".cache-webp").resolve(CHEMIN_EXISTANT + ".webp")).exists();
    }

    @Test
    void reutilise_le_cache_sans_reconvertir() {
        ImageStatiqueController controleur = nouveauControleur();
        controleur.servir("/" + CHEMIN_EXISTANT, "image/webp");

        conversion.simulerEchec(); // si le cache n'est pas réutilisé, cette requête échouerait
        ResponseEntity<Resource> reponse = controleur.servir("/" + CHEMIN_EXISTANT, "image/webp");

        assertThat(reponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reponse.getHeaders().getContentType().toString()).isEqualTo("image/webp");
    }

    @Test
    void se_rabat_sur_loriginal_si_la_conversion_echoue() {
        conversion.simulerEchec();
        ImageStatiqueController controleur = nouveauControleur();

        ResponseEntity<Resource> reponse = controleur.servir("/" + CHEMIN_EXISTANT, "image/webp");

        assertThat(reponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reponse.getHeaders().getContentType().toString()).startsWith("image/jpeg");
    }

    @Test
    void repond_404_si_limage_nexiste_pas() {
        ImageStatiqueController controleur = nouveauControleur();

        ResponseEntity<Resource> reponse = controleur.servir("/univers/inexistante.jpg", "image/webp");

        assertThat(reponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void refuse_une_traversee_de_chemin() {
        ImageStatiqueController controleur = nouveauControleur();

        ResponseEntity<Resource> reponse = controleur.servir("/../../pom.xml", "image/webp");

        assertThat(reponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
