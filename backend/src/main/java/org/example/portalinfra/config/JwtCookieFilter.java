
package org.example.portalinfra.config;

import org.example.portalinfra.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtCookieFilter extends OncePerRequestFilter {

    private static final String COOKIE_NAME = "JWT_TOKEN";

    private final JwtService jwtService;

    public JwtCookieFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = null;

        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                token = cookie.getValue();
                break;
            }
        }

        if (token != null
                && SecurityContextHolder.getContext()
                        .getAuthentication() == null
                && jwtService.validarToken(token)) {

            try {
                Long usuarioId =
                        jwtService.extrairUsuarioId(token);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                usuarioId,
                                null,
                                List.of()
                        );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

            } catch (RuntimeException e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
