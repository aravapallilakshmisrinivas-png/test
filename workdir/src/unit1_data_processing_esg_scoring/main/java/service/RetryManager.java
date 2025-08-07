package service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RetryManager {
    private final ConcurrentHashMap<String, AtomicInteger> retryCounters = new ConcurrentHashMap<>();
    private final ErrorClassifier errorClassifier = new ErrorClassifier();
    
    public void scheduleRetry(String context, Exception error) {
        String key = context + "_" + error.getClass().getSimpleName();
        AtomicInteger counter = retryCounters.computeIfAbsent(key, k -> new AtomicInteger(0));
        
        int currentCount = counter.incrementAndGet();
        String errorType = errorClassifier.classifyError(error);
        int maxRetries = errorClassifier.getRetryCount(errorType);
        
        if (currentCount <= maxRetries) {
            System.out.println("Scheduling retry " + currentCount + "/" + maxRetries + " for: " + context);
        } else {
            System.out.println("Max retries exceeded for: " + context);
            retryCounters.remove(key);
        }
    }
    
    public boolean canRetry(String context, Exception error) {
        String key = context + "_" + error.getClass().getSimpleName();
        AtomicInteger counter = retryCounters.get(key);
        if (counter == null) return true;
        
        String errorType = errorClassifier.classifyError(error);
        return counter.get() < errorClassifier.getRetryCount(errorType);
    }
}