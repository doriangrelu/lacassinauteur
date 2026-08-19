package fr.lacassinauteur.site.shared.infrastructure.qrcode;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ZxingQrCodeAdapterTest {

    private final ZxingQrCodeAdapter adapter = new ZxingQrCodeAdapter();

    @Test
    void produit_un_svg_bien_forme() {
        String svg = adapter.genererSvg("https://thierrylacassin-auteur.fr/livres/les-liens-du-crime/pro");

        assertThat(svg).startsWith("<svg xmlns=\"http://www.w3.org/2000/svg\"");
        assertThat(svg).endsWith("</svg>");
        assertThat(svg).contains("viewBox=\"0 0 ");
        // Fond blanc + au moins un module noir.
        assertThat(svg).contains("fill=\"#ffffff\"");
        assertThat(svg).contains("fill=\"#000000\"");
    }

    /**
     * Le vrai risque d'une génération SVG maison n'est pas de produire du XML
     * invalide, mais un motif que les lecteurs ne savent pas décoder (marge absente,
     * modules décalés). On relit donc le QR code produit plutôt que de se contenter
     * d'inspecter la chaîne.
     */
    @Test
    void le_qr_code_produit_est_relisible_et_contient_lurl() throws Exception {
        String url = "https://thierrylacassin-auteur.fr/livres/les-liens-du-crime/pro";

        String svg = adapter.genererSvg(url);
        // PURE_BARCODE : l'image reconstituée fait exactement un pixel par module,
        // sans marge photographique ni bruit — sans cet indice, le détecteur cherche
        // les motifs de repérage avec une tolérance calibrée pour une photo et
        // échoue sur une image aussi petite.
        Map<DecodeHintType, Object> indices = Map.of(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
        String decode = new QRCodeReader().decode(bitmapDepuisSvg(svg), indices).getText();

        assertThat(decode).isEqualTo(url);
    }

    /**
     * Rasterise le SVG « à la main » : chaque module vaut 1 pixel, on relit donc les
     * rects noirs pour reconstituer la matrice, sans dépendre d'une bibliothèque de
     * rendu SVG.
     */
    private BinaryBitmap bitmapDepuisSvg(String svg) {
        int cote = Integer.parseInt(svg.replaceAll("(?s).*viewBox=\"0 0 (\\d+) .*", "$1"));

        int[] pixels = new int[cote * cote];
        java.util.Arrays.fill(pixels, 0xFFFFFFFF);

        java.util.regex.Matcher module = java.util.regex.Pattern
                .compile("<rect x=\"(\\d+)\" y=\"(\\d+)\" width=\"1\" height=\"1\"")
                .matcher(svg);
        while (module.find()) {
            int x = Integer.parseInt(module.group(1));
            int y = Integer.parseInt(module.group(2));
            pixels[y * cote + x] = 0xFF000000;
        }

        return new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(cote, cote, pixels)));
    }
}
