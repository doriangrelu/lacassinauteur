package fr.lacassinauteur.site.identity.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class LoginRateLimitingFilter extends OncePerRequestFilter {

    private final LoginRateLimiter loginRateLimiter;
    private final String loginProcessingUrl;

    public LoginRateLimitingFilter(LoginRateLimiter loginRateLimiter, String loginProcessingUrl) {
        this.loginRateLimiter = loginRateLimiter;
        this.loginProcessingUrl = loginProcessingUrl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        boolean estTentativeDeConnexion = "POST".equalsIgnoreCase(request.getMethod())
                && loginProcessingUrl.equals(request.getServletPath());

        if (estTentativeDeConnexion) {
            String ip = request.getRemoteAddr();
            String identifiant = request.getParameter("username");

            if (!loginRateLimiter.autoriserTentative(ip, identifiant == null ? "" : identifiant)) {
                response.setStatus(429);
                response.setContentType("text/plain; charset=UTF-8");
                response.getWriter().write("Trop de tentatives de connexion, réessayez plus tard.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
