# Cross-Unit Integration Overview - Domain Model Integration

## Domain Model Consistency Validation

### Shared Kernel Concepts
All units consistently use these core domain concepts:

#### HoldingIdentifier
- **Unit 1**: Used in ESGScoreCalculation aggregate
- **Unit 2**: Used in PortfolioHolding entity
- **Unit 3**: Used in ESGRiskAssessment aggregate
- **Unit 4**: Used in AnalyticsDataSet aggregate
- **Consistency**: Same structure and validation rules across all units

#### ESG Score (0-100 scale)
- **Unit 1**: Produces composite and component ESG scores
- **Unit 2**: Consumes for portfolio aggregation
- **Unit 3**: Consumes for risk adjustment calculations
- **Unit 4**: Consumes for visualization and analytics
- **Consistency**: Same scale, precision, and business rules

#### Threshold Values
- **Unit 2**: Uses for portfolio alerting (Critical <30, Warning 30-50, Watch 50-70)
- **Unit 3**: Uses for risk alerting with same threshold concepts
- **Unit 5**: Manages threshold configuration
- **Consistency**: Same threshold definitions and evaluation logic

## Anti-Corruption Layer Specifications

### Unit 1 → Unit 2 Integration
**Event**: ESGScoreCalculated
**ACL Transformation**: ESGScoreCalculated → PortfolioHoldingESGScore
```
PortfolioHoldingESGScore
├── HoldingId: ESGScoreCalculated.HoldingIdentifier
├── ESGScore: ESGScoreCalculated.CompositeESGScore.FinalESGScore
├── ComponentScores: Map from ESGScoreCalculated component entities
├── ScoreGrade: Derived from ESGScore using threshold rules
├── LastUpdated: ESGScoreCalculated.CalculationDate
└── QualityIndicator: ESGScoreCalculated.QualityIndicators
```

### Unit 1 → Unit 3 Integration
**Event**: ESGScoreCalculated
**ACL Transformation**: ESGScoreCalculated → RiskAdjustmentESGData
```
RiskAdjustmentESGData
├── HoldingId: ESGScoreCalculated.HoldingIdentifier
├── CompositeESGScore: ESGScoreCalculated.CompositeESGScore.FinalESGScore
├── EnvironmentalScore: ESGScoreCalculated.EnvironmentalScore.TotalEnvironmentalScore
├── SocialScore: ESGScoreCalculated.SocialScore.TotalSocialScore
├── GovernanceScore: ESGScoreCalculated.GovernanceScore.TotalGovernanceScore
├── CalculationDate: ESGScoreCalculated.CalculationDate
└── MethodologyVersion: ESGScoreCalculated.MethodologyVersion
```

### Unit 2 → Unit 4 Integration
**Event**: PortfolioESGScoreUpdated
**ACL Transformation**: PortfolioESGPerformance → DashboardPortfolioData
```
DashboardPortfolioData
├── PortfolioId: PortfolioESGPerformance.PortfolioId
├── PortfolioScore: PortfolioESGPerformance.PortfolioESGScore
├── CompositionBreakdown: PortfolioESGPerformance.CompositionAnalysis
├── TopPerformers: PortfolioESGPerformance.CompositionAnalysis.TopPerformers
├── BottomPerformers: PortfolioESGPerformance.CompositionAnalysis.BottomPerformers
├── PerformanceTrends: PortfolioESGPerformance.PerformanceHistory
└── LastUpdated: PortfolioESGPerformance.LastUpdated
```

### Unit 3 → Unit 4 Integration
**Event**: ESGRiskCalculated
**ACL Transformation**: ESGRiskAssessment → AnalyticsRiskData
```
AnalyticsRiskData
├── HoldingId: ESGRiskAssessment.HoldingIdentifier
├── TraditionalRisk: ESGRiskAssessment.BaseRiskData.TraditionalRiskScore
├── ESGAdjustedRisk: ESGRiskAssessment.ESGAdjustedRisk.AdjustedRiskScore
├── RiskAdjustmentFactor: ESGRiskAssessment.RiskAdjustmentCalculation.AdjustmentFactor
├── ESGRiskContribution: ESGRiskAssessment.ESGRiskFactors
├── RiskImprovement: ESGRiskAssessment.ESGAdjustedRisk.RiskImprovement
└── AssessmentDate: ESGRiskAssessment.AssessmentDate
```

### Unit 5 → All Units Integration
**Event**: ConfigurationUpdated
**ACL Transformations**:

#### To Unit 1: SystemConfiguration → ESGScoringParameters
```
ESGScoringParameters
├── MethodologyVersion: ESGMethodologyConfig.MethodologyVersion
├── EnvironmentalWeight: ESGMethodologyConfig.ComponentWeightings.Environmental (40%)
├── SocialWeight: ESGMethodologyConfig.ComponentWeightings.Social (30%)
├── GovernanceWeight: ESGMethodologyConfig.ComponentWeightings.Governance (30%)
├── ComponentWeights: ESGMethodologyConfig sub-component weights
└── EffectiveDate: ESGMethodologyConfig.EffectiveDate
```

