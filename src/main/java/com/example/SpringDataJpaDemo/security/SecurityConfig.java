package com.example.SpringDataJpaDemo.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.nio.charset.StandardCharsets;

/**
 * Central Spring Security setup for this API.
 *
 * @Configuration — class that defines @Bean methods for the Spring container
 * @AllArgsConstructor — injects JwtAuthFilter
 *
 * Flow for a protected request:
 * 1) JwtAuthFilter reads Authorization: Bearer <token>
 * 2) If valid, sets Authentication in SecurityContext
 * 3) authorizeHttpRequests checks roles / authenticated
 */
@Configuration
@AllArgsConstructor
public class SecurityConfig {

    /** Our custom filter that turns JWT into a SecurityContext Authentication. */
    private final JwtAuthFilter jwtAuthFilter;

    /**
     * @Bean SecurityFilterChain — the main HTTP security rules (who can call what).
     * csrf disabled — typical for stateless JWT APIs (no browser session cookies).
     * STATELESS — no HTTP session; every request must carry its own JWT.
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        // Public: register / login (no JWT required)
                        auth.requestMatchers("/api/v1/auth/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                                // Logged-in user: profile + own orders via JWT (no userId in URL)
                                .requestMatchers("/api/v1/me", "/api/v1/me/**").hasAnyRole("ADMIN", "USER")
                                // Path still has userId — USER or ADMIN
                                .requestMatchers("/api/v1/users/*/orders/**").hasAnyRole("ADMIN", "USER")
                                // Admin-only user management
                                .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                                // Everything else needs a valid JWT
                                .anyRequest().authenticated())
                // Run JWT filter before Spring's username/password filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED,
                                        "UNAUTHORIZED", "Authentication required"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeJsonError(response, HttpServletResponse.SC_FORBIDDEN,
                                        "FORBIDDEN", "Access denied")));

        return httpSecurity.build();
    }

    /** Small helper to write JSON error bodies from security handlers. */
    private static void writeJsonError(HttpServletResponse response, int status, String code, String message)
            throws java.io.IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String body = "{\"code\":\"" + code + "\",\"message\":\"" + message + "\"}";
        response.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * AuthenticationManager used by AuthService.login().
     * DaoAuthenticationProvider loads user via UserDetailsService and checks password with PasswordEncoder.
     */
    @Bean
    AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider =
                new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authenticationProvider);
    }

    /** BCrypt hasher/matcher bean used for register + login. */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * JwtAuthFilter is a @Component Filter — Boot would also register it as a servlet Filter.
     * Disable that so it only runs inside the Security filter chain (once).
     */
    @Bean
    FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration(JwtAuthFilter filter) {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
