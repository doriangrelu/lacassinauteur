package fr.lacassinauteur.site.contact.infrastructure.email.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration SMTP de l'envoi des notifications de contact (cf. ADR-0014).
 * Volontairement générique (host/port/identifiants) plutôt que verrouillée sur un
 * fournisseur précis : fonctionne aussi bien avec le compte Gmail existant de
 * l'auteur (mot de passe d'application) qu'avec un relais SMTP transactionnel — le
 * choix définitif reste une décision opérationnelle de déploiement, pas
 * architecturale.
 */
@ConfigurationProperties(prefix = "app.contact.smtp")
public class SmtpContactProperties {

    private String host = "";
    private int port = 587;
    private String username = "";
    private String password = "";
    private String expediteurEmail = "contact@thierrylacassin-auteur.fr";
    private String expediteurNom = "Site Thierry Lacassin";

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getExpediteurEmail() {
        return expediteurEmail;
    }

    public void setExpediteurEmail(String expediteurEmail) {
        this.expediteurEmail = expediteurEmail;
    }

    public String getExpediteurNom() {
        return expediteurNom;
    }

    public void setExpediteurNom(String expediteurNom) {
        this.expediteurNom = expediteurNom;
    }
}
