package component;

import config.ApplicationConfig;
import service.FileDetectionService;
import service.FileQueueManager;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FileMonitorComponent {
    private final ApplicationConfig config = new ApplicationConfig();
    private final FileDetectionService detectionService = new FileDetectionService();
    private final FileQueueManager queueManager = new FileQueueManager();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private boolean isMonitoring = false;

    public void startMonitoring() {
        if (isMonitoring) return;
        
        isMonitoring = true;
        List<String> watchPaths = config.getWatchPaths();
        int intervalSeconds = config.getPollingIntervalSeconds();
        
        scheduler.scheduleAtFixedRate(() -> {
            for (String path : watchPaths) {
                List<String> newFiles = detectionService.detectNewFiles(path);
                for (String file : newFiles) {
                    queueManager.addFileToQueue(file);
                }
            }
        }, 0, intervalSeconds, TimeUnit.SECONDS);
        
        System.out.println("File monitoring started for paths: " + watchPaths);
    }

    public void stopMonitoring() {
        isMonitoring = false;
        scheduler.shutdown();
        System.out.println("File monitoring stopped");
    }

    public List<String> getQueuedFiles() {
        return queueManager.getQueuedFiles();
    }

    public String getNextFile() {
        return queueManager.getNextFile();
    }

    public boolean hasQueuedFiles() {
        return queueManager.hasFiles();
    }
}