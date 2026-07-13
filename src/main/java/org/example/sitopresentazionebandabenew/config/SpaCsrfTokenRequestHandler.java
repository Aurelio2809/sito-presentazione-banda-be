package org.example.sitopresentazionebandabenew.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Supplier;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

/**
 * Gestore del token CSRF pensato per Single Page Application (Angular).
 *
 * <p>Ricetta ufficiale Spring Security: il token viene reso (per il cookie {@code XSRF-TOKEN})
 * tramite {@link XorCsrfTokenRequestAttributeHandler} per mitigare BREACH, ma in fase di
 * validazione viene risolto come valore "in chiaro" quando arriva dall'header
 * {@code X-XSRF-TOKEN} (come fa Angular leggendo il cookie). Per i form classici (parametro)
 * resta la decodifica XOR.
 */
final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

    private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
    private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
        // Rende il token con protezione XOR/BREACH e lo espone nel cookie.
        this.xor.handle(request, response, csrfToken);
    }

    @Override
    public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
        String headerValue = request.getHeader(csrfToken.getHeaderName());
        // Se il token arriva dall'header (Angular), lo trattiamo come valore in chiaro;
        // altrimenti (form/parametro) usiamo la decodifica XOR.
        return StringUtils.hasText(headerValue)
                ? this.plain.resolveCsrfTokenValue(request, csrfToken)
                : this.xor.resolveCsrfTokenValue(request, csrfToken);
    }
}
