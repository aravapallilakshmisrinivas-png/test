import component.ESGCalculationEngine;
import model.ESGHolding;
import model.ESGScores;
import org.junit.Test;
import static org.junit.Assert.*;

public class ESGCalculationEngineTest {
    
    @Test
    public void testValidESGCalculation() {
        ESGCalculationEngine engine = new ESGCalculationEngine();
        ESGHolding holding = new ESGHolding("TEST001", "Test Company", "Technology");
        holding.setEnvironmentalScore(80.0);
        holding.setSocialScore(70.0);
        holding.setGovernanceScore(90.0);
        
        ESGScores result = engine.calculateESGScore(holding);
        
        assertNotNull(result);
        assertEquals(79.0, result.getOverallScore(), 0.1); // 80*0.4 + 70*0.3 + 90*0.3
    }
    
    @Test
    public void testInvalidScoreHandling() {
        ESGCalculationEngine engine = new ESGCalculationEngine();
        ESGHolding holding = new ESGHolding("TEST002", "Invalid Company", "Technology");
        holding.setEnvironmentalScore(-10.0);
        holding.setSocialScore(70.0);
        holding.setGovernanceScore(90.0);
        
        ESGScores result = engine.calculateESGScore(holding);
        
        assertNull(result);
    }
}