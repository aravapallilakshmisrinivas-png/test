package model;

import java.time.LocalDateTime;
import java.util.List;

public class Portfolio {
    private String portfolioId;
    private String name;
    private String description;
    private List<ESGHolding> holdings;
    private ESGScores aggregateScores;
    private Double totalValue;
    private LocalDateTime lastUpdated;
    private String status;

    public Portfolio() {}

    public Portfolio(String portfolioId, String name, List<ESGHolding> holdings) {
        this.portfolioId = portfolioId;
        this.name = name;
        this.holdings = holdings;
        this.lastUpdated = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    // Getters and Setters
    public String getPortfolioId() { return portfolioId; }
    public void setPortfolioId(String portfolioId) { this.portfolioId = portfolioId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<ESGHolding> getHoldings() { return holdings; }
    public void setHoldings(List<ESGHolding> holdings) { this.holdings = holdings; }

    public ESGScores getAggregateScores() { return aggregateScores; }
    public void setAggregateScores(ESGScores aggregateScores) { this.aggregateScores = aggregateScores; }

    public Double getTotalValue() { return totalValue; }
    public void setTotalValue(Double totalValue) { this.totalValue = totalValue; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public void addHolding(ESGHolding holding) {
        if (holdings != null) {
            holdings.add(holding);
            this.lastUpdated = LocalDateTime.now();
        }
    }
}