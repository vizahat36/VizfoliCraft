package com.yourcompany.portfoliogenerator.publicsite;

import com.yourcompany.portfoliogenerator.model.Portfolio;
import com.yourcompany.portfoliogenerator.model.User;
import com.yourcompany.portfoliogenerator.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PortfolioController {
    
    private final PortfolioService portfolioService;
    
    @GetMapping
    public ResponseEntity<List<Portfolio>> getUserPortfolios(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Portfolio> portfolios = portfolioService.getUserPortfolios(user);
        return ResponseEntity.ok(portfolios);
    }
    
    @PostMapping
    public ResponseEntity<Portfolio> createPortfolio(
            @RequestBody Portfolio portfolio,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        Portfolio createdPortfolio = portfolioService.createPortfolio(portfolio, user);
        return ResponseEntity.ok(createdPortfolio);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Portfolio> updatePortfolio(
            @PathVariable Long id,
            @RequestBody Portfolio portfolio,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return portfolioService.updatePortfolio(id, portfolio, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolio(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        boolean deleted = portfolioService.deletePortfolio(id, user);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
