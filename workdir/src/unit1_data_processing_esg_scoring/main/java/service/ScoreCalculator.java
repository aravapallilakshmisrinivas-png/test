package service;

public class ScoreCalculator {
    
    public double calculateComposite(Double environmentalScore, Double socialScore, Double governanceScore, WeightingManager.ESGWeights weights) {
        if (environmentalScore == null || socialScore == null || governanceScore == null) {
            return 0.0;
        }
        
        return (environmentalScore * weights.getEnvironmentalWeight()) +
               (socialScore * weights.getSocialWeight()) +
               (governanceScore * weights.getGovernanceWeight());
    }

    public double calculateNormalizedScore(double rawScore, double minScore, double maxScore) {
        if (maxScore == minScore) return 50.0; // Default to middle if no range
        return ((rawScore - minScore) / (maxScore - minScore)) * 100.0;
    }

    public double calculatePercentileScore(double score, double[] allScores) {
        if (allScores.length == 0) return 0.0;
        
        int count = 0;
        for (double s : allScores) {
            if (s <= score) count++;
        }
        
        return ((double) count / allScores.length) * 100.0;
    }

    public StatisticalSummary calculateStatistics(double[] scores) {
        if (scores.length == 0) {
            return new StatisticalSummary(0, 0, 0, 0, 0, 0);
        }
        
        double sum = 0;
        double min = scores[0];
        double max = scores[0];
        
        for (double score : scores) {
            sum += score;
            min = Math.min(min, score);
            max = Math.max(max, score);
        }
        
        double mean = sum / scores.length;
        
        // Calculate standard deviation
        double sumSquaredDiffs = 0;
        for (double score : scores) {
            sumSquaredDiffs += Math.pow(score - mean, 2);
        }
        double stdDev = Math.sqrt(sumSquaredDiffs / scores.length);
        
        return new StatisticalSummary(mean, min, max, stdDev, sum, scores.length);
    }

    public static class StatisticalSummary {
        private final double mean;
        private final double min;
        private final double max;
        private final double standardDeviation;
        private final double sum;
        private final int count;

        public StatisticalSummary(double mean, double min, double max, double standardDeviation, double sum, int count) {
            this.mean = mean;
            this.min = min;
            this.max = max;
            this.standardDeviation = standardDeviation;
            this.sum = sum;
            this.count = count;
        }

        public double getMean() { return mean; }
        public double getMin() { return min; }
        public double getMax() { return max; }
        public double getStandardDeviation() { return standardDeviation; }
        public double getSum() { return sum; }
        public int getCount() { return count; }
    }
}