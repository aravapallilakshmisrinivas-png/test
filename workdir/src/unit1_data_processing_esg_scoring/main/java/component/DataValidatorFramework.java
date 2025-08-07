package component;

import model.ESGHolding;
import model.DataQualityReport;
import model.ValidationError;
import service.ValidationRuleEngine;
import service.DataQualityAnalyzer;
import service.ValidationReportBuilder;

import java.util.List;
import java.util.UUID;

public class DataValidatorFramework {
    private final ValidationRuleEngine ruleEngine = new ValidationRuleEngine();
    private final DataQualityAnalyzer qualityAnalyzer = new DataQualityAnalyzer();
    private final ValidationReportBuilder reportBuilder = new ValidationReportBuilder();

    public ValidationResult validate(List<ESGHolding> holdings, String fileName) {
        List<ValidationError> errors = ruleEngine.validateHoldings(holdings);
        double qualityScore = qualityAnalyzer.calculateQualityScore(holdings.size(), errors);
        
        DataQualityReport report = reportBuilder.buildReport(
            UUID.randomUUID().toString(),
            fileName,
            "PORTFOLIO_001", // Default portfolio ID
            holdings.size(),
            errors,
            qualityScore
        );
        
        return new ValidationResult(errors.isEmpty(), errors, report);
    }

    public static class ValidationResult {
        private final boolean isValid;
        private final List<ValidationError> errors;
        private final DataQualityReport report;

        public ValidationResult(boolean isValid, List<ValidationError> errors, DataQualityReport report) {
            this.isValid = isValid;
            this.errors = errors;
            this.report = report;
        }

        public boolean isValid() { return isValid; }
        public List<ValidationError> getErrors() { return errors; }
        public DataQualityReport getReport() { return report; }
    }
}