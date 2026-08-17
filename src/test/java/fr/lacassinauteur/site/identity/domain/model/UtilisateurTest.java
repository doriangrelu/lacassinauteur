package fr.lacassinauteur.site.identity.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UtilisateurTest {

    @Test
    void changer_le_mot_de_passe_remplace_le_hache_existant() {
        Utilisateur utilisateur = Utilisateur.creer(new Email("alice@example.com"), new MotDePasseHache("ancien"), Role.ADMIN);

        utilisateur.changerMotDePasse(new MotDePasseHache("nouveau"));

        assertThat(utilisateur.motDePasseHache()).isEqualTo(new MotDePasseHache("nouveau"));
    }
}
