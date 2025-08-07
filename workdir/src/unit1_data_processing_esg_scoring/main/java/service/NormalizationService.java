package service;

import model.ESGHolding;

import java.util.List;

public class NormalizationService {
    
    public void normalizeScores(List<ESGHolding> holdings) {
        if (holdings.isEmpty()) return;
        
        // Find min/max for each score type
        ScoreRange envRange = findScoreRange(holdings, "environmental");
        ScoreRange socRange = findScoreRange(holdings, "social");
        ScoreRange govRange = findScoreRange(holdings, "governance");
        
        // Normalize each holding's scores
        for (ESGHolding holding : holdings) {
            if (holding.getEnvironmentalScore() != null) {
                double normalized = normalizeScore(holding.getEnvironmentalScore(), envRange);
                holding.setEnvironmentalScore(normalized);
            }
            if (holding.getSocialScore() != null) {
                double normalized = normalizeScore(holding.getSocialScore(), socRange);
                holding.setSocialScore(normalized);
            }
            if (holding.getGovernanceScore() != null) {
                double normalized = normalizeScore(holding.getGovernanceScore(), govRange);
                holding.setGovernanceScore(normalized);
            }
        }
    }

    private ScoreRange findScoreRange(List<ESGHolding> holdings, String scoreType) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        
        for (ESGHolding holding : holdings) {
            Double score = getScore(holding, scoreType);
            if (score != null) {
                min = Math.min(min, score);
                max = Math.max(max, score);
            }
        }
        
        return new ScoreRange(min, max);
    }

    private Double getScore(ESGHolding holding, String scoreType) {
        switch (scoreType.toLowerCase()) {
            case "environmental":
                return holding.getEnvironmentalScore();
            case "social":
                return holding.getSocialScore();
            case "governance":
                return holding.getGovernanceScore();
            default:
                return null;
        }
    }

    private double normalizeScore(double score, ScoreRange range) {
        if (range.max == range.min) return 50.0; // Default to middle if no range
        return ((score - range.min) / (range.max - range.min)) * 100.0;
    }

    public double normalizeToRange(double value, double minValue, double maxValue, double targetMin, double targetMax) {
        if (maxValue == minValue) return (targetMin + targetMax) / 2.0;
        
        double normalizedRatio = (value - minValue) / (maxValue - minValue);
        return targetMin + (normalizedRatio * (targetMax - targetMin));
    }

    private static class ScoreRange {
        final double min;
        final double max;

        ScoreRange(double min, double max) {
            this.min = min;
            this.max = max;
        }
    }
}