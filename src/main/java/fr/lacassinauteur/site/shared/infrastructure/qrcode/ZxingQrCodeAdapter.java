package fr.lacassinauteur.site.shared.infrastructure.qrcode;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;
import fr.lacassinauteur.site.shared.domain.exception.GenerationQrCodeEchoueeException;
import fr.lacassinauteur.site.shared.domain.port.GenerationQrCodePort;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import com.google.zxing.EncodeHintType;

/**
 * Génère le SVG directement depuis la matrice de modules produite par ZXing, plutôt
 * que de passer par une image matricielle : un QR code destiné à l'impression doit
 * rester vectoriel, et cela évite la dépendance "zxing-javase" (encodage d'images)
 * pour une poignée de lignes de SVG.
 */
@Component
public class ZxingQrCodeAdapter implements GenerationQrCodePort {

    /** Marge blanche obligatoire autour du motif, en nombre de modules. */
    private static final int QUIET_ZONE_MODULES = 4;

    /**
     * Niveau M (~15 % de redondance) : compromis usuel entre densité et tolérance
     * aux défauts d'impression, suffisant pour une URL courte sur une plaquette.
     */
    private static final ErrorCorrectionLevel NIVEAU_CORRECTION = ErrorCorrectionLevel.M;

    @Override
    public String genererSvg(String contenu) {
        ByteMatrix matrice = encoder(contenu);
        int cote = matrice.getWidth() + 2 * QUIET_ZONE_MODULES;

        StringBuilder svg = new StringBuilder();
        svg.append("<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 ")
                .append(cote).append(' ').append(cote)
                .append("\" shape-rendering=\"crispEdges\">");
        // Fond blanc explicite : sans lui, un QR code sombre imprimé sur un support
        // colore devient illisible pour les lecteurs.
        svg.append("<rect width=\"").append(cote).append("\" height=\"").append(cote)
                .append("\" fill=\"#ffffff\"/>");

        for (int y = 0; y < matrice.getHeight(); y++) {
            for (int x = 0; x < matrice.getWidth(); x++) {
                if (matrice.get(x, y) == 1) {
                    svg.append("<rect x=\"").append(x + QUIET_ZONE_MODULES)
                            .append("\" y=\"").append(y + QUIET_ZONE_MODULES)
                            .append("\" width=\"1\" height=\"1\" fill=\"#000000\"/>");
                }
            }
        }

        return svg.append("</svg>").toString();
    }

    private ByteMatrix encoder(String contenu) {
        Map<EncodeHintType, Object> options = new HashMap<>();
        options.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        try {
            QRCode qrCode = Encoder.encode(contenu, NIVEAU_CORRECTION, options);
            return qrCode.getMatrix();
        } catch (WriterException exception) {
            throw new GenerationQrCodeEchoueeException(contenu, exception);
        }
    }
}
