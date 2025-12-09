package com.legalaid.backend.controller;

import com.legalaid.backend.model.User;
import com.legalaid.backend.repository.UserRepository;
import com.legalaid.backend.service.UserService;
import com.legalaid.backend.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService,
                          UserRepository userRepository,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String email = body.get("email");
        String password = body.get("password");
        String role = body.getOrDefault("role", "ROLE_CITIZEN");

        if (username == null || password == null || email == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "username, email and password are required"));
        }

        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already taken"));
        }
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already in use"));
        }

        User saved = userService.registerUser(username, email, password, role);

        return ResponseEntity.status(201).body(Map.of(
                "id", saved.getId(),
                "username", saved.getUsername(),
                "email", saved.getEmail(),
                "role", saved.getRole()
        ));
    }

    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String password = body.get("password");
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            String token = jwtUtil.generateToken(username);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }
}

