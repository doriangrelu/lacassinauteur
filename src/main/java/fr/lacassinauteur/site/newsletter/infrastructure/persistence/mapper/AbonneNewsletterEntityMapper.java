package fr.lacassinauteur.site.newsletter.infrastructure.persistence.mapper;

import fr.lacassinauteur.site.newsletter.domain.model.AbonneNewsletter;
import fr.lacassinauteur.site.newsletter.domain.model.Email;
import fr.lacassinauteur.site.newsletter.infrastructure.persistence.entity.AbonneNewsletterJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AbonneNewsletterEntityMapper {

    public AbonneNewsletterJpaEntity versEntite(AbonneNewsletter abonne) {
        return new AbonneNewsletterJpaEntity(
                abonne.id(), abonne.prenom(), abonne.email().valeur(), abonne.statut(),
                abonne.dateInscription(), abonne.dateConfirmation(), abonne.jetonConfirmation());
    }

    public AbonneNewsletter versDomaine(AbonneNewsletterJpaEntity entite) {
        return new AbonneNewsletter(
                entite.getId(), entite.getPrenom(), new Email(entite.getEmail()), entite.getStatut(),
                entite.getDateInscription(), entite.getDateConfirmation(), entite.getJetonConfirmation());
    }
}
