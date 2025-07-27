package com.yourcompany.portfoliogenerator.service;

import com.yourcompany.portfoliogenerator.config.JwtUtil;
import com.yourcompany.portfoliogenerator.model.Role;
import com.yourcompany.portfoliogenerator.model.User;
import com.yourcompany.portfoliogenerator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(Role.USER);
        
        User savedUser = userRepository.save(user);
        
        String token = jwtUtil.generateToken(savedUser);
        
        return AuthResponse.builder()
                .token(token)
                .user(UserResponse.fromUser(savedUser))
                .build();
    }
    
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        String token = jwtUtil.generateToken(user);
        
        return AuthResponse.builder()
                .token(token)
                .user(UserResponse.fromUser(user))
                .build();
    }
}
