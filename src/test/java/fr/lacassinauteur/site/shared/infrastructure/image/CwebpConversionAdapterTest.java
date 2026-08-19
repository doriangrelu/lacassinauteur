package fr.lacassinauteur.site.shared.infrastructure.image;

import fr.lacassinauteur.site.shared.domain.exception.ConversionImageEchoueeException;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Dépend du binaire externe "cwebp" (cf. CwebpConversionAdapter), absent sur un
 * poste de développement Windows hors Docker — les tests qui l'exigent se
 * désactivent proprement via Assumptions plutôt que d'échouer, même logique que
 * JpaUtilisateurRepositoryTest (cf. CLAUDE.md "Pièges connus").
 */
class CwebpConversionAdapterTest {

    private final CwebpConversionAdapter adapter = new CwebpConversionAdapter();

    private boolean cwebpDisponible() {
        try {
            Process processus = new ProcessBuilder("cwebp", "-version").start();
            return processus.waitFor(5, java.util.concurrent.TimeUnit.SECONDS) && processus.exitValue() == 0;
        } catch (IOException | InterruptedException exception) {
            return false;
        }
    }

    @BeforeEach
    void verifierDisponibiliteCwebp() {
        Assumptions.assumeTrue(cwebpDisponible(), "cwebp non installé sur cet environnement — test ignoré");
    }

    @Test
    void convertit_une_image_png_en_webp() throws IOException {
        byte[] png = genererPngValide();

        byte[] webp = adapter.convertirEnWebp(png);

        assertThat(webp).isNotEmpty();
        assertThat(new String(webp, 0, 4, StandardCharsets.US_ASCII)).isEqualTo("RIFF");
        assertThat(new String(webp, 8, 4, StandardCharsets.US_ASCII)).isEqualTo("WEBP");
    }

    @Test
    void leve_une_exception_si_le_contenu_nest_pas_une_image_valide() {
        assertThatThrownBy(() -> adapter.convertirEnWebp("pas-une-image".getBytes(StandardCharsets.UTF_8)))
                .isInstanceOf(ConversionImageEchoueeException.class);
    }

    @Test
    void redimensionne_une_image_surdimensionnee(@org.junit.jupiter.api.io.TempDir Path dossierTemp) throws IOException {
        Assumptions.assumeTrue(dwebpDisponible(), "dwebp non installé sur cet environnement — test ignoré");
        byte[] png = genererPngValide(2000, 1000);

        byte[] webp = adapter.convertirEnWebp(png);

        BufferedImage decodee = decoderWebp(webp, dossierTemp);
        assertThat(Math.max(decodee.getWidth(), decodee.getHeight())).isLessThanOrEqualTo(1600);
        assertThat(decodee.getWidth()).isEqualTo(1600);
        assertThat(decodee.getHeight()).isEqualTo(800);
    }

    @Test
    void ne_redimensionne_pas_une_image_deja_sous_le_seuil() throws IOException {
        byte[] png = genererPngValide(4, 4);

        byte[] webp = adapter.convertirEnWebp(png);

        assertThat(webp).isNotEmpty();
    }

    private boolean dwebpDisponible() {
        try {
            Process processus = new ProcessBuilder("dwebp", "-version").start();
            return processus.waitFor(5, java.util.concurrent.TimeUnit.SECONDS) && processus.exitValue() == 0;
        } catch (IOException | InterruptedException exception) {
            return false;
        }
    }

    private BufferedImage decoderWebp(byte[] webp, Path dossierTemp) throws IOException {
        Path entree = dossierTemp.resolve("decode-in.webp");
        Path sortie = dossierTemp.resolve("decode-out.png");
        Files.write(entree, webp);
        try {
            Process processus = new ProcessBuilder("dwebp", entree.toString(), "-o", sortie.toString())
                    .redirectErrorStream(true)
                    .start();
            processus.waitFor(5, java.util.concurrent.TimeUnit.SECONDS);
            return ImageIO.read(sortie.toFile());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IOException(exception);
        }
    }

    private byte[] genererPngValide() throws IOException {
        return genererPngValide(4, 4);
    }

    private byte[] genererPngValide(int largeur, int hauteur) throws IOException {
        BufferedImage image = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream sortie = new ByteArrayOutputStream();
        ImageIO.write(image, "png", sortie);
        return sortie.toByteArray();
    }
}
