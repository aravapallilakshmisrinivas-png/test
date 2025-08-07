package service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class FileDetectionService {
    private final ConcurrentHashMap<String, Long> processedFiles = new ConcurrentHashMap<>();

    public List<String> detectNewFiles(String directoryPath) {
        List<String> newFiles = new ArrayList<>();
        File directory = new File(directoryPath);
        
        if (!directory.exists() || !directory.isDirectory()) {
            return newFiles;
        }
        
        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (files == null) return newFiles;
        
        for (File file : files) {
            String filePath = file.getAbsolutePath();
            long lastModified = file.lastModified();
            
            if (!processedFiles.containsKey(filePath) || processedFiles.get(filePath) < lastModified) {
                processedFiles.put(filePath, lastModified);
                newFiles.add(filePath);
            }
        }
        
        return newFiles;
    }

    public boolean isValidFile(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile() && file.canRead() && filePath.toLowerCase().endsWith(".csv");
    }
}