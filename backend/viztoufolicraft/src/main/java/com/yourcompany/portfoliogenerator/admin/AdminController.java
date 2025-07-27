package com.yourcompany.portfoliogenerator.admin;

import com.yourcompany.portfoliogenerator.model.User;
import com.yourcompany.portfoliogenerator.repository.UserRepository;
import com.yourcompany.portfoliogenerator.service.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {
    
    private final UserRepository userRepository;
    
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = users.stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponses);
    }
    
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(UserResponse::fromUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
