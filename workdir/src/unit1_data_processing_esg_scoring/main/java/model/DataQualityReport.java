package model;

import java.time.LocalDateTime;
import java.util.List;

public class DataQualityReport {
    private String reportId;
    private String fileName;
    private String portfolioId;
    private Integer totalRecords;
    private Integer validRecords;
    private Integer errorRecords;
    private List<ValidationError> errors;
    private Double qualityScore;
    private LocalDateTime generatedAt;
    private ProcessingMetrics metrics;

    public DataQualityReport() {
        this.generatedAt = LocalDateTime.now();
    }

    public DataQualityReport(String reportId, String fileName, String portfolioId) {
        this.reportId = reportId;
        this.fileName = fileName;
        this.portfolioId = portfolioId;
        this.generatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getPortfolioId() { return portfolioId; }
    public void setPortfolioId(String portfolioId) { this.portfolioId = portfolioId; }

    public Integer getTotalRecords() { return totalRecords; }
    public void setTotalRecords(Integer totalRecords) { this.totalRecords = totalRecords; }

    public Integer getValidRecords() { return validRecords; }
    public void setValidRecords(Integer validRecords) { this.validRecords = validRecords; }

    public Integer getErrorRecords() { return errorRecords; }
    public void setErrorRecords(Integer errorRecords) { this.errorRecords = errorRecords; }

    public List<ValidationError> getErrors() { return errors; }
    public void setErrors(List<ValidationError> errors) { this.errors = errors; }

    public Double getQualityScore() { return qualityScore; }
    public void setQualityScore(Double qualityScore) { this.qualityScore = qualityScore; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public ProcessingMetrics getMetrics() { return metrics; }
    public void setMetrics(ProcessingMetrics metrics) { this.metrics = metrics; }
}