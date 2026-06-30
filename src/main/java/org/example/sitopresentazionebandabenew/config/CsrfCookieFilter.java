package org.example.sitopresentazionebandabenew.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Forza il caricamento del token CSRF a ogni richiesta, così il repository basato su cookie
 * emette sempre il cookie {@code XSRF-TOKEN} che la SPA Angular potrà leggere e rispedire
 * nell'header {@code X-XSRF-TOKEN}.
 *
 * <p>Necessario perché in Spring Security il {@link CsrfToken} è "deferred": senza accedere
 * al valore, il cookie non verrebbe scritto.
 */
final class CsrfCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            // Accedere al valore forza la scrittura del cookie XSRF-TOKEN.
            csrfToken.getToken();
        }
        filterChain.doFilter(request, response);
    }
}
