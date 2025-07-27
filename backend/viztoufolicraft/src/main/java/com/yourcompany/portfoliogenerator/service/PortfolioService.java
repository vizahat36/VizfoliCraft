package com.yourcompany.portfoliogenerator.service;

import com.yourcompany.portfoliogenerator.model.Portfolio;
import com.yourcompany.portfoliogenerator.model.User;
import com.yourcompany.portfoliogenerator.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    
    private final PortfolioRepository portfolioRepository;
    
    public List<Portfolio> getUserPortfolios(User user) {
        return portfolioRepository.findByUser(user);
    }
    
    public Portfolio createPortfolio(Portfolio portfolio, User user) {
        portfolio.setUser(user);
        return portfolioRepository.save(portfolio);
    }
    
    public Optional<Portfolio> updatePortfolio(Long id, Portfolio updatedPortfolio, User user) {
        return portfolioRepository.findById(id)
                .filter(portfolio -> portfolio.getUser().getId().equals(user.getId()))
                .map(portfolio -> {
                    portfolio.setTitle(updatedPortfolio.getTitle());
                    portfolio.setDescription(updatedPortfolio.getDescription());
                    portfolio.setContent(updatedPortfolio.getContent());
                    portfolio.setPublished(updatedPortfolio.isPublished());
                    return portfolioRepository.save(portfolio);
                });
    }
    
    public boolean deletePortfolio(Long id, User user) {
        return portfolioRepository.findById(id)
                .filter(portfolio -> portfolio.getUser().getId().equals(user.getId()))
                .map(portfolio -> {
                    portfolioRepository.delete(portfolio);
                    return true;
                })
                .orElse(false);
    }
}
