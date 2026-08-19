package fr.lacassinauteur.site.biographie.infrastructure.seed;

import fr.lacassinauteur.site.biographie.domain.model.Biographie;
import fr.lacassinauteur.site.biographie.domain.port.BiographieRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Crée la biographie initiale à partir du contenu réellement fourni par l'auteur
 * (docs/business/source/Texte.docx, section « Page auteur », et la photo
 * Photos/Portraits/Page auteur.jpg). Idempotent — ne fait rien si une biographie
 * existe déjà, y compris après modification par l'auteur, qui ne sera donc jamais
 * écrasée.
 *
 * Même logique que CatalogueInitialContentSeeder : il s'agit du contenu réel du
 * site, pas de données de test, donc actif aussi en production au premier démarrage.
 */
@Component
@Profile("!test")
public class BiographieInitialContentSeeder implements ApplicationRunner {

    private static final String TEXTE_INITIAL = """
            Thierry Lacassin écrit des romans noirs, des enquêtes de territoire et des récits de \
            trajectoires humaines. Ses histoires traversent les lieux, les époques et les milieux, \
            mais reviennent souvent aux mêmes obsessions : la mémoire, les liens familiaux, les \
            secrets, les choix et ce qu'ils laissent derrière eux.

            D'un univers à l'autre, l'écriture reste volontairement sobre, directe et centrée sur \
            les personnages. Les décors changent, les voix aussi, mais une même ligne demeure : \
            raconter des vies prises dans ce qui les dépasse.""";

    private static final String PHOTO_INITIALE = "/images/auteur/thierry-lacassin.jpg";

    private final BiographieRepository biographieRepository;

    public BiographieInitialContentSeeder(BiographieRepository biographieRepository) {
        this.biographieRepository = biographieRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (biographieRepository.charger().isPresent()) {
            return;
        }

        biographieRepository.save(Biographie.initiale(TEXTE_INITIAL, PHOTO_INITIALE));
    }
}
