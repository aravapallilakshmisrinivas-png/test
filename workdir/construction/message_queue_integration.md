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
```

## Data Access Patterns
- **Unit 1**: Writes to esg_holdings, processing_status
- **Unit 2**: Polls esg_holdings, writes alerts and analytics
- **Unit 3**: Reads esg_holdings, writes risk_assessments
- **Unit 4**: Reads all tables for dashboard display
- **Unit 5**: Reads all tables for interactive features

## Polling Strategy
- **Frequency**: Every 30 seconds for change detection
- **Change Detection**: Using updated_at timestamps
- **Batch Processing**: Process changes in batches
- **Connection Pooling**: Shared database connections