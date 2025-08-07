package component;

import service.ErrorClassifier;
import service.RetryManager;
import service.ErrorReporter;

public class ErrorHandlerComponent {
    private final ErrorClassifier errorClassifier;
    private final RetryManager retryManager;
    private final ErrorReporter errorReporter;
    
    public ErrorHandlerComponent() {
        this.errorClassifier = new ErrorClassifier();
        this.retryManager = new RetryManager();
        this.errorReporter = new ErrorReporter();
    }
    
    public void handleError(Exception error, String context) {
        String errorType = errorClassifier.classifyError(error);
        errorReporter.reportError(error, context, errorType);
        
        if (errorClassifier.isRetryable(error)) {
            retryManager.scheduleRetry(context, error);
        }
    }
    
    public boolean shouldRetry(Exception error) {
        return errorClassifier.isRetryable(error);
    }
}