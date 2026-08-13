package fr.lacassinauteur.site.contact.infrastructure.email;

import fr.lacassinauteur.site.contact.domain.model.MessageContact;
import fr.lacassinauteur.site.contact.domain.port.EnvoiEmailContactPort;
import fr.lacassinauteur.site.contact.infrastructure.email.config.SmtpContactProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Profile("!dev")
public class SmtpEnvoiEmailContactAdapter implements EnvoiEmailContactPort {

    private final JavaMailSender mailSender;
    private final SmtpContactProperties proprietes;
    private final String emailAuteur;

    public SmtpEnvoiEmailContactAdapter(JavaMailSender mailSender, SmtpContactProperties proprietes,
                                         @Value("${app.contact.email-auteur}") String emailAuteur) {
        this.mailSender = mailSender;
        this.proprietes = proprietes;
        this.emailAuteur = emailAuteur;
    }

    @Override
    public void envoyerNotification(MessageContact message) {
        String emailVisiteur = sansRetourLigne(message.email());
        String objet = sansRetourLigne(message.objet());

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(proprietes.getExpediteurNom() + " <" + proprietes.getExpediteurEmail() + ">");
        email.setTo(emailAuteur);
        email.setReplyTo(emailVisiteur);
        email.setSubject("[Site] " + objet);
        email.setText("""
                Nouveau message reçu via le formulaire de contact du site.

                De : %s <%s>
                Objet : %s

                %s""".formatted(message.nom(), emailVisiteur, objet, message.message()));

        mailSender.send(email);
    }

    /**
     * Un objet/email saisi par un visiteur ne doit jamais contenir de retour à la
     * ligne : ces valeurs finissent dans des en-têtes email (Sujet, Reply-To), où un
     * CR/LF injecté pourrait ajouter des en-têtes non prévus.
     */
    private String sansRetourLigne(String valeur) {
        return valeur.replaceAll("[\\r\\n]", " ");
    }
}
