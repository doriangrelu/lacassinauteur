package fr.lacassinauteur.site.catalogue.infrastructure.seed;

import fr.lacassinauteur.site.catalogue.domain.model.Collection;
import fr.lacassinauteur.site.catalogue.domain.model.LienAchat;
import fr.lacassinauteur.site.catalogue.domain.model.Livre;
import fr.lacassinauteur.site.catalogue.domain.model.Univers;
import fr.lacassinauteur.site.catalogue.domain.port.CollectionRepository;
import fr.lacassinauteur.site.catalogue.domain.port.LivreRepository;
import fr.lacassinauteur.site.catalogue.domain.port.UniversRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Importe le catalogue réel de Thierry Lacassin (2 univers, 4 collections, 7 livres)
 * fourni dans docs/business/source. Idempotent (ne fait rien si des univers existent
 * déjà) — tourne dans tous les environnements, y compris en production au premier
 * démarrage, car il s'agit du contenu réel du site, pas de données de test.
 */
@Component
@Profile("!test")
public class CatalogueInitialContentSeeder implements ApplicationRunner {

    private final UniversRepository universRepository;
    private final CollectionRepository collectionRepository;
    private final LivreRepository livreRepository;

    public CatalogueInitialContentSeeder(UniversRepository universRepository, CollectionRepository collectionRepository,
                                          LivreRepository livreRepository) {
        this.universRepository = universRepository;
        this.collectionRepository = collectionRepository;
        this.livreRepository = livreRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!universRepository.findAllOrderByOrdre().isEmpty()) {
            return;
        }

        Univers universNoir = universRepository.save(Univers.creer(
                "lacassin-noir-territoire",
                "LACASSIN noir & territoire",
                "Deux univers. Une seule façon d'écrire.",
                "Dans cet univers, les histoires remontent aux origines avant de devenir des enquêtes. "
                        + "LACASSIN noir explore les familles, les héritages, les fidélités, la violence et les choix "
                        + "qui construisent les hommes. LACASSIN territoire suit Eugène Cazal lorsqu'il revient sur les "
                        + "traces laissées par ces vies, dans des lieux où le passé n'a jamais tout à fait disparu. "
                        + "Deux collections différentes, reliées par la même question : qu'est-ce que les hommes "
                        + "laissent derrière eux ?",
                "/images/univers/sobriete.jpg",
                1));

        Univers universTrajectoire = universRepository.save(Univers.creer(
                "lacassin-trajectoire-ailleurs",
                "LACASSIN trajectoire & ailleurs",
                "Deux univers. Une seule façon d'écrire.",
                "Dans cet univers, les histoires quittent les cadres établis pour suivre des regards, des "
                        + "déplacements et des trajectoires. LACASSIN trajectoire explore le monde à travers Camille B., "
                        + "ses rencontres, ses voyages et sa manière d'observer ce qui l'entoure. LACASSIN ailleurs "
                        + "ouvre la porte à des récits plus libres, où les lieux, les époques et les formes peuvent "
                        + "changer sans contrainte. Deux collections différentes, reliées par une même envie : aller "
                        + "voir ce qui se passe plus loin.",
                "/images/univers/urbain-graffiti.png",
                2));

        Collection lacassinNoir = collectionRepository.save(Collection.creer(
                "lacassin-noir", universNoir.id(), "LACASSIN noir", "Les Origines",
                "LACASSIN noir explore les origines : celles des familles, des alliances, des fidélités et des "
                        + "ruptures. On y suit des hommes et des femmes façonnés par leur milieu, leurs choix, la "
                        + "violence, le respect et ce qu'ils sont prêts à perdre pour conserver leur place. Ici, le "
                        + "crime n'est jamais seulement une affaire de faits. Il naît d'une histoire, d'un héritage, "
                        + "d'un lien. Les Origines, c'est l'endroit où tout commence.",
                1));

        Collection lacassinTerritoire = collectionRepository.save(Collection.creer(
                "lacassin-territoire", universNoir.id(), "LACASSIN territoire", "Les enquêtes d'Eugène Cazal",
                "LACASSIN territoire suit les enquêtes d'Eugène Cazal, commissaire attentif aux traces que les "
                        + "hommes laissent derrière eux. D'un territoire à l'autre, il remonte des histoires anciennes, "
                        + "des silences, des secrets et des faits que le temps n'a pas effacés. Ici, les lieux ne sont "
                        + "jamais de simples décors. Ils gardent une mémoire, parfois enfouie, parfois encore vive. "
                        + "Les enquêtes d'Eugène Cazal, là où le passé finit toujours par refaire surface.",
                2));

