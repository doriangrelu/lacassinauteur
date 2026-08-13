package fr.lacassinauteur.site.contact.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class MessageContact {

    private final UUID id;
    private final String nom;
    private final String email;
    private final String objet;
    private final String message;
    private final LocalDateTime dateReception;
    private StatutMessage statut;

    public MessageContact(UUID id, String nom, String email, String objet, String message,
                           LocalDateTime dateReception, StatutMessage statut) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.objet = objet;
        this.message = message;
        this.dateReception = dateReception;
        this.statut = statut;
    }

    public static MessageContact creer(String nom, String email, String objet, String message) {
        return new MessageContact(UUID.randomUUID(), nom, email, objet, message, LocalDateTime.now(), StatutMessage.NOUVEAU);
    }

    /** Marque le message comme lu, sans écraser un statut déjà plus avancé (TRAITE). */
    public void marquerLu() {
        if (statut == StatutMessage.NOUVEAU) {
            statut = StatutMessage.LU;
        }
    }

    public void marquerTraite() {
        statut = StatutMessage.TRAITE;
    }

    public UUID id() {
        return id;
    }

    public String nom() {
        return nom;
    }

    public String email() {
        return email;
    }

    public String objet() {
        return objet;
    }

    public String message() {
        return message;
    }

    public LocalDateTime dateReception() {
        return dateReception;
    }

    public StatutMessage statut() {
        return statut;
    }
}
