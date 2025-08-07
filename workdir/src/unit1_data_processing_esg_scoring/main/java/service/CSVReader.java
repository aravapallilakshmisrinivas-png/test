package service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
    
    public List<String[]> readCSV(String filePath) throws IOException {
        List<String[]> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(parseCSVLine(line));
            }
        }
        return result;
    }
    
    private String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(field.toString().trim());
                field = new StringBuilder();
            } else {
                field.append(c);
            }
        }
        fields.add(field.toString().trim());
        return fields.toArray(new String[0]);
    }

    public boolean isValidCSVStructure(List<String[]> csvData) {
        if (csvData.isEmpty()) return false;
        
        // Check header row
        String[] headers = csvData.get(0);
        return headers.length >= 7 && 
               containsRequiredHeaders(headers);
    }

    private boolean containsRequiredHeaders(String[] headers) {
        boolean hasHoldingId = false, hasSymbol = false, hasCompanyName = false, hasSector = false;
        boolean hasEnvScore = false, hasSocScore = false, hasGovScore = false, hasMarketValue = false;
        
        for (String header : headers) {
            String h = header.toLowerCase().trim();
            if (h.contains("holdingid") || h.contains("holding_id")) hasHoldingId = true;
            if (h.contains("symbol")) hasSymbol = true;
            if (h.contains("companyname") || h.contains("company_name")) hasCompanyName = true;
            if (h.contains("sector")) hasSector = true;
            if (h.contains("environmentalscore") || h.contains("environmental_score")) hasEnvScore = true;
            if (h.contains("socialscore") || h.contains("social_score")) hasSocScore = true;
            if (h.contains("governancescore") || h.contains("governance_score")) hasGovScore = true;
            if (h.contains("marketvalue") || h.contains("market_value")) hasMarketValue = true;
        }
        
        return hasHoldingId && hasSymbol && hasCompanyName && hasSector && 
               hasEnvScore && hasSocScore && hasGovScore && hasMarketValue;
    }
}