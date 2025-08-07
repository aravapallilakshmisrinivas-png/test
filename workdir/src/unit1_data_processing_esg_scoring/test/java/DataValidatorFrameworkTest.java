import component.DataValidatorFramework;
import model.ESGHolding;
import org.junit.Test;
import static org.junit.Assert.*;

public class DataValidatorFrameworkTest {
    
    @Test
    public void testValidDataValidation() {
        DataValidatorFramework validator = new DataValidatorFramework();
        ESGHolding holding = new ESGHolding("TEST001", "Test Company", "Technology");
        holding.setEnvironmentalScore(80.0);
        holding.setSocialScore(70.0);
        holding.setGovernanceScore(90.0);
        
        DataValidatorFramework.ValidationResult result = validator.validateHolding(holding);
        
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }
    
    @Test
    public void testInvalidDataValidation() {
        DataValidatorFramework validator = new DataValidatorFramework();
        ESGHolding holding = new ESGHolding("", "", "");
        holding.setEnvironmentalScore(-10.0);
        holding.setSocialScore(150.0);
        holding.setGovernanceScore(90.0);
        
        DataValidatorFramework.ValidationResult result = validator.validateHolding(holding);
        
        assertFalse(result.isValid());
        assertFalse(result.getErrors().isEmpty());
    }
}