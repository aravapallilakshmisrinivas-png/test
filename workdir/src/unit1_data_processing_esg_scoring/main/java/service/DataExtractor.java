package service;

import model.ESGHolding;

import java.util.ArrayList;
import java.util.List;

public class DataExtractor {
    
    public List<ESGHolding> extractHoldings(List<String[]> csvData) {
        List<ESGHolding> holdings = new ArrayList<>();
        
        if (csvData.isEmpty()) return holdings;
        
        String[] headers = csvData.get(0);
        int[] columnIndices = mapColumnIndices(headers);
        
        for (int i = 1; i < csvData.size(); i++) {
            String[] row = csvData.get(i);
            try {
                ESGHolding holding = extractHolding(row, columnIndices);
                if (holding != null) {
                    holdings.add(holding);
                }
            } catch (Exception e) {
                System.err.println("Error extracting holding from row " + i + ": " + e.getMessage());
            }
        }
        
        return holdings;
    }

    private int[] mapColumnIndices(String[] headers) {
        int[] indices = new int[8]; // holdingId, symbol, companyName, sector, envScore, socScore, govScore, marketValue
        
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].toLowerCase().trim();
            if (header.contains("holdingid") || header.contains("holding_id")) indices[0] = i;
            else if (header.contains("symbol")) indices[1] = i;
            else if (header.contains("companyname") || header.contains("company_name")) indices[2] = i;
            else if (header.contains("sector")) indices[3] = i;
            else if (header.contains("environmentalscore") || header.contains("environmental_score")) indices[4] = i;
            else if (header.contains("socialscore") || header.contains("social_score")) indices[5] = i;
            else if (header.contains("governancescore") || header.contains("governance_score")) indices[6] = i;
            else if (header.contains("marketvalue") || header.contains("market_value")) indices[7] = i;
        }
        
        return indices;
    }

    private ESGHolding extractHolding(String[] row, int[] columnIndices) {
        if (row.length < 8) return null;
        
        try {
            String holdingId = row[columnIndices[0]].trim();
            String symbol = row[columnIndices[1]].trim();
            String companyName = row[columnIndices[2]].trim();
            String sector = row[columnIndices[3]].trim();
            Double envScore = Double.parseDouble(row[columnIndices[4]].trim());
            Double socScore = Double.parseDouble(row[columnIndices[5]].trim());
            Double govScore = Double.parseDouble(row[columnIndices[6]].trim());
            Double marketValue = Double.parseDouble(row[columnIndices[7]].trim());
            
            return new ESGHolding(holdingId, symbol, companyName, sector, envScore, socScore, govScore, marketValue);
            
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
}