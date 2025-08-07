package service;

import model.ESGHolding;
import model.ValidationError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ValidationRuleEngine {
    
    public List<ValidationError> validateHoldings(List<ESGHolding> holdings) {
        List<ValidationError> errors = new ArrayList<>();
        Set<String> seenHoldingIds = new HashSet<>();
        
        for (ESGHolding holding : holdings) {
            errors.addAll(validateHolding(holding));
            
            // Check for duplicate holding IDs
            if (seenHoldingIds.contains(holding.getHoldingId())) {
                errors.add(new ValidationError(
                    UUID.randomUUID().toString(),
                    holding.getHoldingId(),
                    "holdingId",
                    "DUPLICATE",
                    "Duplicate holding ID found"
                ));
            } else {
                seenHoldingIds.add(holding.getHoldingId());
            }
        }
        
        return errors;
    }

    private List<ValidationError> validateHolding(ESGHolding holding) {
        List<ValidationError> errors = new ArrayList<>();
        
        // Required field validation
        if (isNullOrEmpty(holding.getHoldingId())) {
            errors.add(createValidationError(holding.getHoldingId(), "holdingId", "MISSING", "Holding ID is required"));
        }
        if (isNullOrEmpty(holding.getSymbol())) {
            errors.add(createValidationError(holding.getHoldingId(), "symbol", "MISSING", "Symbol is required"));
        }
        if (isNullOrEmpty(holding.getCompanyName())) {
            errors.add(createValidationError(holding.getHoldingId(), "companyName", "MISSING", "Company name is required"));
        }
        if (isNullOrEmpty(holding.getSector())) {
            errors.add(createValidationError(holding.getHoldingId(), "sector", "MISSING", "Sector is required"));
        }
        
        // Score range validation (0-100)
        if (holding.getEnvironmentalScore() != null && !isValidScore(holding.getEnvironmentalScore())) {
            errors.add(createValidationError(holding.getHoldingId(), "environmentalScore", "OUT_OF_RANGE", "Environmental score must be between 0 and 100"));
        }
        if (holding.getSocialScore() != null && !isValidScore(holding.getSocialScore())) {
            errors.add(createValidationError(holding.getHoldingId(), "socialScore", "OUT_OF_RANGE", "Social score must be between 0 and 100"));
        }
        if (holding.getGovernanceScore() != null && !isValidScore(holding.getGovernanceScore())) {
            errors.add(createValidationError(holding.getHoldingId(), "governanceScore", "OUT_OF_RANGE", "Governance score must be between 0 and 100"));
        }
        
        // Market value validation
        if (holding.getMarketValue() != null && holding.getMarketValue() <= 0) {
            errors.add(createValidationError(holding.getHoldingId(), "marketValue", "INVALID", "Market value must be greater than 0"));
        }
        
        return errors;
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isValidScore(Double score) {
        return score >= 0.0 && score <= 100.0;
    }

    private ValidationError createValidationError(String recordId, String fieldName, String errorType, String message) {
        ValidationError error = new ValidationError(UUID.randomUUID().toString(), recordId, fieldName, errorType, message);
        error.setSeverity("ERROR");
        return error;
    }
}