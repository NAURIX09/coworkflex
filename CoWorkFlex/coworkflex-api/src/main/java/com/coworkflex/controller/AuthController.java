package com.coworkflex.controller;

import com.coworkflex.dto.Dtos.*;
import com.coworkflex.entity.User;
import com.coworkflex.exception.ConflictException;
import com.coworkflex.repository.UserRepository;
import com.coworkflex.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Register & Login")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    @Operation(summary = "Créer un compte")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new ConflictException("Email déjà utilisé.");
        }
        User user = User.builder()
            .fullName(req.fullName()).email(req.email())
            .password(passwordEncoder.encode(req.password()))
            .role(User.Role.USER).build();

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new AuthResponse(token, saved.getId(),
                saved.getFullName(), saved.getEmail(), saved.getRole().name()));
    }

    @PostMapping("/login")
    @Operation(summary = "Se connecter")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password()));
            User user = (User) auth.getPrincipal();
            String token = jwtService.generateToken(user);
            return ResponseEntity.ok(new AuthResponse(
                token, user.getId(), user.getFullName(), user.getEmail(), user.getRole().name()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Email ou mot de passe incorrect."));
        }
    }
}
