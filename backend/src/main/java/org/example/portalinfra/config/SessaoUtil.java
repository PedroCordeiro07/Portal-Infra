package org.example.portalinfra.config;

import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

public final class SessaoUtil {

    private SessaoUtil() {}

    public static Long idLogado(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof Long id)) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Não autenticado.");
        }
        return id;
    }
}