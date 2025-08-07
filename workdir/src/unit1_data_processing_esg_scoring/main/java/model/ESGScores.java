package model;

public class ESGScores {
    private Double environmentalScore;
    private Double socialScore;
    private Double governanceScore;
    private Double compositeScore;
    private Integer holdingCount;
    private Double coveragePercentage;

    public ESGScores() {}

    public ESGScores(Double environmentalScore, Double socialScore, Double governanceScore, Double compositeScore) {
        this.environmentalScore = environmentalScore;
        this.socialScore = socialScore;
        this.governanceScore = governanceScore;
        this.compositeScore = compositeScore;
    }

    // Getters and Setters
    public Double getEnvironmentalScore() { return environmentalScore; }
    public void setEnvironmentalScore(Double environmentalScore) { this.environmentalScore = environmentalScore; }

    public Double getSocialScore() { return socialScore; }
    public void setSocialScore(Double socialScore) { this.socialScore = socialScore; }

    public Double getGovernanceScore() { return governanceScore; }
    public void setGovernanceScore(Double governanceScore) { this.governanceScore = governanceScore; }

    public Double getCompositeScore() { return compositeScore; }
    public void setCompositeScore(Double compositeScore) { this.compositeScore = compositeScore; }

    public Integer getHoldingCount() { return holdingCount; }
    public void setHoldingCount(Integer holdingCount) { this.holdingCount = holdingCount; }

    public Double getCoveragePercentage() { return coveragePercentage; }
    public void setCoveragePercentage(Double coveragePercentage) { this.coveragePercentage = coveragePercentage; }
}