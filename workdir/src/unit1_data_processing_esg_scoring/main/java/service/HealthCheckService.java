package service;

import java.util.HashMap;
import java.util.Map;

public class HealthCheckService {
    private final Map<String, Boolean> componentHealth = new HashMap<>();
    
    public HealthCheckService() {
        initializeComponents();
    }
    
    private void initializeComponents() {
        componentHealth.put("FileMonitor", true);
        componentHealth.put("CSVIngestion", true);
        componentHealth.put("DataValidator", true);
        componentHealth.put("ESGCalculation", true);
        componentHealth.put("DatabaseWriter", true);
        componentHealth.put("ErrorHandler", true);
    }
    
    public boolean isSystemHealthy() {
        return componentHealth.values().stream().allMatch(Boolean::booleanValue);
    }
    
    public Map<String, Boolean> getComponentHealth() {
        return new HashMap<>(componentHealth);
    }
    
    public void updateComponentHealth(String component, boolean healthy) {
        componentHealth.put(component, healthy);
    }
    
    public String getHealthStatus() {
        if (isSystemHealthy()) {
            return "HEALTHY";
        } else {
            long unhealthyCount = componentHealth.values().stream().mapToLong(h -> h ? 0 : 1).sum();
            return "DEGRADED (" + unhealthyCount + " components down)";
        }
    }
}