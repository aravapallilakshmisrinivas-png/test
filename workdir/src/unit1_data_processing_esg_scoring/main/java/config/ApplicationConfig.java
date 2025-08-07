package config;

import java.util.List;
import java.util.Arrays;

public class ApplicationConfig {
    private final ConfigurationManager configManager = ConfigurationManager.getInstance();

    // File monitoring settings
    public List<String> getWatchPaths() {
        String paths = configManager.getProperty("file.watch.paths", String.class);
        return paths != null ? Arrays.asList(paths.split(",")) : Arrays.asList("./sample-data");
    }

    public int getPollingIntervalSeconds() {
        return configManager.getProperty("file.polling.interval.seconds", Integer.class) != null ?
               configManager.getProperty("file.polling.interval.seconds", Integer.class) : 5;
    }

    // Processing settings
    public int getThreadPoolSize() {
        return configManager.getProperty("processing.thread.pool.size", Integer.class) != null ?
               configManager.getProperty("processing.thread.pool.size", Integer.class) : 4;
    }

    public int getBatchSize() {
        return configManager.getProperty("processing.batch.size", Integer.class) != null ?
               configManager.getProperty("processing.batch.size", Integer.class) : 1000;
    }

    public boolean isParallelProcessingEnabled() {
        return configManager.getProperty("processing.parallel.enabled", Boolean.class) != null ?
               configManager.getProperty("processing.parallel.enabled", Boolean.class) : true;
    }

    // Validation settings
    public double getQualityThreshold() {
        return configManager.getProperty("validation.quality.threshold", Double.class) != null ?
               configManager.getProperty("validation.quality.threshold", Double.class) : 0.85;
    }

    // ESG calculation settings
    public double getEnvironmentalWeight() {
        return configManager.getProperty("esg.weights.environmental", Double.class) != null ?
               configManager.getProperty("esg.weights.environmental", Double.class) : 0.40;
    }

    public double getSocialWeight() {
        return configManager.getProperty("esg.weights.social", Double.class) != null ?
               configManager.getProperty("esg.weights.social", Double.class) : 0.30;
    }

    public double getGovernanceWeight() {
        return configManager.getProperty("esg.weights.governance", Double.class) != null ?
               configManager.getProperty("esg.weights.governance", Double.class) : 0.30;
    }
}