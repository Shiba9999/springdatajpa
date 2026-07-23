package com.example.SpringDataJpaDemo.security;

import com.example.SpringDataJpaDemo.entities.User;
import com.example.SpringDataJpaDemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Bridge between our DB User entity and Spring Security's UserDetails.
 * Used by:
 *  - AuthenticationManager during login (load by email, check password)
 *  - JwtAuthFilter after parsing JWT (reload authorities/roles)
 *
 * @Service — bean implementing UserDetailsService
 * @RequiredArgsConstructor — injects UserRepository
 *
 * Note: "username" in Spring Security = our email field.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Spring Security calls this with the "username" (we pass email).
     * .roles("USER") becomes authority ROLE_USER — matches hasRole("USER") in SecurityConfig.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email " + email));

        // Fully qualified name avoids clash with our entity User
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
