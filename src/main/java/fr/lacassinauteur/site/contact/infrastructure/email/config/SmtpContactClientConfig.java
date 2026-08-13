package fr.lacassinauteur.site.contact.infrastructure.email.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Construit le {@link JavaMailSender} manuellement à partir de {@link SmtpContactProperties}
 * plutôt que de dépendre de l'auto-configuration Spring Boot (conditionnelle à
 * {@code spring.mail.host}, absente tant qu'aucun SMTP réel n'est configuré) — le
 * bean existe donc toujours, y compris avant que les identifiants SMTP réels soient
 * fournis en production (cf. ADR-0014). Même profil que l'adaptateur ({@code !dev}) :
 * inutile en dev, où {@code LogEnvoiEmailContactAdapter} est actif à la place.
 */
@Configuration
@Profile("!dev")
@EnableConfigurationProperties(SmtpContactProperties.class)
public class SmtpContactClientConfig {

    @Bean
    public JavaMailSender contactMailSender(SmtpContactProperties proprietes) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(proprietes.getHost());
        mailSender.setPort(proprietes.getPort());
        mailSender.setUsername(proprietes.getUsername());
        mailSender.setPassword(proprietes.getPassword());

        Properties proprietesJavaMail = mailSender.getJavaMailProperties();
        proprietesJavaMail.put("mail.transport.protocol", "smtp");
        proprietesJavaMail.put("mail.smtp.auth", "true");
        proprietesJavaMail.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }
}
