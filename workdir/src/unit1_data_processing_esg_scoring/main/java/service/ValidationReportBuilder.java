package service;

import model.DataQualityReport;
import model.ValidationError;
import model.ProcessingMetrics;

import java.util.List;

public class ValidationReportBuilder {
    
    public DataQualityReport buildReport(String reportId, String fileName, String portfolioId, 
                                       int totalRecords, List<ValidationError> errors, double qualityScore) {
        
        DataQualityReport report = new DataQualityReport(reportId, fileName, portfolioId);
        report.setTotalRecords(totalRecords);
        report.setValidRecords(totalRecords - errors.size());
        report.setErrorRecords(errors.size());
        report.setErrors(errors);
        report.setQualityScore(qualityScore);
        
        // Create basic processing metrics
        ProcessingMetrics metrics = new ProcessingMetrics(
            System.currentTimeMillis(),
            (long) totalRecords,
            Runtime.getRuntime().totalMemory() / (1024 * 1024),
            1
        );
        report.setMetrics(metrics);
        
        return report;
    }

    public String generateReportSummary(DataQualityReport report) {
        StringBuilder summary = new StringBuilder();
        summary.append("Data Quality Report Summary\n");
        summary.append("==========================\n");
        summary.append("File: ").append(report.getFileName()).append("\n");
        summary.append("Total Records: ").append(report.getTotalRecords()).append("\n");
        summary.append("Valid Records: ").append(report.getValidRecords()).append("\n");
        summary.append("Error Records: ").append(report.getErrorRecords()).append("\n");
        summary.append("Quality Score: ").append(String.format("%.2f%%", report.getQualityScore())).append("\n");
        
        if (!report.getErrors().isEmpty()) {
            summary.append("\nTop Errors:\n");
            int errorCount = Math.min(5, report.getErrors().size());
            for (int i = 0; i < errorCount; i++) {
                ValidationError error = report.getErrors().get(i);
                summary.append("- ").append(error.getErrorMessage()).append("\n");
            }
        }
        
        return summary.toString();
    }
}