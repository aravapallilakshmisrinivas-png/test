package service;

import model.ValidationError;

import java.util.List;

public class DataQualityAnalyzer {
    
    public double calculateQualityScore(int totalRecords, List<ValidationError> errors) {
        if (totalRecords == 0) return 0.0;
        
        int errorCount = 0;
        int warningCount = 0;
        int infoCount = 0;
        
        for (ValidationError error : errors) {
            switch (error.getSeverity()) {
                case "ERROR":
                    errorCount++;
                    break;
                case "WARNING":
                    warningCount++;
                    break;
                case "INFO":
                    infoCount++;
                    break;
            }
        }
        
        // Quality Score = (Valid Records / Total Records) × 100
        // Weighted by severity: ERROR (-10), WARNING (-5), INFO (-1)
        double penalty = (errorCount * 10) + (warningCount * 5) + (infoCount * 1);
        double baseScore = ((double) (totalRecords - errors.size()) / totalRecords) * 100;
        
        return Math.max(0.0, baseScore - (penalty / totalRecords * 100));
    }

    public QualityMetrics analyzeQuality(int totalRecords, List<ValidationError> errors) {
        int validRecords = totalRecords - errors.size();
        double qualityScore = calculateQualityScore(totalRecords, errors);
        double coveragePercentage = totalRecords > 0 ? ((double) validRecords / totalRecords) * 100 : 0.0;
        
        return new QualityMetrics(totalRecords, validRecords, errors.size(), qualityScore, coveragePercentage);
    }

    public static class QualityMetrics {
        private final int totalRecords;
        private final int validRecords;
        private final int errorRecords;
        private final double qualityScore;
        private final double coveragePercentage;

        public QualityMetrics(int totalRecords, int validRecords, int errorRecords, double qualityScore, double coveragePercentage) {
            this.totalRecords = totalRecords;
            this.validRecords = validRecords;
            this.errorRecords = errorRecords;
            this.qualityScore = qualityScore;
            this.coveragePercentage = coveragePercentage;
        }

        public int getTotalRecords() { return totalRecords; }
        public int getValidRecords() { return validRecords; }
        public int getErrorRecords() { return errorRecords; }
        public double getQualityScore() { return qualityScore; }
        public double getCoveragePercentage() { return coveragePercentage; }
    }
}