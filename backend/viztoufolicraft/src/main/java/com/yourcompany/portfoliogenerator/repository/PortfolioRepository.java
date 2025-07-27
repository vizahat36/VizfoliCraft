package com.yourcompany.portfoliogenerator.repository;

import com.yourcompany.portfoliogenerator.model.Portfolio;
import com.yourcompany.portfoliogenerator.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByUser(User user);
    List<Portfolio> findByUserAndPublished(User user, boolean published);
    List<Portfolio> findByPublished(boolean published);
}
