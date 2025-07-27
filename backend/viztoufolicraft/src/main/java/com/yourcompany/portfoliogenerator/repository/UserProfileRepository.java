package com.yourcompany.portfoliogenerator.repository;

import com.yourcompany.portfoliogenerator.model.User;
import com.yourcompany.portfoliogenerator.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUser(User user);
    Optional<UserProfile> findByUserId(Long userId);
    boolean existsByUser(User user);
}
