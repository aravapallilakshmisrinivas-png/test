package repository;

import model.Portfolio;
import model.ESGHolding;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PortfolioRepository {
    private final ConcurrentHashMap<String, Portfolio> portfolios = new ConcurrentHashMap<>();
    
    public void savePortfolio(Portfolio portfolio) {
        portfolios.put(portfolio.getPortfolioId(), portfolio);
    }
    
    public Portfolio getPortfolio(String portfolioId) {
        return portfolios.get(portfolioId);
    }
    
    public List<Portfolio> getAllPortfolios() {
        return new ArrayList<>(portfolios.values());
    }
    
    public void addHoldingToPortfolio(String portfolioId, ESGHolding holding) {
        Portfolio portfolio = portfolios.get(portfolioId);
        if (portfolio != null) {
            portfolio.addHolding(holding);
        }
    }
    
    public void clearAll() {
        portfolios.clear();
    }
    
    public int getPortfolioCount() {
        return portfolios.size();
    }
}