package model;

public class ValidationError {
    private String errorId;
    private String recordId;
    private String fieldName;
    private String errorType;
    private String errorMessage;
    private String actualValue;
    private String expectedFormat;
    private String severity;

    public ValidationError() {}

    public ValidationError(String errorId, String recordId, String fieldName, String errorType, String errorMessage) {
        this.errorId = errorId;
        this.recordId = recordId;
        this.fieldName = fieldName;
        this.errorType = errorType;
        this.errorMessage = errorMessage;
        this.severity = "ERROR";
    }

    // Getters and Setters
    public String getErrorId() { return errorId; }
    public void setErrorId(String errorId) { this.errorId = errorId; }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getErrorType() { return errorType; }
    public void setErrorType(String errorType) { this.errorType = errorType; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getActualValue() { return actualValue; }
    public void setActualValue(String actualValue) { this.actualValue = actualValue; }

    public String getExpectedFormat() { return expectedFormat; }
    public void setExpectedFormat(String expectedFormat) { this.expectedFormat = expectedFormat; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
}