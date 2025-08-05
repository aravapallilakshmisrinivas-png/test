# Unit 2: Portfolio Management & Alerting System

## Unit Overview

### Purpose
This unit handles portfolio-level ESG analysis, monitoring, and automated alerting functionality. It transforms individual holding ESG scores into portfolio insights and manages the notification system for ESG threshold breaches.

### Team Focus
Business logic and notification systems team with expertise in:
- Portfolio analysis and aggregation algorithms
- Business rule engines and threshold management
- Email notification systems and messaging
- Real-time monitoring and alerting systems

### Unit Boundaries
- **Input**: ESG scores from Unit 1, portfolio composition data, configuration from Unit 5
- **Output**: Portfolio ESG analytics, alert notifications, portfolio performance metrics
- **Scope**: Portfolio-focused business logic and automated notification system

## User Stories

### US-008: Portfolio ESG Score Monitoring
- **As a** portfolio manager
- **I want** to monitor ESG scores across my portfolio holdings
- **So that** I can identify ESG risks and opportunities

**Acceptance Criteria:**
- Portfolio-level ESG scores calculated as weighted averages
- Individual holding ESG scores are visible
- Portfolio composition by ESG rating bands is displayed
- ESG score changes are tracked and highlighted
- Historical ESG performance is available (last 12 months)

### US-009: Threshold-Based Alert Configuration
- **As a** system administrator
- **I want** to configure ESG score thresholds through system configuration files
- **So that** appropriate alerts are triggered when ESG performance deteriorates

**Acceptance Criteria:**
- Critical threshold (<30) triggers immediate email alerts
- Warning threshold (30-50) triggers daily digest emails
- Watch threshold (50-70) triggers weekly summary emails
- Threshold values are configurable through system files
- Alert frequency is configurable per threshold level

### US-010: ESG Threshold Breach Alerts
- **As a** portfolio manager
- **I want** to receive email notifications when portfolio holdings breach ESG thresholds
- **So that** I can take timely action on ESG risks

**Acceptance Criteria:**
- Immediate email sent for critical threshold breaches (<30)
- Daily digest email for warning threshold breaches (30-50)
- Weekly summary for watch threshold items (50-70)
- Email includes holding details and current ESG score
- Alert history is maintained for reference

### US-011: Portfolio-Level Alert Triggers
- **As a** portfolio manager
- **I want** to receive alerts when portfolio-level ESG metrics deteriorate significantly
- **So that** I can assess portfolio-wide ESG risks

**Acceptance Criteria:**
- Alert when portfolio ESG score drops >10 points in single update
- Alert when >25% of holdings fall below warning threshold
- Alert when any holding reaches critical threshold
- Portfolio-level alerts include summary statistics
- Alerts specify which holdings are driving the deterioration

## Unit Architecture

### Data Flow Within Unit
```
ESG Scores (Unit 1) → Portfolio Aggregation → Portfolio Analytics
        ↓                      ↓                     ↓
Portfolio Holdings → Threshold Monitoring → Alert Generation
        ↓                      ↓                     ↓
Configuration → Alert Rules Engine → Email Notifications
        ↓                      ↓                     ↓
Alert History ← Alert Tracking ← Notification Status
```

### Core Components
1. **Portfolio Aggregator**: Calculates portfolio-level ESG metrics from individual holdings
2. **Threshold Monitor**: Continuously monitors ESG scores against configured thresholds
3. **Alert Rules Engine**: Evaluates alert conditions and determines notification requirements
4. **Notification Service**: Manages email delivery and notification scheduling
5. **Alert History Manager**: Tracks and stores alert history and status
6. **Portfolio Analytics Engine**: Generates portfolio composition and performance analytics

### Data Models
- **Portfolio Holdings**: Portfolio composition with weights and ESG scores
- **Portfolio ESG Metrics**: Aggregated portfolio-level ESG performance
- **Alert Configurations**: Threshold values and notification settings
- **Alert History**: Record of all alerts generated and their status
- **Portfolio Analytics**: Historical performance and composition analysis

## Unit Interfaces

### External Dependencies
- **Unit 1 (ESG Scores API)**: Individual holding ESG scores and historical data
- **Unit 5 (Configuration API)**: Alert thresholds and notification settings
- **Email Service**: External email system for notification delivery (mocked for testing)

### Consumed APIs

#### From Unit 1 - ESG Scores API
- `GET /esg-scores/portfolio/{portfolio-id}` - All holdings ESG scores
- `GET /esg-scores/historical/{holding-id}` - Historical ESG trends
- `GET /esg-scores/{holding-id}` - Individual holding current score

