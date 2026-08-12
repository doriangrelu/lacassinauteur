package fr.lacassinauteur.site.identity.infrastructure.persistence.adapter;

import fr.lacassinauteur.site.identity.domain.model.Email;
import fr.lacassinauteur.site.identity.domain.model.MotDePasseHache;
import fr.lacassinauteur.site.identity.domain.model.Role;
import fr.lacassinauteur.site.identity.domain.model.Utilisateur;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class JpaUtilisateurRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private JpaUtilisateurRepository repository;

    @Test
    void sauvegarde_et_retrouve_un_utilisateur_par_email() {
        Utilisateur utilisateur = Utilisateur.creer(
                new Email("thierry@lacassinauteur.local"), new MotDePasseHache("hache"), Role.AUTEUR);

        repository.save(utilisateur);

        Optional<Utilisateur> retrouve = repository.findByEmail(new Email("thierry@lacassinauteur.local"));

        assertThat(retrouve).isPresent();
        assertThat(retrouve.get().role()).isEqualTo(Role.AUTEUR);
        assertThat(retrouve.get().actif()).isTrue();
    }

    @Test
    void existsByEmail_reflete_les_utilisateurs_sauvegardes() {
        assertThat(repository.existsByEmail(new Email("inconnu@lacassinauteur.local"))).isFalse();

        repository.save(Utilisateur.creer(
                new Email("inconnu@lacassinauteur.local"), new MotDePasseHache("hache"), Role.ADMIN));

        assertThat(repository.existsByEmail(new Email("inconnu@lacassinauteur.local"))).isTrue();
    }
}
