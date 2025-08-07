package service;

import java.util.function.Supplier;

public class TransactionManager {
    private static final int MAX_RETRIES = 3;
    
    public <T> T executeInTransaction(Supplier<T> operation) {
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts < MAX_RETRIES) {
            try {
                return operation.get();
            } catch (Exception e) {
                lastException = e;
                attempts++;
                if (attempts < MAX_RETRIES) {
                    try {
                        Thread.sleep(100 * attempts); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        throw new RuntimeException("Transaction failed after " + MAX_RETRIES + " attempts", lastException);
    }
}