# Unit 3: Risk Integration

## Unit Overview
This unit specializes in integrating ESG factors into credit risk assessments, providing ESG-adjusted risk calculations using linear adjustment models for comprehensive risk analysis.

## Business Capability
- ESG-adjusted credit risk calculations
- Linear adjustment model implementation
- Risk assessment integration and comparison
- Risk calculation transparency and auditability

## User Stories

### US-007: ESG-Adjusted Credit Risk Calculation
**As a** Credit Risk Officer  
**I want** the system to calculate ESG-adjusted credit risk scores  
**So that** I can incorporate sustainability factors into risk assessments

**Acceptance Criteria:**
- Applies linear adjustment model to base credit risk scores
- Uses ESG scores to modify risk calculations uniformly across sectors
- Produces adjusted risk scores on standardized scale
- Maintains traceability between base and adjusted risk scores

### US-008: Linear Adjustment Model Implementation
**As a** Credit Risk Officer  
**I want** the system to use consistent linear models for ESG risk adjustments  
**So that** risk calculations are transparent and reproducible

**Acceptance Criteria:**
- Implements predefined linear adjustment formulas
- Applies same adjustment methodology across all sectors
- Validates adjustment parameters are within acceptable ranges
- Documents adjustment calculations for audit purposes

### US-009: Risk Assessment Integration
**As a** Credit Risk Officer  
**I want** to view integrated ESG and credit risk metrics  
**So that** I can make informed decisions about portfolio risk exposure

**Acceptance Criteria:**
- Displays both original and ESG-adjusted risk scores
- Shows impact of ESG factors on overall risk assessment
- Provides risk score comparisons across portfolio holdings
- Enables filtering and sorting by risk adjustment magnitude

## Unit Boundaries and Responsibilities

### What This Unit Owns
- ESG-adjusted risk calculation algorithms
- Linear adjustment model implementation
- Risk score comparison and analysis logic
- Risk calculation audit trails
- Risk assessment data models

### What This Unit Does NOT Own
- Base credit risk score calculations (external input)
- ESG score calculations (consumed from Unit 1)
- Risk visualization dashboards
- Portfolio monitoring and alerting

## Integration Points

### Outbound Messages (Published Events)
- **RiskScoresCalculated**: Published when ESG-adjusted risk scores are computed
  - Payload: Portfolio ID, Holdings with original and adjusted risk scores, Calculation timestamp
- **RiskAdjustmentCompleted**: Published when risk adjustment process finishes
  - Payload: Portfolio ID, Adjustment summary, Processing metrics
- **RiskCalculationFailed**: Published when risk calculations encounter errors
  - Payload: Portfolio ID, Error details, Failed holdings count

### Inbound Messages (Subscribed Events)
- **ESGDataProcessed**: Receives ESG scores from Unit 1 for risk adjustment
  - Payload: Portfolio ID, Holdings with ESG scores, Processing timestamp
- **BaseRiskScoresUpdated**: Receives base credit risk scores from external systems
  - Payload: Portfolio ID, Holdings with base risk scores, Update timestamp

### Shared Data Models
- **RiskAssessment**: Combined ESG and credit risk metrics for holdings
- **AdjustmentCalculation**: Details of linear adjustment applied to risk scores
- **RiskComparison**: Side-by-side comparison of original vs adjusted risk scores

## Technical Considerations

### Calculation Models
- Linear adjustment formula: Adjusted Risk = Base Risk × (1 + ESG Impact Factor)
- ESG Impact Factor derived from normalized ESG scores
- Configurable adjustment parameters for different risk scenarios

### Performance Requirements
- Calculate risk adjustments within 1 minute for portfolios up to 1000 holdings
- Support concurrent risk calculations for multiple portfolios
- Maintain calculation history for audit purposes

### Data Integrity
- Validate all input scores before processing
- Ensure calculation reproducibility with identical inputs
- Comprehensive logging of all adjustment calculations

## Dependencies
- **Unit 1**: ESG scores for risk adjustment calculations
- **External**: Base credit risk scores from risk management systems
- **Internal**: Configuration service for adjustment parameters

## Success Criteria
- 100% accuracy in risk adjustment calculations
- Risk calculations complete within 1 minute SLA
- Full audit trail maintained for all calculations
- Zero calculation errors for valid input data