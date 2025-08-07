package service;

public class ErrorClassifier {
    
    public String classifyError(Exception error) {
        if (error instanceof IllegalArgumentException) {
            return "VALIDATION_ERROR";
        } else if (error instanceof RuntimeException) {
            return "PROCESSING_ERROR";
        } else if (error instanceof InterruptedException) {
            return "THREAD_ERROR";
        } else {
            return "UNKNOWN_ERROR";
        }
    }
    
    public boolean isRetryable(Exception error) {
        return !(error instanceof IllegalArgumentException) && 
               !(error instanceof NullPointerException);
    }
    
    public int getRetryCount(String errorType) {
        switch (errorType) {
            case "PROCESSING_ERROR": return 3;
            case "THREAD_ERROR": return 2;
            default: return 1;
        }
    }
}