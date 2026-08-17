package fr.lacassinauteur.site.identity.infrastructure.security;

import fr.lacassinauteur.site.identity.domain.exception.JetonReinitialisationInvalideException;
import fr.lacassinauteur.site.identity.domain.port.JetonReinitialisationMotDePassePort;
import fr.lacassinauteur.site.identity.infrastructure.security.config.JwtReinitialisationProperties;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtJetonReinitialisationAdapterTest {

    private JwtReinitialisationProperties proprietes(long dureeValiditeMinutes) {
        JwtReinitialisationProperties proprietes = new JwtReinitialisationProperties();
        proprietes.setSecret("secret-de-test-largement-superieur-a-32-caracteres-pour-hs256");
        proprietes.setDureeValiditeMinutes(dureeValiditeMinutes);
        return proprietes;
    }

    @Test
    void genere_puis_valide_un_jeton_et_retrouve_lid_utilisateur_et_du_jeton() {
        JwtJetonReinitialisationAdapter adapter = new JwtJetonReinitialisationAdapter(proprietes(15));
        UUID utilisateurId = UUID.randomUUID();
        UUID jetonId = UUID.randomUUID();

        String jeton = adapter.genererJeton(utilisateurId, jetonId);
        JetonReinitialisationMotDePassePort.JetonDecode decode = adapter.validerEtExtraire(jeton);

        assertThat(decode.utilisateurId()).isEqualTo(utilisateurId);
        assertThat(decode.jetonId()).isEqualTo(jetonId);
    }

    @Test
    void refuse_un_jeton_malforme() {
        JwtJetonReinitialisationAdapter adapter = new JwtJetonReinitialisationAdapter(proprietes(15));

        assertThatThrownBy(() -> adapter.validerEtExtraire("ceci-nest-pas-un-jwt"))
                .isInstanceOf(JetonReinitialisationInvalideException.class);
    }

    @Test
    void refuse_un_jeton_signe_avec_un_autre_secret() {
        JwtJetonReinitialisationAdapter emetteur = new JwtJetonReinitialisationAdapter(proprietes(15));
        String jeton = emetteur.genererJeton(UUID.randomUUID(), UUID.randomUUID());

        JwtReinitialisationProperties autreSecret = proprietes(15);
        autreSecret.setSecret("un-secret-totalement-different-lui-aussi-assez-long-pour-hs256");
        JwtJetonReinitialisationAdapter verificateur = new JwtJetonReinitialisationAdapter(autreSecret);

        assertThatThrownBy(() -> verificateur.validerEtExtraire(jeton))
                .isInstanceOf(JetonReinitialisationInvalideException.class);
    }

    @Test
    void refuse_un_jeton_expire() throws InterruptedException {
        JwtReinitialisationProperties expirationImmediate = proprietes(0);
        JwtJetonReinitialisationAdapter adapter = new JwtJetonReinitialisationAdapter(expirationImmediate);
        String jeton = adapter.genererJeton(UUID.randomUUID(), UUID.randomUUID());

        Thread.sleep(1000);

        assertThatThrownBy(() -> adapter.validerEtExtraire(jeton))
                .isInstanceOf(JetonReinitialisationInvalideException.class);
    }
}