        Collection lacassinTrajectoire = collectionRepository.save(Collection.creer(
                "lacassin-trajectoire", universTrajectoire.id(), "LACASSIN trajectoire", "Le monde selon Camille B.",
                "LACASSIN trajectoire suit Camille B. au fil des lieux, des rencontres et des époques. "
                        + "Journaliste, observateur, photographe, il avance en regardant le monde autant qu'en s'y "
                        + "laissant entraîner. Chaque récit naît d'un déplacement, d'un visage, d'un détail aperçu au "
                        + "bon moment, puis devient une histoire de trajectoire humaine. Le monde selon Camille B., vu "
                        + "depuis l'intérieur, mais jamais tout à fait de face.",
                1));

        Collection lacassinAilleurs = collectionRepository.save(Collection.creer(
                "lacassin-ailleurs", universTrajectoire.id(), "LACASSIN ailleurs", "Où tout est permis",
                "LACASSIN ailleurs accueille les récits qui refusent de rester dans un cadre défini. Ici, les "
                        + "lieux, les époques, les voix et les formes peuvent changer librement, au gré de l'histoire. "
                        + "Ce sont des romans de déplacement, de rupture, de musique, de mémoire ou de hasard, avec une "
                        + "seule règle : laisser le récit aller là où il doit aller. Où tout est permis, sauf de "
                        + "raconter sans nécessité.",
                2));

        Livre liensDuCrime = Livre.creer(
                "les-liens-du-crime", lacassinNoir.id(), "Les liens du crime", null,
                "/images/couvertures/les-liens-du-crime.png",
                "Autour d'un héritage criminel, les liens se nouent par la famille, la loyauté, l'intérêt ou la "
                        + "peur. Et chacun finit par payer le prix de ses choix.",
                "Julien Autel croyait avoir laissé Lyon et son passé derrière lui. Installé à Cancheville avec "
                        + "Stéphanie et leur fille, il mène une vie presque ordinaire jusqu'au matin où les gendarmes "
                        + "frappent à sa porte. Une arrestation banale en apparence suffit à rouvrir ce qu'il avait "
                        + "tenté d'enterrer.\n\n"
                        + "Dans son sillage réapparaît Franck Keller, figure du milieu lyonnais et homme auquel Julien "
                        + "doit une partie de ce qu'il est devenu. Avec lui remontent les anciennes fidélités, les "
                        + "dettes, les rivalités et les choix jamais vraiment oubliés. À Lyon, Eugène Cazal reprend lui "
                        + "aussi de vieux dossiers et comprend que plusieurs histoires qu'il croyait terminées "
                        + "recommencent à se croiser.\n\n"
                        + "Autour d'eux, Stéphanie, Sylvie et ceux qui gravitent encore dans ce monde vont devoir "
                        + "choisir leur place. Car certains liens survivent à l'éloignement, aux années et aux "
                        + "trahisons. Et lorsque le passé revient réclamer son dû, chacun découvre jusqu'où il est "
                        + "prêt à aller pour protéger ce qu'il lui reste.",
                1);
        liensDuCrime.publier(new LienAchat("https://www.amazon.fr/dp/B0HCJN9QGN", "Amazon"));
        livreRepository.save(liensDuCrime);

        Livre franckAKeller = Livre.creer(
                "de-franck-a-keller", lacassinNoir.id(), "De Franck à Keller", "À la recherche du respect",
                "/images/couvertures/de-franck-a-keller.png",
                "Depuis sa cellule, Franck Keller raconte le chemin qui l'a mené de l'adolescence au milieu du "
                        + "crime. Une vie construite autour d'une obsession : le respect.",
                "Depuis sa cellule, Franck Keller revient sur sa vie. Pas pour se justifier, ni pour demander "
                        + "pardon, mais pour raconter à sa fille comment il est devenu l'homme qu'elle connaît.\n\n"
                        + "De son adolescence marquée par la violence aux premières rencontres qui vont façonner son "
                        + "avenir, Franck apprend très tôt que le respect ne se reçoit pas, il se gagne, parfois au "
                        + "prix fort. Peu à peu, il entre dans un monde régi par ses propres règles, où la loyauté, la "
                        + "peur, les alliances et les silences comptent davantage que la loi.\n\n"
                        + "Au fil des années, celui que l'on appelait encore Franck devient Keller. Une transformation "
                        + "lente, presque inévitable, faite de choix successifs, de fidélités et de renoncements.\n\n"
                        + "Écrit dans une langue sèche, directe, sans détour, De Franck à Keller raconte la "
                        + "construction d'un homme autant que son entrée dans le crime, jusqu'au point où l'identité "
                        + "prend définitivement le pas sur celui qu'il était.",
                2);
        franckAKeller.publier(new LienAchat("https://www.amazon.fr/dp/B0HCPH3YTY", "Amazon"));
        livreRepository.save(franckAKeller);

