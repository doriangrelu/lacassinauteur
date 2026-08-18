package fr.lacassinauteur.site.shared.infrastructure.image;

import fr.lacassinauteur.site.shared.domain.exception.ConversionImageEchoueeException;
import fr.lacassinauteur.site.shared.domain.port.ConversionImageWebPPort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    @Override
    public byte[] convertirEnWebp(byte[] contenuOriginal) {
        Path entree = null;
        Path sortie = null;
        try {
            entree = Files.createTempFile("webp-in-", ".src");
            sortie = Files.createTempFile("webp-out-", ".webp");
            Files.write(entree, contenuOriginal);

            Process processus = new ProcessBuilder(
                    "cwebp", "-quiet", "-q", String.valueOf(QUALITE),
                    entree.toString(), "-o", sortie.toString())
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
