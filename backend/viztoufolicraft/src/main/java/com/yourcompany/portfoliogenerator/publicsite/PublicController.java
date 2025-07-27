package com.yourcompany.portfoliogenerator.publicsite;

import com.yourcompany.portfoliogenerator.model.Portfolio;
import com.yourcompany.portfoliogenerator.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PublicController {
    
    private final PortfolioRepository portfolioRepository;
    
    @GetMapping("/portfolios")
    public ResponseEntity<List<Portfolio>> getPublishedPortfolios() {
        List<Portfolio> portfolios = portfolioRepository.findByPublished(true);
        return ResponseEntity.ok(portfolios);
    }
    
    @GetMapping("/portfolios/{id}")
    public ResponseEntity<Portfolio> getPortfolioById(@PathVariable Long id) {
        return portfolioRepository.findById(id)
                .filter(Portfolio::isPublished)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