#### From Unit 5 - Configuration API
- `GET /config/alert-thresholds` - Current threshold configurations
- `GET /config/notification-settings` - Email notification settings
- `GET /config/portfolio-settings` - Portfolio analysis parameters

### Provided APIs

#### Portfolio Analytics API
**Purpose**: Provides portfolio ESG analytics to other units
**Endpoints**:
- `GET /portfolio/{portfolio-id}/esg-summary` - Portfolio ESG overview
- `GET /portfolio/{portfolio-id}/composition` - Portfolio composition by ESG bands
- `GET /portfolio/{portfolio-id}/performance` - Historical portfolio ESG performance
- `GET /portfolio/{portfolio-id}/holdings` - Holdings with ESG scores and weights

**Data Format**:
```json
{
  "portfolio_id": "string",
  "calculation_date": "date",
  "portfolio_esg_score": "number (0-100)",
  "weighted_average_components": {
    "environmental_score": "number",
    "social_score": "number", 
    "governance_score": "number"
  },
  "composition_by_bands": {
    "critical_count": "number",
    "warning_count": "number",
    "watch_count": "number",
    "good_count": "number"
  },
  "top_performers": ["holding_id"],
  "bottom_performers": ["holding_id"],
  "score_changes": {
    "improved": "number",
    "declined": "number",
    "unchanged": "number"
  }
}
```

#### Alert Management API
**Purpose**: Provides alert history and status information
**Endpoints**:
- `GET /alerts/history/{portfolio-id}` - Alert history for portfolio
- `GET /alerts/active` - Currently active alerts
- `GET /alerts/status/{alert-id}` - Individual alert status
- `POST /alerts/acknowledge/{alert-id}` - Acknowledge alert

#### Portfolio Monitoring API
**Purpose**: Provides real-time portfolio monitoring data
**Endpoints**:
- `GET /monitoring/thresholds` - Current threshold configurations
- `GET /monitoring/status/{portfolio-id}` - Portfolio monitoring status
- `GET /monitoring/breaches/recent` - Recent threshold breaches

## Testing Strategy

### Unit Testing
- Portfolio aggregation calculation testing
- Threshold monitoring logic testing
- Alert rule evaluation testing
- Email notification formatting testing

### Integration Testing
- End-to-end portfolio analysis workflow
- Alert generation and delivery testing
- API integration with Unit 1 and Unit 5
- Email service integration testing

### Mock Dependencies
- **Unit 1 ESG Scores**: Mock ESG score data for various scenarios
- **Unit 5 Configuration**: Mock threshold and notification configurations
- **Email Service**: Mock email delivery for testing notifications

## Performance Requirements
- Portfolio ESG calculations complete within 30 seconds for 1000 holdings
- Alert evaluation and generation within 5 minutes of ESG score updates
- Email notifications delivered within 15 minutes of threshold breach
- Support monitoring of up to 100 portfolios simultaneously

## Quality Assurance
- Portfolio ESG scores must accurately reflect weighted averages of holdings
- Alert thresholds must be evaluated correctly with no false positives/negatives
- Email notifications must include all required information and formatting
- Alert history must be complete and accurate for audit purposes

## Business Rules

### Portfolio ESG Score Calculation
- Portfolio ESG score = Σ(holding_weight × holding_esg_score) for all holdings
- Component scores calculated using same weighted average approach
- Holdings with missing ESG scores excluded from calculation with warning

### Alert Threshold Logic
- **Critical (<30)**: Immediate email alert, escalation to management
- **Warning (30-50)**: Daily digest email, requires acknowledgment
- **Watch (50-70)**: Weekly summary email, monitoring only
- **Good (>70)**: Monthly reporting only, no alerts

### Portfolio-Level Alert Triggers
- **Score Drop**: Portfolio ESG score decreases >10 points in single update
- **Composition Deterioration**: >25% of holdings fall below warning threshold
- **Critical Holdings**: Any individual holding reaches critical threshold
- **Trend Analysis**: Consistent decline over 3 consecutive updates

## Deployment Considerations
- Requires database for storing portfolio analytics and alert history
- Needs scheduled jobs for daily/weekly alert processing
- Requires email service configuration for notification delivery
- Should include monitoring for alert processing and delivery

## Success Criteria
- All 4 user stories fully implemented and tested
- Portfolio analytics available via stable API
- Alert system generates accurate and timely notifications
- Portfolio managers receive appropriate ESG risk notifications
- Unit operates independently with clear interfaces to other units
