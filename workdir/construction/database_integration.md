# Database Integration Design

## Shared Database Schema

### Core Tables
```sql
-- ESG Holdings Data
esg_holdings (
  holding_id VARCHAR PRIMARY KEY,
  portfolio_id VARCHAR,
  symbol VARCHAR,
  sector VARCHAR,
  environmental_score DECIMAL,
  social_score DECIMAL,
  governance_score DECIMAL,
  composite_score DECIMAL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

-- Portfolio Analytics
portfolio_analytics (
  portfolio_id VARCHAR PRIMARY KEY,
  total_holdings INTEGER,
  average_esg_score DECIMAL,
  sector_breakdown JSON,
  last_calculated TIMESTAMP
);

-- Threshold Alerts
threshold_alerts (
  alert_id VARCHAR PRIMARY KEY,
  portfolio_id VARCHAR,
  holding_id VARCHAR,
  alert_type VARCHAR,
  severity VARCHAR,
  current_score DECIMAL,
  threshold_value DECIMAL,
  created_at TIMESTAMP
);

-- Risk Assessments
risk_assessments (
  assessment_id VARCHAR PRIMARY KEY,
  holding_id VARCHAR,
  portfolio_id VARCHAR,
  base_risk_score DECIMAL,
  esg_adjustment_factor DECIMAL,
  adjusted_risk_score DECIMAL,
  created_at TIMESTAMP
);

-- Processing Status
processing_status (
  process_id VARCHAR PRIMARY KEY,
  unit_name VARCHAR,
  status VARCHAR,
  last_updated TIMESTAMP,
  details JSON
);
```

## Data Access Patterns

### Unit 1: Data Processing
- **Writes**: esg_holdings, processing_status
- **Reads**: Configuration tables

### Unit 2: Portfolio Monitoring  
- **Reads**: esg_holdings (polling for new data)
- **Writes**: threshold_alerts, portfolio_analytics, email_notifications

### Unit 3: Risk Integration
- **Reads**: esg_holdings, external_risk_data
- **Writes**: risk_assessments

### Unit 4: Core Analytics
- **Reads**: esg_holdings, portfolio_analytics, risk_assessments
- **Writes**: dashboard_views, user_sessions

### Unit 5: Interactive Features
- **Reads**: All data tables for filtering/export
- **Writes**: user_interactions, export_requests

## Polling Strategy
- **Frequency**: Every 30 seconds for new data detection
- **Change Detection**: Using updated_at timestamps
- **Batch Processing**: Process changes in batches of 100 records
- **Connection Pooling**: Shared connection pool across components