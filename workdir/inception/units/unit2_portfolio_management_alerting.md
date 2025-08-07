# Unit 2: Portfolio Monitoring and Alerting

## Unit Overview
This unit monitors portfolio ESG performance against configurable thresholds and provides alerting capabilities to notify stakeholders of sustainability risks requiring attention.

## Business Capability
- Real-time ESG threshold monitoring
- Portfolio performance analytics
- Email notification system
- Threshold configuration management

## User Stories

### US-004: Threshold-Based Monitoring
**As a** Portfolio Manager  
**I want** the system to monitor ESG scores against pre-configured thresholds  
**So that** I can identify portfolios requiring attention for sustainability risks

**Acceptance Criteria:**
- Monitors individual holding and portfolio-level ESG scores
- Uses pre-configured threshold values from system configuration
- Triggers alerts when scores fall below minimum thresholds
- Supports different thresholds for Environmental, Social, and Governance components

### US-005: Basic Portfolio Analytics
**As a** Portfolio Analyst  
**I want** to view fundamental ESG performance metrics for my portfolios  
**So that** I can assess overall sustainability performance

**Acceptance Criteria:**
- Displays portfolio-level ESG score summaries
- Shows distribution of holdings across ESG score ranges
- Provides sector-level ESG performance breakdowns
- Calculates basic statistics (average, min, max ESG scores)

### US-006: Email Notification System
**As a** Portfolio Manager  
**I want** to receive email notifications when ESG thresholds are breached  
**So that** I can take timely action on sustainability risks

**Acceptance Criteria:**
- Sends email alerts for threshold breaches within 15 minutes
- Includes portfolio details and specific ESG metrics that triggered alert
- Allows basic email preference settings (frequency, recipients)
- Provides clear, actionable information in notification content

### US-018: Pre-configured Threshold Management
**As a** System Administrator  
**I want** to manage ESG threshold values through configuration files  
**So that** the system can monitor portfolios against appropriate sustainability benchmarks

**Acceptance Criteria:**
- Stores threshold values in system configuration files
- Supports different thresholds for Environmental, Social, and Governance scores
- Validates threshold values are within acceptable ranges (0-100)
- Applies threshold changes without system restart

## Unit Boundaries and Responsibilities

### What This Unit Owns
- ESG threshold monitoring logic
- Portfolio performance analytics calculations
- Email notification system and templates
- Threshold configuration management
- Alert history and tracking

### What This Unit Does NOT Own
- ESG score calculations (consumed from Unit 1)
- Risk adjustment algorithms
- Dashboard user interfaces
- Interactive visualization features

## Integration Points

### Outbound Messages (Published Events)
- **ThresholdBreachDetected**: Published when ESG thresholds are violated
  - Payload: Portfolio ID, Threshold type, Current score, Threshold value, Severity
- **EmailNotificationSent**: Published when email alerts are dispatched
  - Payload: Recipient list, Alert type, Timestamp, Delivery status
- **PortfolioAnalyticsUpdated**: Published when portfolio metrics are recalculated
  - Payload: Portfolio ID, Analytics summary, Update timestamp

### Inbound Messages (Subscribed Events)
- **ESGDataProcessed**: Receives new ESG scores from Unit 1 for monitoring
  - Payload: Portfolio ID, Holdings with ESG scores, Processing timestamp
- **ThresholdConfigurationUpdated**: Receives threshold configuration changes
  - Payload: New threshold values, Configuration version

### Shared Data Models
- **PortfolioMetrics**: Aggregated ESG performance statistics
- **ThresholdAlert**: Alert details with severity and context
- **NotificationPreferences**: Email settings and recipient configurations

## Technical Considerations

### Monitoring Frequency
- Real-time evaluation upon receiving new ESG scores
- Batch monitoring every 15 minutes for comprehensive checks
- Configurable monitoring intervals

### Performance Requirements
- Evaluate thresholds within 30 seconds of receiving new scores
- Send email notifications within 15 minutes of threshold breach
- Support monitoring of up to 50 portfolios simultaneously

### Error Handling
- Retry mechanism for failed email deliveries
- Fallback notification methods if primary email fails
- Comprehensive logging of all monitoring activities

## Dependencies
- **Unit 1**: ESG scores and data processing events
- **External**: SMTP server for email notifications
- **Internal**: Configuration service for thresholds and email settings

## Success Criteria
- 100% detection rate for genuine threshold breaches
- Email notifications delivered within 15 minutes SLA
- Zero false positive alerts due to system errors
- 99.9% uptime for monitoring services