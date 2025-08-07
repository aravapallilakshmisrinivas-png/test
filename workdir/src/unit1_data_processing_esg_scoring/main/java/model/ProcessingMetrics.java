package model;

import java.util.Map;

public class ProcessingMetrics {
    private Long processingTimeMs;
    private Long recordsPerSecond;
    private Long memoryUsedMB;
    private Integer threadsUsed;
    private Map<String, Long> phaseTimings;

    public ProcessingMetrics() {}

    public ProcessingMetrics(Long processingTimeMs, Long recordsPerSecond, Long memoryUsedMB, Integer threadsUsed) {
        this.processingTimeMs = processingTimeMs;
        this.recordsPerSecond = recordsPerSecond;
        this.memoryUsedMB = memoryUsedMB;
        this.threadsUsed = threadsUsed;
    }

    // Getters and Setters
    public Long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(Long processingTimeMs) { this.processingTimeMs = processingTimeMs; }

    public Long getRecordsPerSecond() { return recordsPerSecond; }
    public void setRecordsPerSecond(Long recordsPerSecond) { this.recordsPerSecond = recordsPerSecond; }

    public Long getMemoryUsedMB() { return memoryUsedMB; }
    public void setMemoryUsedMB(Long memoryUsedMB) { this.memoryUsedMB = memoryUsedMB; }

    public Integer getThreadsUsed() { return threadsUsed; }
    public void setThreadsUsed(Integer threadsUsed) { this.threadsUsed = threadsUsed; }

    public Map<String, Long> getPhaseTimings() { return phaseTimings; }
    public void setPhaseTimings(Map<String, Long> phaseTimings) { this.phaseTimings = phaseTimings; }
}