package fr.lacassinauteur.site.config;

import fr.lacassinauteur.site.identity.infrastructure.security.LoginRateLimiter;
import fr.lacassinauteur.site.identity.infrastructure.security.LoginRateLimitingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final String PAGE_CONNEXION = "/backoffice/connexion";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, LoginRateLimiter loginRateLimiter) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(PAGE_CONNEXION, "/backoffice/mot-de-passe-oublie", "/backoffice/reinitialiser-mot-de-passe")
                        .permitAll()
                        .requestMatchers("/backoffice/**").hasAnyRole("ADMIN", "AUTEUR")
                        .anyRequest().permitAll())
                .formLogin(form -> form
                        .loginPage(PAGE_CONNEXION)
                        .loginProcessingUrl(PAGE_CONNEXION)
                        .defaultSuccessUrl("/backoffice/comptes", true)
                        .failureUrl(PAGE_CONNEXION + "?erreur")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/backoffice/deconnexion")
                        .logoutSuccessUrl(PAGE_CONNEXION + "?deconnecte"))
                .addFilterBefore(
                        new LoginRateLimitingFilter(loginRateLimiter, PAGE_CONNEXION),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
