package fr.lacassinauteur.site.shared.infrastructure.stockage;

import fr.lacassinauteur.site.shared.domain.exception.FichierInvalideException;
import fr.lacassinauteur.site.shared.domain.port.StockageFichierPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Component
public class StockageFichierLocal implements StockageFichierPort {

    private static final Logger LOG = LoggerFactory.getLogger(StockageFichierLocal.class);
    private static final List<String> EXTENSIONS_AUTORISEES = List.of("jpg", "jpeg", "png", "webp", "gif");

    private final StockageImagesProperties proprietes;

    public StockageFichierLocal(StockageImagesProperties proprietes) {
        this.proprietes = proprietes;
    }

    @Override
    public String enregistrer(byte[] contenu, String nomOriginalFichier, String sousDossier) {
        String extension = extraireExtensionValidee(nomOriginalFichier);
        String nomGenere = UUID.randomUUID() + "." + extension;

        Path dossier = Path.of(proprietes.getChemin()).resolve(sousDossier).normalize();
        Path cible = dossier.resolve(nomGenere);

        try {
            Files.createDirectories(dossier);
            Files.write(cible, contenu);
        } catch (IOException exception) {
            throw new UncheckedIOException("Impossible d'enregistrer le fichier " + nomOriginalFichier, exception);
        }

        return proprietes.getPrefixeUrl() + "/" + sousDossier + "/" + nomGenere;
    }

    @Override
    public void supprimerSiGere(String url) {
        if (url == null || !url.startsWith(proprietes.getPrefixeUrl() + "/")) {
            return;
        }

        try {
            Path base = Path.of(proprietes.getChemin()).normalize().toAbsolutePath();
            String cheminRelatif = url.substring(proprietes.getPrefixeUrl().length() + 1);
            Path cible = base.resolve(cheminRelatif).normalize().toAbsolutePath();

            if (!cible.startsWith(base)) {
                LOG.warn("Suppression ignorée, chemin hors du dossier de stockage : {}", url);
                return;
            }

            Files.deleteIfExists(cible);
        } catch (IOException exception) {
            LOG.warn("Échec de la suppression du fichier {} : {}", url, exception.getMessage());
        }
    }

    private String extraireExtensionValidee(String nomOriginalFichier) {
        if (nomOriginalFichier == null || !nomOriginalFichier.contains(".")) {
            throw new FichierInvalideException("Le fichier doit avoir une extension");
        }

        String extension = nomOriginalFichier.substring(nomOriginalFichier.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);

        if (!EXTENSIONS_AUTORISEES.contains(extension)) {
            throw new FichierInvalideException(
                    "Format non supporté (" + extension + "), formats acceptés : " + EXTENSIONS_AUTORISEES);
        }

        return extension;
    }
}
