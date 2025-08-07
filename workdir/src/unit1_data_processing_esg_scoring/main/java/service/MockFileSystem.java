package service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MockFileSystem {
    private final Map<String, String> files = new ConcurrentHashMap<>();
    private final List<String> watchedDirectories = new ArrayList<>();
    
    public MockFileSystem() {
        initializeSampleFiles();
    }
    
    private void initializeSampleFiles() {
        // Valid ESG data file
        String validData = "HoldingId,CompanyName,Sector,EnvironmentalScore,SocialScore,GovernanceScore\n" +
                          "AAPL001,Apple Inc.,Technology,85,78,92\n" +
                          "MSFT001,Microsoft Corp.,Technology,88,82,89\n" +
                          "AMZN001,Amazon.com Inc.,Consumer Discretionary,72,65,78\n";
        files.put("/data/valid_esg_data.csv", validData);
        
        // Invalid ESG data file
        String invalidData = "HoldingId,CompanyName,Sector,EnvironmentalScore,SocialScore,GovernanceScore\n" +
                            "INV001,Invalid Corp A,Technology,-5,75,80\n" +
                            "INV002,Invalid Corp B,Healthcare,85,150,70\n";
        files.put("/data/invalid_esg_data.csv", invalidData);
    }
    
    public String readFile(String filePath) {
        return files.get(filePath);
    }
    
    public boolean fileExists(String filePath) {
        return files.containsKey(filePath);
    }
    
    public List<String> listFiles(String directory) {
        return files.keySet().stream()
                   .filter(path -> path.startsWith(directory))
                   .toList();
    }
    
    public void addFile(String filePath, String content) {
        files.put(filePath, content);
    }
    
    public void watchDirectory(String directory) {
        if (!watchedDirectories.contains(directory)) {
            watchedDirectories.add(directory);
        }
    }
    
    public List<String> getNewFiles() {
        // Simulate new file detection
        return listFiles("/data/");
    }
}