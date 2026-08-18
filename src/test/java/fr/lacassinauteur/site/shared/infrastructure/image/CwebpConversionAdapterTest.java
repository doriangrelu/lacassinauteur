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

    private byte[] genererPngValide() throws IOException {
        BufferedImage image = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream sortie = new ByteArrayOutputStream();
        ImageIO.write(image, "png", sortie);
        return sortie.toByteArray();
    }
}
