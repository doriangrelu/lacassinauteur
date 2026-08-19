package fr.lacassinauteur.site.legal.infrastructure.seed;

import fr.lacassinauteur.site.legal.domain.model.InformationsLegales;
import fr.lacassinauteur.site.legal.domain.port.InformationsLegalesRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Crée l'enregistrement unique au premier démarrage, avec les seules valeurs
 * connues sans intervention humaine : l'hébergeur (le VPS est chez OVHcloud, cf.
 * ADR-0016) et des durées de conservation par défaut.
 *
 * L'identité de l'éditeur est volontairement laissée <strong>vide</strong> : elle
 * relève d'informations juridiques réelles que seul l'auteur peut fournir. Les
 * pages légales signalent alors explicitement ce qui manque, plutôt que d'afficher
 * une identité inventée — cf. ADR-0029.
 */
@Component
@Profile("!test")
public class InformationsLegalesSeeder implements ApplicationRunner {

    private static final String HEBERGEUR_NOM = "OVH SAS";
    private static final String HEBERGEUR_ADRESSE = "2 rue Kellermann, 59100 Roubaix, France";

    /** 3 ans après le dernier contact, recommandation CNIL usuelle en prospection. */
    private static final int CONSERVATION_NEWSLETTER_MOIS = 36;

    /** 1 an après traitement du message, durée courante pour une demande simple. */
    private static final int CONSERVATION_CONTACT_MOIS = 12;

    private final InformationsLegalesRepository informationsLegalesRepository;

    public InformationsLegalesSeeder(InformationsLegalesRepository informationsLegalesRepository) {
        this.informationsLegalesRepository = informationsLegalesRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (informationsLegalesRepository.charger().isPresent()) {
            return;
        }

        informationsLegalesRepository.save(InformationsLegales.initiales(
                HEBERGEUR_NOM, HEBERGEUR_ADRESSE, CONSERVATION_NEWSLETTER_MOIS, CONSERVATION_CONTACT_MOIS));
    }
}