        Livre hommeDeSarenne = Livre.creer(
                "homme-de-sarenne", lacassinTerritoire.id(), "L'Homme de Sarenne", "Alpe d'Huez 3 300 mètres",
                "/images/couvertures/homme-de-sarenne.png",
                "À l'Alpe d'Huez, la découverte d'un corps dans un glacier réveille une affaire ancienne. Eugène "
                        + "Cazal remonte le fil d'un secret que la montagne avait gardé pendant des décennies.",
                "À l'Alpe d'Huez, la découverte d'un corps prisonnier des glaces fait ressurgir une affaire que "
                        + "la montagne semblait avoir engloutie depuis longtemps.\n\n"
                        + "Eugène Cazal se retrouve entraîné dans une enquête où les traces du passé sont rares, les "
                        + "souvenirs fragiles et les silences tenaces. Entre les archives, les témoignages et les "
                        + "secrets que chacun préfère laisser enfouis, il remonte peu à peu le fil d'une histoire "
                        + "ancienne.\n\n"
                        + "Mais en montagne, le temps ne fait pas disparaître les choses. Il les conserve. Et ce que "
                        + "la glace finit par rendre peut bouleverser bien plus qu'une enquête.\n\n"
                        + "L'Homme de Sarenne est un roman de mémoire et de territoire, où le paysage devient "
                        + "lui-même gardien du secret.",
                1);
        hommeDeSarenne.publier(new LienAchat("https://amzn.eu/d/0aNxHRs9", "Amazon"));
        livreRepository.save(hommeDeSarenne);

        Livre jugementDuSilence = Livre.creer(
                "le-jugement-du-silence", lacassinTerritoire.id(), "Le jugement du silence", "Le vaisseau des Cévennes",
                "/images/couvertures/le-jugement-du-silence.png",
                "Dans un village cévenol, une vérité enfouie depuis la Libération refait surface. Eugène Cazal "
                        + "découvre qu'un homme condamné comme traître ne l'était peut-être pas, et que certains "
                        + "silences ont traversé les générations.",
                "Dans un village cévenol, une vieille histoire née à la Libération revient troubler le présent. "
                        + "Un homme autrefois désigné comme traître pourrait avoir été condamné à tort, et derrière "
                        + "cette injustice se cachent des silences que personne n'a vraiment voulu rompre.\n\n"
                        + "Eugène Cazal s'engage dans une enquête où les souvenirs sont incomplets, les versions "
                        + "contradictoires et les témoins marqués par ce qu'ils ont choisi de taire. À mesure qu'il "
                        + "remonte le fil des événements, il découvre qu'un secret ancien peut encore peser sur "
                        + "plusieurs générations.\n\n"
                        + "Entre mémoire, culpabilité et loyautés enfouies, Le Jugement du silence explore ce que le "
                        + "temps n'efface pas et ce que les hommes préfèrent parfois laisser dans l'ombre.",
                2);
        jugementDuSilence.publier(new LienAchat("https://amzn.eu/d/0dwUJiaN", "Amazon"));
        livreRepository.save(jugementDuSilence);

