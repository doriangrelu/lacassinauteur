package fr.lacassinauteur.site.shared.infrastructure.image;

import fr.lacassinauteur.site.shared.domain.exception.ConversionImageEchoueeException;
import fr.lacassinauteur.site.shared.domain.port.ConversionImageWebPPort;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * S'appuie sur le binaire externe "cwebp" (paquet Debian "webp", installé dans
 * l'image Docker de prod) plutôt qu'une bibliothèque Java — même logique que le CLI
 * Tailwind standalone (cf. ADR-0006) : un binaire de référence, éprouvé, sans
 * dépendance native JVM à faire fonctionner sur toutes les plateformes.
 *
 * Si le binaire est absent (ex. poste de développement Windows, hors Docker) ou que
 * la conversion échoue pour toute autre raison, lève
 * {@link ConversionImageEchoueeException} — aux appelants de dégrader proprement
 * (conserver le format d'origine) plutôt que de faire échouer l'opération.
 */
@Component
public class CwebpConversionAdapter implements ConversionImageWebPPort {

    private static final int QUALITE = 82;
    private static final long DELAI_MAX_SECONDES = 15;

    /**
     * Aucune image du site n'est jamais affichée au-delà de cette dimension (cf.
     * audit Lighthouse image-delivery-insight : les couvertures sources, parfois
     * scannées en très haute résolution, étaient livrées telles quelles alors que
     * la plus grande taille d'affichage réelle est très inférieure).
     */
    private static final int DIMENSION_MAX_PIXELS = 1600;

    @Override
    public byte[] convertirEnWebp(byte[] contenuOriginal) {
        Path entree = null;
        Path sortie = null;
        try {
            entree = Files.createTempFile("webp-in-", ".src");
            sortie = Files.createTempFile("webp-out-", ".webp");
            Files.write(entree, contenuOriginal);

            List<String> commande = new ArrayList<>(
                    List.of("cwebp", "-quiet", "-q", String.valueOf(QUALITE)));
            ajouterRedimensionnementSiNecessaire(contenuOriginal, commande);
            commande.addAll(List.of(entree.toString(), "-o", sortie.toString()));

            Process processus = new ProcessBuilder(commande)
                    .redirectErrorStream(true)
                    .start();

            boolean termine = processus.waitFor(DELAI_MAX_SECONDES, TimeUnit.SECONDS);
            if (!termine) {
                processus.destroyForcibly();
                throw new ConversionImageEchoueeException("cwebp a dépassé le délai de " + DELAI_MAX_SECONDES + "s");
            }
            if (processus.exitValue() != 0) {
                throw new ConversionImageEchoueeException("cwebp a échoué (code " + processus.exitValue() + ")");
            }

            return Files.readAllBytes(sortie);
        } catch (IOException exception) {
            throw new ConversionImageEchoueeException("Binaire cwebp indisponible ou erreur d'E/S", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ConversionImageEchoueeException("Conversion WebP interrompue", exception);
        } finally {
            supprimerSiPresent(entree);
            supprimerSiPresent(sortie);
        }
    }

    private void ajouterRedimensionnementSiNecessaire(byte[] contenuOriginal, List<String> commande) {
        int[] dimensions = lireDimensions(contenuOriginal);
        if (dimensions == null) {
            return;
        }
        int largeur = dimensions[0];
        int hauteur = dimensions[1];
        if (Math.max(largeur, hauteur) <= DIMENSION_MAX_PIXELS) {
            return;
        }
        if (largeur >= hauteur) {
            commande.addAll(List.of("-resize", String.valueOf(DIMENSION_MAX_PIXELS), "0"));
        } else {
            commande.addAll(List.of("-resize", "0", String.valueOf(DIMENSION_MAX_PIXELS)));
        }
    }

    /**
     * Lecture des dimensions via les seuls en-têtes du fichier (ImageIO ne décode
     * pas les pixels pour {@code getWidth}/{@code getHeight}) — rapide même sur de
     * gros fichiers, et volontairement tolérante : un format non reconnu par
     * ImageIO (ex. déjà un WebP) désactive juste le redimensionnement, cwebp se
     * chargeant ensuite de rejeter l'entrée si elle n'est vraiment pas une image.
     */
    private int[] lireDimensions(byte[] contenuOriginal) {
        try (ImageInputStream flux = ImageIO.createImageInputStream(new ByteArrayInputStream(contenuOriginal))) {
            if (flux == null) {
                return null;
            }
            Iterator<ImageReader> lecteurs = ImageIO.getImageReaders(flux);
            if (!lecteurs.hasNext()) {
                return null;
            }
            ImageReader lecteur = lecteurs.next();
            try {
                lecteur.setInput(flux);
                return new int[] {lecteur.getWidth(0), lecteur.getHeight(0)};
            } finally {
                lecteur.dispose();
            }
        } catch (IOException ignoree) {
            return null;
        }
    }

    private void supprimerSiPresent(Path chemin) {
        if (chemin == null) {
            return;
        }
        try {
            Files.deleteIfExists(chemin);
        } catch (IOException ignoree) {
            // Fichier temporaire, best-effort — le système nettoiera /tmp de toute façon.
        }
    }
}
