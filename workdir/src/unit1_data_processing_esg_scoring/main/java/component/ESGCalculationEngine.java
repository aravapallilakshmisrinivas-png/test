package component;

import config.ApplicationConfig;
import model.ESGHolding;
import model.ESGScores;
import model.Portfolio;
import service.ScoreCalculator;
import service.WeightingManager;
import service.NormalizationService;
import service.CalculationAuditor;

import java.util.List;

public class ESGCalculationEngine {
    private final ApplicationConfig config = new ApplicationConfig();
    private final ScoreCalculator scoreCalculator = new ScoreCalculator();
    private final WeightingManager weightingManager = new WeightingManager(config);
    private final NormalizationService normalizationService = new NormalizationService();
    private final CalculationAuditor auditor = new CalculationAuditor();

    public void calculateCompositeScores(List<ESGHolding> holdings) {
        for (ESGHolding holding : holdings) {
            if (hasValidScores(holding)) {
                double compositeScore = scoreCalculator.calculateComposite(
                    holding.getEnvironmentalScore(),
                    holding.getSocialScore(),
                    holding.getGovernanceScore(),
                    weightingManager.getWeights()
                );
                
                holding.setCompositeScore(compositeScore);
                auditor.logCalculation(holding.getHoldingId(), "COMPOSITE", compositeScore);
            }
        }
    }

    public ESGScores calculatePortfolioScores(Portfolio portfolio) {
        List<ESGHolding> holdings = portfolio.getHoldings();
        if (holdings.isEmpty()) {
            return new ESGScores(0.0, 0.0, 0.0, 0.0);
        }

        double totalValue = holdings.stream()
            .mapToDouble(h -> h.getMarketValue() != null ? h.getMarketValue() : 0.0)
            .sum();

        if (totalValue == 0) {
            // Equal-weighted calculation
            return calculateEqualWeightedScores(holdings);
        } else {
            // Value-weighted calculation
            return calculateValueWeightedScores(holdings, totalValue);
        }
    }

    private ESGScores calculateEqualWeightedScores(List<ESGHolding> holdings) {
        double envSum = 0, socSum = 0, govSum = 0, compSum = 0;
        int validCount = 0;

        for (ESGHolding holding : holdings) {
            if (hasValidScores(holding)) {
                envSum += holding.getEnvironmentalScore();
                socSum += holding.getSocialScore();
                govSum += holding.getGovernanceScore();
                compSum += holding.getCompositeScore() != null ? holding.getCompositeScore() : 0;
                validCount++;
            }
        }

        if (validCount == 0) return new ESGScores(0.0, 0.0, 0.0, 0.0);

        ESGScores scores = new ESGScores(
            envSum / validCount,
            socSum / validCount,
            govSum / validCount,
            compSum / validCount
        );
        scores.setHoldingCount(validCount);
        scores.setCoveragePercentage((double) validCount / holdings.size() * 100);

        return scores;
    }

    private ESGScores calculateValueWeightedScores(List<ESGHolding> holdings, double totalValue) {
        double envWeighted = 0, socWeighted = 0, govWeighted = 0, compWeighted = 0;
        double validValue = 0;

        for (ESGHolding holding : holdings) {
            if (hasValidScores(holding) && holding.getMarketValue() != null) {
                double weight = holding.getMarketValue() / totalValue;
                envWeighted += holding.getEnvironmentalScore() * weight;
                socWeighted += holding.getSocialScore() * weight;
                govWeighted += holding.getGovernanceScore() * weight;
                compWeighted += (holding.getCompositeScore() != null ? holding.getCompositeScore() : 0) * weight;
                validValue += holding.getMarketValue();
            }
        }

        ESGScores scores = new ESGScores(envWeighted, socWeighted, govWeighted, compWeighted);
        scores.setHoldingCount(holdings.size());
        scores.setCoveragePercentage(validValue / totalValue * 100);

        return scores;
    }

    private boolean hasValidScores(ESGHolding holding) {
        return holding.getEnvironmentalScore() != null &&
               holding.getSocialScore() != null &&
               holding.getGovernanceScore() != null;
    }
}