        Livre camilleJournaliste = Livre.creer(
                "moi-camille-b-journaliste", lacassinTrajectoire.id(), "Moi, Camille B. Journaliste", "Gard, années 80",
                "/images/couvertures/moi-camille-b-journaliste.png",
                "Dans le Gard des années 80, Camille B., jeune journaliste, découvre qu'un même fil relie "
                        + "plusieurs histoires en apparence ordinaires : l'eau. En enquêtant, il comprend que derrière "
                        + "les paysages familiers se cachent des vérités que certains préfèrent laisser enfouies.",
                "Dans le Gard des années 80, Camille B. débute comme journaliste et apprend son métier au fil "
                        + "des rencontres, des faits divers et des histoires que le territoire lui offre.\n\n"
                        + "Peu à peu, un même élément revient derrière des situations qui semblent pourtant n'avoir "
                        + "aucun lien entre elles : l'eau. Celle que l'on boit, celle qui traverse les villages, celle "
                        + "autour de laquelle circulent les habitudes, les silences et parfois les inquiétudes.\n\n"
                        + "En suivant ce fil discret, Camille comprend que le travail d'un journaliste ne consiste "
                        + "pas seulement à rapporter ce qu'on lui dit, mais à regarder ce qui se répète, à écouter ce "
                        + "qui n'est pas formulé et à poser la question que personne ne semblait vouloir poser.\n\n"
                        + "Moi, Camille B., journaliste – Gard, années 80 raconte les débuts d'un regard, dans un "
                        + "territoire où l'ordinaire finit par révéler ce qu'il cachait.",
                1);
        livreRepository.save(camilleJournaliste);

        Livre camillePhotographe = Livre.creer(
                "camille-b-photographe-ordinaire", lacassinTrajectoire.id(), "Camille B. Un photographe ordinaire", "Londres, années 90",
                "/images/couvertures/camille-b-photographe-ordinaire.png",
                "À Londres, au début des années 90, Camille B. traverse Camden, les squats et les marges de la "
                        + "ville avec son appareil photo. D'abord observateur, il finit peu à peu par devenir partie "
                        + "prenante de ce qu'il photographie.",
                "Londres, début des années 90. Camille B. parcourt la ville avec son appareil photo, attiré par "
                        + "Camden, les squats, la musique et ceux qui vivent en marge. Il photographie d'abord pour "
                        + "observer, garder une trace, saisir ce qui échappe aux autres.\n\n"
                        + "Sa rencontre avec Stacey l'entraîne pourtant bien au-delà de ce qu'il était venu chercher. "
                        + "À travers elle, Camille découvre la réalité de femmes battues, la peur, l'emprise et les "
                        + "silences qui entourent les violences qu'elles subissent. Peu à peu, il ne peut plus se "
                        + "contenter de regarder.\n\n"
                        + "Entre photographie, musique, vies en marge et souffrances invisibles, Camille B. – Un "
                        + "photographe ordinaire, Londres années 90 raconte le passage d'un homme du statut "
                        + "d'observateur à celui de témoin impliqué, jusqu'au moment où photographier ne suffit "
                        + "plus.",
                2);
        livreRepository.save(camillePhotographe);

        Livre rameEtLaRage = Livre.creer(
                "la-rame-et-la-rage", lacassinAilleurs.id(), "La rame et la rage", null,
                "/images/couvertures/la-rame-et-la-rage.png",
                "À Brooklyn, un ancien conducteur de métro raconte à un journaliste français l'été 1973, quand "
                        + "New York grondait déjà d'une musique nouvelle. Entre violence, amitiés et concerts, une "
                        + "bande de jeunes cherche sa place au moment même où le punk commence à prendre feu.",
                "Sur Bowery, devant un magasin de fringues branché, Jérémy, journaliste français, rencontre "
                        + "Steve, ancien conducteur du métro new-yorkais. Pour lui raconter son histoire, Steve "
                        + "l'entraîne dans une rame de la ligne F.\n\n"
                        + "Le trajet devient alors un passage vers l'été 1973, dans un New York violent, sale, "
                        + "électrique, où Steve est encore jeune et cherche sa place entre musique, petits boulots, "
                        + "familles cabossées et nuits de concerts. Quelque chose est en train de naître dans les "
                        + "clubs et les rues, une énergie nouvelle que personne ne sait encore vraiment nommer.\n\n"
                        + "Au fil des stations et des souvenirs, Jérémy comprend qu'il n'écoute pas seulement le "
                        + "récit d'un homme, mais celui d'une génération qui assiste aux premiers grondements de ce "
                        + "qui deviendra le punk.\n\n"
                        + "La rame et la rage raconte la naissance d'une colère, d'une musique et d'une manière de "
                        + "vivre, dans le New York de 1973.",
                1);
        livreRepository.save(rameEtLaRage);

        franckAKeller.marquerCommeDerniereParution();
        livreRepository.save(franckAKeller);
    }
}
