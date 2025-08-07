package service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorReporter {
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public void reportError(Exception error, String context, String errorType) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        System.err.println("[" + timestamp + "] ERROR [" + errorType + "] in " + context + ": " + error.getMessage());
        
        if (error.getCause() != null) {
            System.err.println("Caused by: " + error.getCause().getMessage());
        }
    }
    
    public void reportWarning(String message, String context) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        System.out.println("[" + timestamp + "] WARNING in " + context + ": " + message);
    }
    
    public void reportInfo(String message, String context) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        System.out.println("[" + timestamp + "] INFO in " + context + ": " + message);
    }
}