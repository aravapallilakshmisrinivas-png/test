package service;

import java.io.File;

public class FormatValidator {
    
    public boolean validateFormat(String filePath) {
        File file = new File(filePath);
        
        // Basic file checks
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            return false;
        }
        
        // Check file extension
        if (!filePath.toLowerCase().endsWith(".csv")) {
            return false;
        }
        
        // Check file size (max 100MB as per config)
        long maxSizeBytes = 100 * 1024 * 1024; // 100MB
        if (file.length() > maxSizeBytes) {
            System.err.println("File too large: " + file.length() + " bytes");
            return false;
        }
        
        return true;
    }

    public boolean validateCSVContent(String filePath) {
        try {
            CSVReader csvReader = new CSVReader();
            var csvData = csvReader.readCSV(filePath);
            return csvReader.isValidCSVStructure(csvData);
        } catch (Exception e) {
            System.err.println("CSV content validation failed: " + e.getMessage());
            return false;
        }
    }
}