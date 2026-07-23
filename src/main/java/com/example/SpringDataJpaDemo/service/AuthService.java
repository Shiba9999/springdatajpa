package com.example.SpringDataJpaDemo.service;

import com.example.SpringDataJpaDemo.dto.CreateUserDto;
import com.example.SpringDataJpaDemo.dto.LoginDto;
import com.example.SpringDataJpaDemo.dto.LoginResponseDto;
import com.example.SpringDataJpaDemo.dto.RegisterUserResponseDto;
import com.example.SpringDataJpaDemo.entities.Role;
import com.example.SpringDataJpaDemo.entities.User;
import com.example.SpringDataJpaDemo.exception.InvalidCredentialsException;
import com.example.SpringDataJpaDemo.repository.UserRepository;
import com.example.SpringDataJpaDemo.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication business logic: register (save hashed password) and login (issue JWT).
 *
 * @Service — Spring registers this as a bean in the service layer
 * @AllArgsConstructor — constructor-injects all final dependencies
 */
@Service
@AllArgsConstructor
public class AuthService {

    /** Hashes passwords with BCrypt before saving; never store plain text. */
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    /** Spring Security component that checks email/password via UserDetailsService. */
    private final AuthenticationManager authenticationManager;
    /** Builds and parses JWT strings. */
    private final JwtService jwtService;

    /**
     * Creates a new USER with a BCrypt-hashed password and returns name + id.
     */
    public RegisterUserResponseDto registerUser(CreateUserDto createUserDto) {
        User user = new User();
        user.setName(createUserDto.getName());
        user.setEmail(createUserDto.getEmail());
        // encode() produces a $2a$... hash — login will use matches() against this
        user.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);
        return new RegisterUserResponseDto(savedUser.getName(), savedUser.getId());
    }

    /**
     * Validates credentials, then returns a JWT.
     * UsernamePasswordAuthenticationToken — Spring's "email + password" auth request object.
     * On failure we throw InvalidCredentialsException (not AuthenticationException),
     * so GlobalExceptionHandler can return a clear JSON body.
     */
    public LoginResponseDto login(LoginDto loginDto) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Principal is the UserDetails loaded by CustomUserDetailsService
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwtToken = jwtService.generateJwtToken(userDetails);
        return new LoginResponseDto(jwtToken);
    }
}
