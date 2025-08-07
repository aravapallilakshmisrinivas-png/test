package service;

import model.ESGHolding;
import model.Portfolio;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MockDatabase {
    private final Map<String, ESGHolding> esgHoldingsTable = new ConcurrentHashMap<>();
    private final Map<String, Portfolio> portfoliosTable = new ConcurrentHashMap<>();
    private final Map<String, Object> metadataTable = new ConcurrentHashMap<>();
    
    // ESG Holdings operations
    public void insertESGHolding(ESGHolding holding) {
        esgHoldingsTable.put(holding.getHoldingId(), holding);
    }
    
    public ESGHolding selectESGHolding(String holdingId) {
        return esgHoldingsTable.get(holdingId);
    }
    
    public List<ESGHolding> selectAllESGHoldings() {
        return new ArrayList<>(esgHoldingsTable.values());
    }
    
    // Portfolio operations
    public void insertPortfolio(Portfolio portfolio) {
        portfoliosTable.put(portfolio.getPortfolioId(), portfolio);
    }
    
    public Portfolio selectPortfolio(String portfolioId) {
        return portfoliosTable.get(portfolioId);
    }
    
    public List<Portfolio> selectAllPortfolios() {
        return new ArrayList<>(portfoliosTable.values());
    }
    
    // Connection simulation
    public boolean testConnection() {
        return true;
    }
    
    public void beginTransaction() {
        metadataTable.put("transaction_active", true);
    }
    
    public void commitTransaction() {
        metadataTable.put("transaction_active", false);
    }
    
    public void rollbackTransaction() {
        metadataTable.put("transaction_active", false);
    }
    
    public void clearAllTables() {
        esgHoldingsTable.clear();
        portfoliosTable.clear();
        metadataTable.clear();
    }
}