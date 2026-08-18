package fr.lacassinauteur.site.shared.web;

import fr.lacassinauteur.site.shared.domain.exception.ConversionImageEchoueeException;
import fr.lacassinauteur.site.shared.domain.port.ConversionImageWebPPort;
import fr.lacassinauteur.site.shared.infrastructure.stockage.StockageImagesProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

/**
 * Sert les visuels du seed initial (packagés dans le jar sous "static/images/",
 * cf. ADR-0010) en WebP à la volée pour les clients qui l'acceptent, avec un cache
 * disque persistant pour ne convertir chaque image qu'une seule fois — cf. ADR-0024.
 *
 * Remplace entièrement la résolution par défaut de Spring Boot pour ce chemin
 * (contrôleur explicite = priorité plus haute que le gestionnaire de ressources
 * statiques, sans configuration supplémentaire).
 */
@Controller
public class ImageStatiqueController {

    private static final Logger LOG = LoggerFactory.getLogger(ImageStatiqueController.class);
    private static final String PREFIXE_CLASSPATH = "static/images/";
    private static final String SOUS_DOSSIER_CACHE = ".cache-webp";
    private static final Duration DUREE_CACHE_NAVIGATEUR = Duration.ofDays(30);

    private final ConversionImageWebPPort conversionImageWebP;
    private final Path dossierCache;

    public ImageStatiqueController(ConversionImageWebPPort conversionImageWebP, StockageImagesProperties proprietesStockage) {
        this.conversionImageWebP = conversionImageWebP;
        this.dossierCache = Path.of(proprietesStockage.getChemin()).resolve(SOUS_DOSSIER_CACHE).normalize();
    }

    @GetMapping("/images/{*chemin}")
    public ResponseEntity<Resource> servir(@PathVariable String chemin,
            @RequestHeader(value = HttpHeaders.ACCEPT, required = false) String accept) {
        String cheminRelatif = normaliserChemin(chemin);
        if (cheminRelatif == null) {
            return ResponseEntity.notFound().build();
        }

        ClassPathResource source = new ClassPathResource(PREFIXE_CLASSPATH + cheminRelatif);
        if (!source.exists()) {
            return ResponseEntity.notFound().build();
        }

        boolean clientAccepteWebp = accept != null && accept.contains("image/webp");
        if (clientAccepteWebp) {
            try {
                Resource webp = depuisCacheOuConverti(source, cheminRelatif);
                return ResponseEntity.ok()
                        .contentType(MediaType.valueOf("image/webp"))
                        .cacheControl(CacheControl.maxAge(DUREE_CACHE_NAVIGATEUR).cachePublic())
                        .body(webp);
            } catch (ConversionImageEchoueeException | IOException exception) {
                LOG.warn("Conversion/lecture du cache WebP impossible pour {}, envoi du format d'origine : {}",
                        cheminRelatif, exception.getMessage());
            }
        }

        return servirTelQuel(source);
    }

    private ResponseEntity<Resource> servirTelQuel(ClassPathResource source) {
        MediaType type = MediaTypeFactory.getMediaType(source).orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .contentType(type)
                .cacheControl(CacheControl.maxAge(DUREE_CACHE_NAVIGATEUR).cachePublic())
                .body(source);
    }

    private Resource depuisCacheOuConverti(ClassPathResource source, String cheminRelatif) throws IOException {
        Path fichierCache = dossierCache.resolve(cheminRelatif + ".webp").normalize();
        if (!fichierCache.startsWith(dossierCache)) {
            throw new ConversionImageEchoueeException("Chemin de cache hors du dossier attendu : " + cheminRelatif);
        }

        if (Files.exists(fichierCache)) {
            return new FileSystemResource(fichierCache);
        }

        byte[] original = source.getInputStream().readAllBytes();
        byte[] webp = conversionImageWebP.convertirEnWebp(original);

        Files.createDirectories(fichierCache.getParent());
        Files.write(fichierCache, webp);
        return new ByteArrayResource(webp);
    }

    /**
     * Refuse tout ce qui pourrait sortir de "static/images/" (segments ".."),
     * même logique défensive que StockageFichierLocal.supprimerSiGere.
     */
    private String normaliserChemin(String chemin) {
        if (chemin == null || chemin.isBlank()) {
            return null;
        }
        String sansSlashInitial = chemin.startsWith("/") ? chemin.substring(1) : chemin;
        Path normalise = Path.of(sansSlashInitial).normalize();
        if (normalise.isAbsolute() || normalise.startsWith("..") || normalise.toString().contains("..")) {
            return null;
        }
        return normalise.toString().replace('\\', '/');
    }
}
