# US-004: Threshold-Based Monitoring - Detailed Specification

## User Story
**As a** Portfolio Manager  
**I want** the system to monitor ESG scores against pre-configured thresholds  
**So that** I can identify portfolios requiring attention for sustainability risks

## Detailed Description
The threshold-based monitoring system continuously evaluates ESG scores at both individual holding and portfolio aggregate levels against predefined sustainability benchmarks. When scores fall below acceptable thresholds, the system triggers alerts to enable proactive risk management.

## Expanded Acceptance Criteria

### AC-004.1: Individual Holding Monitoring
- **Given** a portfolio contains individual holdings with calculated ESG scores
- **When** any holding's composite ESG score falls below the configured minimum threshold
- **Then** the system flags the holding for attention and triggers an alert
- **And** the system records the threshold breach with timestamp and specific score values

### AC-004.2: Portfolio-Level Monitoring  
- **Given** a portfolio has an aggregate ESG score calculated from all holdings
- **When** the portfolio-level ESG score falls below the configured portfolio threshold
- **Then** the system triggers a portfolio-level alert
- **And** identifies which holdings are contributing most to the poor performance

### AC-004.3: Component-Specific Thresholds
- **Given** separate thresholds are configured for Environmental (E), Social (S), and Governance (G) components
- **When** any individual component score falls below its specific threshold
- **Then** the system triggers a component-specific alert
- **And** clearly identifies which ESG component is underperforming

### AC-004.4: Configuration-Driven Thresholds
- **Given** threshold values are stored in system configuration files
- **When** the monitoring system evaluates scores
- **Then** it uses the current configured threshold values without requiring system restart
- **And** supports different threshold levels (e.g., Warning: 40, Critical: 25)

## Detailed Scenarios

### Scenario 1: Individual Holding Breach
```
Given: 
- Holding "ABC Corp" has ESG score of 35
- Individual holding threshold is set to 40
- Portfolio contains 50 holdings

When: The monitoring system runs its evaluation cycle

Then:
- ABC Corp is flagged as below threshold
- Alert is triggered for this specific holding
- Portfolio manager receives notification about ABC Corp
- System logs: "Holding ABC Corp (35) below threshold (40) at 2024-01-15 14:30"
```

### Scenario 2: Portfolio Aggregate Breach
```
Given:
- Portfolio "Tech Growth Fund" has aggregate ESG score of 42
- Portfolio-level threshold is set to 45
- Portfolio contains 25 holdings

When: The monitoring system evaluates portfolio performance

Then:
- Portfolio-level alert is triggered
- System identifies top 5 holdings contributing to low score
- Portfolio manager receives portfolio-wide notification
- Alert includes breakdown of which holdings are dragging down the average
```

### Scenario 3: Component-Specific Breach
```
Given:
- Holding "XYZ Energy" has scores: E=25, S=65, G=70 (Composite=48)
- Environmental threshold is set to 30
- Social and Governance thresholds are set to 40

When: The monitoring system evaluates component scores

Then:
- Environmental component alert is triggered for XYZ Energy
- Alert specifies "Environmental score (25) below threshold (30)"
- No alerts for Social or Governance components
- Portfolio manager can focus on environmental issues specifically
```

## Technical Requirements

### Monitoring Frequency
- **Real-time**: Evaluate scores immediately after ESG calculation updates
- **Batch Processing**: Run comprehensive threshold checks every 15 minutes
- **Performance**: Complete evaluation cycle within 2 minutes for portfolios up to 1000 holdings

### Threshold Configuration Format
```
# Example configuration structure
thresholds:
  individual_holdings:
    composite_esg: 40
    environmental: 30
    social: 35
    governance: 35
  portfolio_level:
    composite_esg: 45
    environmental: 35
    social: 40
    governance: 40
  alert_levels:
    warning: 5 # points above threshold
    critical: 0 # at or below threshold
```

### Alert Prioritization
1. **Critical**: Score at or below threshold
2. **Warning**: Score within 5 points above threshold
3. **Watch**: Score within 10 points above threshold (monitoring only, no alert)

## Data Requirements

### Input Data
- Current ESG scores (composite and component-level)
- Portfolio holdings and weights
- Configured threshold values
- Historical threshold breach data

### Output Data
- Alert records with severity levels
- Threshold breach logs
- Performance trend indicators
- Holdings ranked by threshold proximity

## Integration Points

### Dependencies
- **US-002**: ESG Score Calculation (provides scores to monitor)
- **US-014**: Pre-configured Threshold Management (provides threshold values)
- **US-006**: Email Notification System (receives alert triggers)

### Data Flow
1. ESG scores calculated → 2. Threshold evaluation → 3. Alert generation → 4. Notification dispatch

## Performance Criteria
- **Latency**: Alert generation within 30 seconds of threshold breach
- **Accuracy**: 100% detection of threshold breaches (no false negatives)
- **Scalability**: Support monitoring of up to 10 portfolios with 1000 holdings each
- **Availability**: 99.5% uptime during business hours

## Error Handling
- **Missing Thresholds**: Use system default values and log warning
- **Invalid Scores**: Skip evaluation and log error for investigation
- **Configuration Errors**: Validate threshold ranges (0-100) and reject invalid values
- **System Failures**: Queue alerts for retry and maintain alert history

## Success Metrics
- **Alert Accuracy**: >99% of genuine threshold breaches detected
- **Response Time**: Average alert generation time <15 seconds
- **False Positive Rate**: <2% of alerts are false positives
- **User Satisfaction**: Portfolio managers can identify risk portfolios within 1 minute of threshold breach