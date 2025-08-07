package service;

import model.ESGHolding;
import java.util.ArrayList;
import java.util.List;

public class SampleDataProvider {
    
    public static List<ESGHolding> getValidSampleData() {
        List<ESGHolding> holdings = new ArrayList<>();
        
        holdings.add(new ESGHolding("H001", "AAPL", "Apple Inc", "Technology", 85.5, 78.2, 92.1, 150000000.0));
        holdings.add(new ESGHolding("H002", "MSFT", "Microsoft Corp", "Technology", 88.7, 85.3, 89.4, 200000000.0));
        holdings.add(new ESGHolding("H003", "GOOGL", "Alphabet Inc", "Technology", 82.1, 79.8, 87.6, 180000000.0));
        holdings.add(new ESGHolding("H004", "TSLA", "Tesla Inc", "Automotive", 95.2, 72.4, 68.9, 120000000.0));
        holdings.add(new ESGHolding("H005", "JNJ", "Johnson & Johnson", "Healthcare", 76.8, 88.5, 91.2, 160000000.0));
        holdings.add(new ESGHolding("H006", "JPM", "JPMorgan Chase", "Financial", 65.4, 82.1, 85.7, 140000000.0));
        holdings.add(new ESGHolding("H007", "V", "Visa Inc", "Financial", 71.2, 86.3, 89.8, 110000000.0));
        holdings.add(new ESGHolding("H008", "PG", "Procter & Gamble", "Consumer Goods", 79.6, 84.7, 88.1, 130000000.0));
        holdings.add(new ESGHolding("H009", "UNH", "UnitedHealth Group", "Healthcare", 68.9, 79.4, 86.5, 170000000.0));
        holdings.add(new ESGHolding("H010", "HD", "Home Depot", "Retail", 73.5, 81.2, 84.6, 125000000.0));
        
        return holdings;
    }
    
    public static List<ESGHolding> getInvalidSampleData() {
        List<ESGHolding> holdings = new ArrayList<>();
        
        // Valid holding
        holdings.add(new ESGHolding("H011", "AMZN", "Amazon Inc", "Technology", 78.5, 82.1, 85.4, 190000000.0));
        
        // Invalid holdings with various issues
        ESGHolding invalidHolding1 = new ESGHolding("", "META", "Meta Platforms", "Technology", 74.2, 76.8, 82.1, 140000000.0); // Missing holding ID
        ESGHolding invalidHolding2 = new ESGHolding("H013", "GOOGL", "Alphabet Inc", "Technology", 150.1, 79.8, 87.6, 180000000.0); // Invalid environmental score > 100
        ESGHolding invalidHolding3 = new ESGHolding("H014", "TSLA", "Tesla Inc", "Automotive", 95.2, -10.4, 68.9, 120000000.0); // Negative social score
        ESGHolding invalidHolding4 = new ESGHolding("H016", "NFLX", "Netflix Inc", "Technology", 69.8, 78.5, 79.2, -50000.0); // Negative market value
        
        holdings.add(invalidHolding1);
        holdings.add(invalidHolding2);
        holdings.add(invalidHolding3);
        holdings.add(invalidHolding4);
        
        return holdings;
    }
}