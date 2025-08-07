package component;

import model.ESGHolding;
import model.Portfolio;
import service.MockDatabaseClient;
import service.TransactionManager;
import java.util.List;

public class DatabaseWriterComponent {
    private final MockDatabaseClient databaseClient;
    private final TransactionManager transactionManager;
    
    public DatabaseWriterComponent() {
        this.databaseClient = new MockDatabaseClient();
        this.transactionManager = new TransactionManager();
    }
    
    public boolean writeESGHoldings(List<ESGHolding> holdings) {
        return transactionManager.executeInTransaction(() -> {
            for (ESGHolding holding : holdings) {
                databaseClient.insertESGHolding(holding);
            }
            return true;
        });
    }
    
    public boolean writePortfolio(Portfolio portfolio) {
        return transactionManager.executeInTransaction(() -> {
            databaseClient.insertPortfolio(portfolio);
            return true;
        });
    }
    
    public boolean updateESGScores(List<ESGHolding> holdings) {
        return transactionManager.executeInTransaction(() -> {
            for (ESGHolding holding : holdings) {
                databaseClient.updateESGScores(holding);
            }
            return true;
        });
    }
}