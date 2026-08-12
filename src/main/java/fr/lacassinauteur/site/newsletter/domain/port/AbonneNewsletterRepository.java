package fr.lacassinauteur.site.newsletter.domain.port;

import fr.lacassinauteur.site.newsletter.domain.model.AbonneNewsletter;
import fr.lacassinauteur.site.newsletter.domain.model.Email;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AbonneNewsletterRepository {

    AbonneNewsletter save(AbonneNewsletter abonne);

    Optional<AbonneNewsletter> findById(UUID id);

    Optional<AbonneNewsletter> findByEmail(Email email);

    Optional<AbonneNewsletter> findByJeton(UUID jetonConfirmation);

    boolean existsByEmail(Email email);

    List<AbonneNewsletter> findAllOrderByDateInscriptionDesc();

    /**
     * Abonnés au statut {@code CONFIRME} uniquement. Non consommé par un use case en
     * v1 (aucune synchronisation ESP ni envoi de campagne dans ce lot, cf.
     * ADR-0013/domain-model.md) — préparé pour cet usage futur plutôt qu'ajouté après
     * coup, le port définissant le contrat complet du repository.
     */
    List<AbonneNewsletter> findAllConfirmes();
}
