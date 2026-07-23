package com.example.SpringDataJpaDemo.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Runs once per HTTP request (OncePerRequestFilter).
 * Reads Authorization: Bearer <jwt>, validates it, loads the user, and sets SecurityContext.
 * Controllers like MeController then read that user via @AuthenticationPrincipal.
 *
 * @Component — Spring bean so SecurityConfig can inject it
 * @RequiredArgsConstructor — injects JwtService + UserDetailsService
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Core filter logic.
     * If there is no Bearer header → continue (public endpoints still work).
     * If token is invalid/expired → 401 and stop the chain.
     * If valid → set Authentication, then continue to controllers.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        // Must be exactly "Bearer " + token (space matters)
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(7); // strip "Bearer "
            Claims claims = jwtService.parseToken(token);
            String email = claims.getSubject(); // we put email in JWT subject at login

            // Only set auth if nothing is already authenticated on this request
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // This is what @AuthenticationPrincipal reads later
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception exception) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getOutputStream().write(
                    "{\"code\":\"UNAUTHORIZED\",\"message\":\"Invalid or expired token\"}"
                            .getBytes(StandardCharsets.UTF_8));
            return; // do not continue to the controller
        }

        filterChain.doFilter(request, response);
    }
}
