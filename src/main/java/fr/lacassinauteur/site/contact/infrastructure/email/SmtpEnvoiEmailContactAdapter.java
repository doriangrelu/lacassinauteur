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
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(proprietes.getExpediteurEmail());
        email.setTo(emailAuteur);
        email.setReplyTo(message.email());
        email.setSubject("[Site] " + message.objet());
        email.setText("""
                Nouveau message reçu via le formulaire de contact du site.

                De : %s <%s>
                Objet : %s

                %s""".formatted(message.nom(), message.email(), message.objet(), message.message()));

        mailSender.send(email);
    }
}
