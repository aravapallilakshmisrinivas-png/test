package service;

import model.ESGHolding;
import model.Portfolio;
import repository.InMemoryDataStore;

public class MockDatabaseClient {
    private final InMemoryDataStore<String, ESGHolding> holdingsTable = new InMemoryDataStore<>();
    private final InMemoryDataStore<String, Portfolio> portfolioTable = new InMemoryDataStore<>();
    
    public void insertESGHolding(ESGHolding holding) {
        holdingsTable.put(holding.getHoldingId(), holding);
    }
    
    public void insertPortfolio(Portfolio portfolio) {
        portfolioTable.put(portfolio.getPortfolioId(), portfolio);
    }
    
    public void updateESGScores(ESGHolding holding) {
        holdingsTable.put(holding.getHoldingId(), holding);
    }
    
    public ESGHolding getHolding(String holdingId) {
        return holdingsTable.get(holdingId);
    }
    
    public Portfolio getPortfolio(String portfolioId) {
        return portfolioTable.get(portfolioId);
    }
    
    public void clearAllTables() {
        holdingsTable.clear();
        portfolioTable.clear();
    }
}