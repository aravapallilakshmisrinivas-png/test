package service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CalculationAuditor {
    private final ConcurrentLinkedQueue<AuditEntry> auditLog = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<String, CalculationHistory> calculationHistory = new ConcurrentHashMap<>();

    public void logCalculation(String holdingId, String calculationType, double result) {
        AuditEntry entry = new AuditEntry(
            holdingId,
            calculationType,
            result,
            LocalDateTime.now()
        );
        
        auditLog.offer(entry);
        
        // Maintain calculation history
        calculationHistory.computeIfAbsent(holdingId, k -> new CalculationHistory())
                         .addCalculation(calculationType, result);
        
        // Keep audit log size manageable (last 10000 entries)
        while (auditLog.size() > 10000) {
            auditLog.poll();
        }
    }

    public void logPortfolioCalculation(String portfolioId, String calculationType, double result, int holdingCount) {
        AuditEntry entry = new AuditEntry(
            portfolioId,
            "PORTFOLIO_" + calculationType,
            result,
            LocalDateTime.now()
        );
        entry.setMetadata("holdingCount", holdingCount);
        
        auditLog.offer(entry);
    }

    public CalculationHistory getCalculationHistory(String holdingId) {
        return calculationHistory.get(holdingId);
    }

    public int getAuditLogSize() {
        return auditLog.size();
    }

    public static class AuditEntry {
        private final String entityId;
        private final String calculationType;
        private final double result;
        private final LocalDateTime timestamp;
        private ConcurrentHashMap<String, Object> metadata = new ConcurrentHashMap<>();

        public AuditEntry(String entityId, String calculationType, double result, LocalDateTime timestamp) {
            this.entityId = entityId;
            this.calculationType = calculationType;
            this.result = result;
            this.timestamp = timestamp;
        }

        public String getEntityId() { return entityId; }
        public String getCalculationType() { return calculationType; }
        public double getResult() { return result; }
        public LocalDateTime getTimestamp() { return timestamp; }
        
        public void setMetadata(String key, Object value) {
            metadata.put(key, value);
        }
        
        public Object getMetadata(String key) {
            return metadata.get(key);
        }
    }

    public static class CalculationHistory {
        private final ConcurrentHashMap<String, Double> latestCalculations = new ConcurrentHashMap<>();
        private final ConcurrentLinkedQueue<AuditEntry> history = new ConcurrentLinkedQueue<>();

        public void addCalculation(String type, double result) {
            latestCalculations.put(type, result);
            history.offer(new AuditEntry("", type, result, LocalDateTime.now()));
            
            // Keep history manageable
            while (history.size() > 100) {
                history.poll();
            }
        }

        public Double getLatestCalculation(String type) {
            return latestCalculations.get(type);
        }

        public int getHistorySize() {
            return history.size();
        }
    }
}