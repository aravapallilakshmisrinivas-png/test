package service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AuditLogger {
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private final ConcurrentLinkedQueue<String> auditLog = new ConcurrentLinkedQueue<>();
    
    public void logProcessingStart(String fileName, int recordCount) {
        String entry = createLogEntry("PROCESSING_START", "File: " + fileName + ", Records: " + recordCount);
        auditLog.offer(entry);
        System.out.println(entry);
    }
    
    public void logProcessingComplete(String fileName, int processedCount, int validCount, int invalidCount) {
        String entry = createLogEntry("PROCESSING_COMPLETE", 
            "File: " + fileName + ", Processed: " + processedCount + ", Valid: " + validCount + ", Invalid: " + invalidCount);
        auditLog.offer(entry);
        System.out.println(entry);
    }
    
    public void logESGCalculation(String holdingId, double esgScore) {
        String entry = createLogEntry("ESG_CALCULATION", "Holding: " + holdingId + ", Score: " + esgScore);
        auditLog.offer(entry);
    }
    
    public void logValidationError(String holdingId, String error) {
        String entry = createLogEntry("VALIDATION_ERROR", "Holding: " + holdingId + ", Error: " + error);
        auditLog.offer(entry);
        System.out.println(entry);
    }
    
    private String createLogEntry(String eventType, String details) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        return "[" + timestamp + "] AUDIT [" + eventType + "] " + details;
    }
    
    public void clearLog() {
        auditLog.clear();
    }
}