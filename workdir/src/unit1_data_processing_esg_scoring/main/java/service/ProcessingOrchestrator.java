package service;

import component.*;
import model.*;
import repository.ESGScoreRepository;

import java.util.List;

public class ProcessingOrchestrator {
    private final DataValidatorFramework validator = new DataValidatorFramework();
    private final ESGCalculationEngine calculator = new ESGCalculationEngine();
    private final ESGScoreRepository repository = new ESGScoreRepository();

    public ProcessingResult processSampleData() {
        try {
            // 1. Get sample ESG data
            List<ESGHolding> holdings = SampleDataProvider.getValidSampleData();

            // 2. Validate data quality
            DataValidatorFramework.ValidationResult validation = validator.validate(holdings, "sample_data.csv");

            // 3. Calculate ESG scores
            calculator.calculateCompositeScores(holdings);

            // 4. Store results
            for (ESGHolding holding : holdings) {
                repository.save(holding);
            }

            // 5. Create portfolio and calculate portfolio scores
            Portfolio portfolio = new Portfolio("PORTFOLIO_001", "Demo Portfolio", holdings);
            ESGScores portfolioScores = calculator.calculatePortfolioScores(portfolio);
            portfolio.setAggregateScores(portfolioScores);

            return new ProcessingResult(true, "Successfully processed " + holdings.size() + " holdings", 
                                      holdings, validation.getReport());

        } catch (Exception e) {
            return new ProcessingResult(false, "Processing failed: " + e.getMessage(), null, null);
        }
    }
    
    public ProcessingResult processInvalidSampleData() {
        try {
            // 1. Get invalid sample ESG data
            List<ESGHolding> holdings = SampleDataProvider.getInvalidSampleData();

            // 2. Validate data quality (will show validation errors)
            DataValidatorFramework.ValidationResult validation = validator.validate(holdings, "invalid_sample_data.csv");

            // 3. Calculate ESG scores for valid holdings only
            calculator.calculateCompositeScores(holdings);

            // 4. Store results
            for (ESGHolding holding : holdings) {
                repository.save(holding);
            }

            return new ProcessingResult(true, "Processed " + holdings.size() + " holdings with validation issues", 
                                      holdings, validation.getReport());

        } catch (Exception e) {
            return new ProcessingResult(false, "Processing failed: " + e.getMessage(), null, null);
        }
    }

    public List<ESGHolding> getAllHoldings() {
        return repository.findAll();
    }

    public void clearData() {
        repository.clear();
    }

    public static class ProcessingResult {
        private final boolean success;
        private final String message;
        private final List<ESGHolding> holdings;
        private final DataQualityReport report;

        public ProcessingResult(boolean success, String message, List<ESGHolding> holdings, DataQualityReport report) {
            this.success = success;
            this.message = message;
            this.holdings = holdings;
            this.report = report;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public List<ESGHolding> getHoldings() { return holdings; }
        public DataQualityReport getReport() { return report; }
    }
}