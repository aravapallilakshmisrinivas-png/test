# Portfolio Monitoring User Stories

## US-004: Threshold-Based Monitoring
**As a** Portfolio Manager  
**I want** the system to monitor ESG scores against pre-configured thresholds  
**So that** I can identify portfolios requiring attention for sustainability risks

**Acceptance Criteria:**
- Monitors individual holding and portfolio-level ESG scores
- Uses pre-configured threshold values from system configuration
- Triggers alerts when scores fall below minimum thresholds
- Supports different thresholds for Environmental, Social, and Governance components

## US-005: Basic Portfolio Analytics
**As a** Portfolio Analyst  
**I want** to view fundamental ESG performance metrics for my portfolios  
**So that** I can assess overall sustainability performance

**Acceptance Criteria:**
- Displays portfolio-level ESG score summaries
- Shows distribution of holdings across ESG score ranges
- Provides sector-level ESG performance breakdowns
- Calculates basic statistics (average, min, max ESG scores)

## US-006: Email Notification System
**As a** Portfolio Manager  
**I want** to receive email notifications when ESG thresholds are breached  
**So that** I can take timely action on sustainability risks

**Acceptance Criteria:**
- Sends email alerts for threshold breaches within 15 minutes
- Includes portfolio details and specific ESG metrics that triggered alert
- Allows basic email preference settings (frequency, recipients)
- Provides clear, actionable information in notification content