#### To Unit 2: SystemConfiguration → ThresholdSettings
```
ThresholdSettings
├── CriticalThreshold: AlertThresholdConfig.CriticalThreshold (<30)
├── WarningThreshold: AlertThresholdConfig.WarningThreshold (30-50)
├── WatchThreshold: AlertThresholdConfig.WatchThreshold (50-70)
├── PortfolioThresholds: AlertThresholdConfig.PortfolioThresholds
├── AlertFrequencies: AlertThresholdConfig.AlertFrequencies
└── NotificationSettings: AlertThresholdConfig.NotificationSettings
```

## Published Language

### Cross-Context Event Contracts

#### ESGScoreCalculated (Unit 1 → Units 2, 3, 4)
```json
{
  "eventType": "ESGScoreCalculated",
  "version": "1.0",
  "holdingIdentifier": "string",
  "compositeESGScore": "decimal (0-100)",
  "environmentalScore": "decimal (0-40)",
  "socialScore": "decimal (0-30)",
  "governanceScore": "decimal (0-30)",
  "scoreGrade": "enum (Critical|Warning|Watch|Good)",
  "calculationDate": "datetime",
  "methodologyVersion": "string",
  "qualityIndicators": {
    "dataCompleteness": "decimal (0-100)",
    "calculationConfidence": "decimal (0-100)"
  }
}
```

#### PortfolioESGScoreUpdated (Unit 2 → Unit 4)
```json
{
  "eventType": "PortfolioESGScoreUpdated",
  "version": "1.0",
  "portfolioId": "string",
  "previousScore": "decimal (0-100)",
  "newScore": "decimal (0-100)",
  "scoreChange": "decimal",
  "componentChanges": {
    "environmental": "decimal",
    "social": "decimal",
    "governance": "decimal"
  },
  "updatedHoldings": ["string"],
  "calculationDate": "datetime"
}
```

#### ESGRiskCalculated (Unit 3 → Unit 4)
```json
{
  "eventType": "ESGRiskCalculated",
  "version": "1.0",
  "holdingIdentifier": "string",
  "traditionalRiskScore": "decimal",
  "esgAdjustedRiskScore": "decimal",
  "riskAdjustmentFactor": "decimal",
  "esgRiskContribution": {
    "environmental": "decimal",
    "social": "decimal",
    "governance": "decimal"
  },
  "calculationDate": "datetime",
  "methodologyVersion": "string"
}
```

#### ConfigurationUpdated (Unit 5 → All Units)
```json
{
  "eventType": "ConfigurationUpdated",
  "version": "1.0",
  "configurationId": "string",
  "previousVersion": "string",
  "newVersion": "string",
  "changedParameters": ["string"],
  "affectedContexts": ["string"],
  "effectiveDate": "datetime"
}
```

## Integration Event Flow

### ESG Score Calculation Flow
1. **Unit 5** publishes ConfigurationUpdated with ESG methodology
2. **Unit 1** receives configuration and processes ESG data
3. **Unit 1** publishes ESGScoreCalculated events
4. **Units 2, 3, 4** receive and process ESG scores through their ACLs
5. **Unit 2** publishes PortfolioESGScoreUpdated after aggregation
6. **Unit 3** publishes ESGRiskCalculated after risk integration
7. **Unit 4** aggregates all data for dashboard presentation

### Alert Generation Flow
1. **Unit 2** detects threshold breach using scores from Unit 1
2. **Unit 2** publishes ESGThresholdBreached event
3. **Unit 4** receives alert event for dashboard notification display
4. **Unit 3** may also generate RiskThresholdBreached events
5. **Unit 4** consolidates all alerts for unified user experience

## Eventual Consistency Strategy

### Consistency Guarantees
- **Strong Consistency**: Within each bounded context (aggregate boundaries)
- **Eventual Consistency**: Between bounded contexts (via events)
- **Compensation**: Error handling and retry mechanisms for failed integrations

### Event Ordering
- **Unit 1** events must be processed before **Unit 2** and **Unit 3** events
- **Unit 2** and **Unit 3** events can be processed in parallel
- **Unit 4** events depend on all other units but can handle partial data

### Error Handling
- **Dead Letter Queue**: Failed events moved to DLQ for manual intervention
- **Retry Policy**: Exponential backoff with maximum retry attempts
- **Circuit Breaker**: Prevent cascade failures between contexts
- **Compensation Actions**: Rollback or correction procedures for data inconsistencies

## Domain Model Integration Summary

### Validation Results
✅ **Consistency**: All units use consistent domain concepts and terminology
✅ **Cohesion**: Each unit maintains high internal cohesion with clear responsibilities
✅ **Coupling**: Loose coupling achieved through events and anti-corruption layers
✅ **Integration**: Clear integration patterns with well-defined transformations
✅ **Scalability**: Event-driven architecture supports independent scaling
✅ **Maintainability**: Bounded contexts enable independent evolution

### Key Integration Patterns
1. **Event-Driven Communication**: All cross-context communication via domain events
2. **Anti-Corruption Layers**: Protect each context from external changes
3. **Published Language**: Standardized event contracts for integration
4. **Eventual Consistency**: Accept temporary inconsistencies for better performance
5. **Compensation Patterns**: Handle integration failures gracefully

### Development Readiness
All domain models are complete and ready for implementation with:
- Clear aggregate boundaries and consistency rules
- Well-defined domain events for integration
- Comprehensive anti-corruption layer specifications
- Detailed transformation mappings between contexts
- Event-driven architecture supporting parallel development
