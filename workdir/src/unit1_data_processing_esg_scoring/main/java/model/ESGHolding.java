package model;

import java.time.LocalDateTime;
import java.util.Map;

public class ESGHolding {
    private String holdingId;
    private String symbol;
    private String companyName;
    private String sector;
    private Double environmentalScore;
    private Double socialScore;
    private Double governanceScore;
    private Double compositeScore;
    private Double marketValue;
    private LocalDateTime lastUpdated;
    private String dataSource;
    private Map<String, Object> metadata;

    public ESGHolding() {}

    public ESGHolding(String holdingId, String symbol, String companyName, String sector,
                      Double environmentalScore, Double socialScore, Double governanceScore,
                      Double marketValue) {
        this.holdingId = holdingId;
        this.symbol = symbol;
        this.companyName = companyName;
        this.sector = sector;
        this.environmentalScore = environmentalScore;
        this.socialScore = socialScore;
        this.governanceScore = governanceScore;
        this.marketValue = marketValue;
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters and Setters
    public String getHoldingId() { return holdingId; }
    public void setHoldingId(String holdingId) { this.holdingId = holdingId; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public Double getEnvironmentalScore() { return environmentalScore; }
    public void setEnvironmentalScore(Double environmentalScore) { this.environmentalScore = environmentalScore; }

    public Double getSocialScore() { return socialScore; }
    public void setSocialScore(Double socialScore) { this.socialScore = socialScore; }

    public Double getGovernanceScore() { return governanceScore; }
    public void setGovernanceScore(Double governanceScore) { this.governanceScore = governanceScore; }

    public Double getCompositeScore() { return compositeScore; }
    public void setCompositeScore(Double compositeScore) { this.compositeScore = compositeScore; }

    public Double getMarketValue() { return marketValue; }
    public void setMarketValue(Double marketValue) { this.marketValue = marketValue; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}