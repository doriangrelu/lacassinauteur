package fr.lacassinauteur.site.catalogue.presentation.backoffice;

import fr.lacassinauteur.site.catalogue.application.usecase.livre.GenererQrCodeFicheProUseCase;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Sert le QR code de la fiche professionnelle d'un livre. Contrôleur dédié plutôt
 * qu'une méthode de plus sur BackofficeLivreController : celui-ci porte déjà neuf
 * dépendances, et servir un fichier binaire n'a rien à voir avec le CRUD de livres.
 */
@Controller
@RequestMapping("/backoffice/livres")
public class BackofficeQrCodeFicheProController {

    private final GenererQrCodeFicheProUseCase genererQrCodeFicheProUseCase;

    public BackofficeQrCodeFicheProController(GenererQrCodeFicheProUseCase genererQrCodeFicheProUseCase) {
        this.genererQrCodeFicheProUseCase = genererQrCodeFicheProUseCase;
    }

    @GetMapping(value = "/{id}/qr-code.svg", produces = "image/svg+xml")
    public ResponseEntity<byte[]> qrCode(@PathVariable UUID id) {
        byte[] svg = genererQrCodeFicheProUseCase.execute(id).getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/svg+xml"))
                // Regénéré à chaque appel (quelques millisecondes) mais toujours
                // identique pour un slug donné : on laisse le navigateur le mettre
                // en cache le temps de la session d'édition.
                .header(HttpHeaders.CACHE_CONTROL, "private, max-age=3600")
                .body(svg);
    }
}
