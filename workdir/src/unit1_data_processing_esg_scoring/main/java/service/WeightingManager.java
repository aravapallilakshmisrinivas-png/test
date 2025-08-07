package service;

import config.ApplicationConfig;

public class WeightingManager {
    private final ApplicationConfig config;
    private ESGWeights weights;

    public WeightingManager(ApplicationConfig config) {
        this.config = config;
        this.weights = loadWeights();
    }

    private ESGWeights loadWeights() {
        return new ESGWeights(
            config.getEnvironmentalWeight(),
            config.getSocialWeight(),
            config.getGovernanceWeight()
        );
    }

    public ESGWeights getWeights() {
        return weights;
    }

    public void updateWeights(double environmental, double social, double governance) {
        // Validate weights sum to 1.0
        double sum = environmental + social + governance;
        if (Math.abs(sum - 1.0) > 0.001) {
            throw new IllegalArgumentException("Weights must sum to 1.0, got: " + sum);
        }
        
        this.weights = new ESGWeights(environmental, social, governance);
    }

    public void reloadWeights() {
        this.weights = loadWeights();
    }

    public static class ESGWeights {
        private final double environmentalWeight;
        private final double socialWeight;
        private final double governanceWeight;

        public ESGWeights(double environmentalWeight, double socialWeight, double governanceWeight) {
            this.environmentalWeight = environmentalWeight;
            this.socialWeight = socialWeight;
            this.governanceWeight = governanceWeight;
        }

        public double getEnvironmentalWeight() { return environmentalWeight; }
        public double getSocialWeight() { return socialWeight; }
        public double getGovernanceWeight() { return governanceWeight; }

        @Override
        public String toString() {
            return String.format("ESGWeights{E=%.2f, S=%.2f, G=%.2f}", 
                environmentalWeight, socialWeight, governanceWeight);
        }
    }
}