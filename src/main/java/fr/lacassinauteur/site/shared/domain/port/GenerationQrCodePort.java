package fr.lacassinauteur.site.shared.domain.port;

public interface GenerationQrCodePort {

    /**
     * Produit un QR code encodant ce contenu, au format SVG (vectoriel, donc
     * imprimable à n'importe quelle taille sans perte — le cas d'usage visé est
     * l'impression sur une plaquette, cf. ADR-0028).
     */
    String genererSvg(String contenu);
}
