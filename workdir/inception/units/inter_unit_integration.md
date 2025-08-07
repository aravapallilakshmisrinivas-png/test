# Inter-Unit Integration Specification

## Integration Overview
This document defines the communication patterns, data contracts, and integration points between the 5 units of the ESG Data Platform using message queue-based architecture.

## Message Queue Architecture

### Message Broker
- **Technology**: Apache Kafka / RabbitMQ / AWS SQS (configurable)
- **Pattern**: Event-driven, publish-subscribe model
- **Delivery**: At-least-once delivery with idempotent consumers
- **Ordering**: Partition-based ordering for related events

### Topic Structure
```
esg-platform/
├── data-processing/
│   ├── esg-data-processed
│   ├── data-validation-failed
│   └── file-processing-completed
├── portfolio-monitoring/
│   ├── threshold-breach-detected
│   ├── email-notification-sent
│   └── portfolio-analytics-updated
├── risk-integration/
│   ├── risk-scores-calculated
│   ├── risk-adjustment-completed
│   └── risk-calculation-failed
├── core-analytics/
│   ├── dashboard-view-accessed
│   ├── drill-down-performed
│   └── system-configuration-updated
└── interactive-features/
    ├── filter-applied
    ├── data-exported
    └── chart-interaction-performed
```

## Data Contracts

### ESGDataProcessed Event
```json
{
  "eventId": "uuid",
  "timestamp": "2024-01-15T10:30:00Z",
  "eventType": "ESGDataProcessed",
  "version": "1.0",
  "source": "unit1-data-processing",
  "data": {
    "portfolioId": "PORTFOLIO_001",
    "processingTimestamp": "2024-01-15T10:29:45Z",
    "holdings": [
      {
        "holdingId": "HOLDING_001",
        "symbol": "AAPL",
        "sector": "Technology",
        "esgScores": {
          "environmental": 75.5,
          "social": 82.3,
          "governance": 88.1,
          "composite": 81.2
        }
      }
    ],
    "portfolioMetrics": {
      "totalHoldings": 150,
      "averageESG": 78.5,
      "sectorBreakdown": {
        "Technology": 45.2,
        "Healthcare": 23.1,
        "Finance": 31.7
      }
    }
  }
}
```

### ThresholdBreachDetected Event
```json
{
  "eventId": "uuid",
  "timestamp": "2024-01-15T10:35:00Z",
  "eventType": "ThresholdBreachDetected",
  "version": "1.0",
  "source": "unit2-portfolio-monitoring",
  "data": {
    "portfolioId": "PORTFOLIO_001",
    "breachType": "INDIVIDUAL_HOLDING",
    "severity": "CRITICAL",
    "thresholdDetails": {
      "component": "environmental",
      "currentScore": 35.2,
      "thresholdValue": 40.0,
      "breachMagnitude": -4.8
    },
    "affectedHolding": {
      "holdingId": "HOLDING_002",
      "symbol": "XOM",
      "sector": "Energy"
    }
  }
}
```

### RiskScoresCalculated Event
```json
{
  "eventId": "uuid",
  "timestamp": "2024-01-15T10:40:00Z",
  "eventType": "RiskScoresCalculated",
  "version": "1.0",
  "source": "unit3-risk-integration",
  "data": {
    "portfolioId": "PORTFOLIO_001",
    "calculationTimestamp": "2024-01-15T10:39:30Z",
    "riskAssessments": [
      {
        "holdingId": "HOLDING_001",
        "baseRiskScore": 65.0,
        "esgAdjustmentFactor": 0.15,
        "adjustedRiskScore": 74.75,
        "adjustmentReason": "Strong ESG performance reduces risk"
      }
    ]
  }
}
```

## Integration Patterns

### 1. Data Flow Pattern (Unit 1 → Units 2,3,4,5)
```
Unit 1 (Data Processing)
    ↓ publishes ESGDataProcessed
    ├→ Unit 2 (Portfolio Monitoring) → triggers threshold checks
    ├→ Unit 3 (Risk Integration) → calculates risk adjustments
    ├→ Unit 4 (Core Analytics) → updates dashboard data
    └→ Unit 5 (Interactive Features) → refreshes real-time views
```

### 2. Analytics Flow Pattern (Units 2,3 → Units 4,5)
```
Unit 2 (Portfolio Monitoring)
    ↓ publishes PortfolioAnalyticsUpdated
    ├→ Unit 4 (Core Analytics) → displays portfolio metrics
    └→ Unit 5 (Interactive Features) → enables filtering

Unit 3 (Risk Integration)
    ↓ publishes RiskScoresCalculated
    ├→ Unit 4 (Core Analytics) → shows risk visualizations
    └→ Unit 5 (Interactive Features) → enables risk-based filtering
```

### 3. User Interaction Pattern (Unit 4 ↔ Unit 5)
```
Unit 4 (Core Analytics)
    ↓ publishes DashboardViewAccessed
    └→ Unit 5 (Interactive Features) → initializes interactive state

Unit 5 (Interactive Features)
    ↓ publishes FilterApplied
    └→ Unit 4 (Core Analytics) → updates dashboard views
```

## Error Handling and Resilience

### Dead Letter Queues
- Failed message processing → Dead Letter Queue
- Manual intervention and replay capabilities
- Error analysis and pattern detection

### Circuit Breaker Pattern
- Prevent cascade failures between units
- Graceful degradation when dependencies unavailable
- Automatic recovery when services restore

### Retry Mechanisms
- Exponential backoff for transient failures
- Maximum retry limits to prevent infinite loops
- Idempotent message processing

## Shared Data Models

### Core Entities
```typescript
interface ESGHolding {
  holdingId: string;
  symbol: string;
  sector: string;
  esgScores: {
    environmental: number;
    social: number;
    governance: number;
    composite: number;
  };
  timestamp: string;
}

interface Portfolio {
  portfolioId: string;
  name: string;
  holdings: ESGHolding[];
  metrics: PortfolioMetrics;
  lastUpdated: string;
}

interface ThresholdAlert {
  alertId: string;
  portfolioId: string;
  severity: 'WARNING' | 'CRITICAL';
  component: 'environmental' | 'social' | 'governance' | 'composite';
  currentScore: number;
  thresholdValue: number;
  timestamp: string;
}
```

## Configuration Management

### Distributed Configuration
- **Unit 1**: File locations and processing parameters
- **Unit 2**: Threshold values and email settings
- **Unit 4**: System parameters and dashboard settings

### Configuration Synchronization
- Configuration changes published as events
- Version-controlled configuration updates
- Rollback capabilities for configuration errors

## Monitoring and Observability

### Message Queue Metrics
- Message throughput and latency
- Queue depth and consumer lag
- Error rates and dead letter queue size

### Cross-Unit Tracing
- Distributed tracing with correlation IDs
- End-to-end transaction monitoring
- Performance bottleneck identification

## Security Considerations

### Message Security
- Message encryption in transit
- Authentication for message producers/consumers
- Authorization for topic access

### Data Privacy
- PII masking in message payloads
- Audit logging for sensitive data access
- Compliance with data protection regulations

## Testing Strategy

### Integration Testing
- Contract testing between units
- End-to-end scenario testing
- Message schema validation

### Performance Testing
- Message throughput testing
- Latency measurement under load
- Scalability testing with multiple consumers