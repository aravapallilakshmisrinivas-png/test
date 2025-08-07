# Unit 2: Portfolio Monitoring & Alerting - High Level Design

## Unit Overview
Unit 2 monitors portfolio ESG performance against configurable thresholds, calculates portfolio analytics, and provides email alerting capabilities to notify stakeholders of sustainability risks requiring attention.

## Strategic Components Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                   UNIT 2: PORTFOLIO MONITORING & ALERTING                       │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐             │
│  │ Database Poller │───▶│ Threshold       │───▶│ Alert Generator │             │
│  │   Component     │    │ Monitoring      │    │   Component     │             │
│  └─────────────────┘    │   Engine        │    └─────────────────┘             │
│           │              └─────────────────┘             │                      │
│           ▼                       │                      ▼                      │
│  ┌─────────────────┐              ▼             ┌─────────────────┐             │
│  │ Data Cache      │    ┌─────────────────┐    │ Email           │             │
│  │   Manager       │    │ Threshold       │    │ Notification    │             │
│  └─────────────────┘    │ Configuration   │    │   Service       │             │
│                          │   Manager       │    └─────────────────┘             │
│                          └─────────────────┘             │                      │
│                                   │                      ▼                      │
│                                   ▼             ┌─────────────────┐             │
│                          ┌─────────────────┐    │ Notification    │             │
│                          │ Portfolio       │    │ Template        │             │
│                          │ Analytics       │    │   Engine        │             │
│                          │ Calculator      │    └─────────────────┘             │
│                          └─────────────────┘                                    │
│                                   │                                             │
│                                   ▼                                             │
│                          ┌─────────────────┐                                   │
│                          │ Analytics       │                                   │
│                          │ Storage         │                                   │
│                          │ Repository      │                                   │
│                          └─────────────────┘                                   │
│                                   │                                             │
│                                   ▼                                             │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐             │
│  │ Error Handler   │    │ Database Writer │    │ Audit Logger    │             │
│  │   Component     │    │   Component     │    │   Component     │             │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘             │
│                                   │                                             │
│                                   ▼                                             │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │                        SHARED DATABASE                                    │   │
│  │  • threshold_alerts                                              │   │
│  │  • email_notifications                                                │   │
│  │  • portfolio_analytics                                            │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────────┘
```

## Strategic Components Specification

### 1. Database Poller Component
**Responsibility**: Polls database for new ESG data and configuration updates
**Type**: Stateful (maintains polling schedule)
**Key Capabilities**:
- Polls esg_holdings table for new ESG data
- Handles data validation and processing
- Implements polling intervals and retry logic
- Routes data to appropriate processing components

**Interfaces**:
- `MessageQueueSubscriber`: Queue subscription management
- `EventDeserializer`: Event data deserialization
- `EventRouter`: Routes events to processors

### 2. Threshold Monitoring Engine
**Responsibility**: Monitors ESG scores against configured thresholds
**Type**: Stateless (rule-based evaluation)
**Key Capabilities**:
- Evaluates individual holding and portfolio-level thresholds
- Supports different thresholds for E, S, G components
- Implements configurable severity levels (Warning, Critical)
- Triggers alerts when thresholds are breached

**Interfaces**:
- `ThresholdEvaluator`: Core threshold evaluation logic
- `SeverityClassifier`: Determines alert severity
- `BreachDetector`: Identifies threshold violations

### 3. Alert Generator Component
**Responsibility**: Generates and manages threshold breach alerts
**Type**: Stateless (alert creation logic)
**Key Capabilities**:
- Creates structured alert objects with context
- Implements alert deduplication logic
- Manages alert priority and routing
- Tracks alert lifecycle and status

**Interfaces**:
- `AlertBuilder`: Creates alert objects
- `AlertDeduplicator`: Prevents duplicate alerts
- `AlertPriorityManager`: Manages alert priorities

### 4. Email Notification Service
**Responsibility**: Sends email notifications for threshold breaches
**Type**: Stateful (maintains SMTP connections)
**Key Capabilities**:
- Sends formatted email alerts within 15-minute SLA
- Manages recipient lists and preferences
- Implements retry logic for failed deliveries
- Tracks delivery status and confirmations

**Interfaces**:
- `EmailSender`: Core email sending functionality
- `SMTPConnectionManager`: Manages SMTP connections
- `DeliveryTracker`: Tracks email delivery status

### 5. Notification Template Engine
**Responsibility**: Generates formatted notification content
**Type**: Stateless (template processing)
**Key Capabilities**:
- Creates email templates with dynamic content
- Supports multiple notification formats
- Includes portfolio details and ESG metrics
- Provides clear, actionable information

**Interfaces**:
- `TemplateProcessor`: Processes notification templates
- `ContentFormatter`: Formats notification content
- `ContextBuilder`: Builds template context data

### 6. Portfolio Analytics Calculator
**Responsibility**: Calculates portfolio-level ESG performance metrics
**Type**: Stateless (calculation logic)
**Key Capabilities**:
- Computes portfolio-level ESG score summaries
- Calculates distribution across ESG score ranges
- Provides sector-level performance breakdowns
- Generates basic statistics (average, min, max)

**Interfaces**:
- `PortfolioAggregator`: Aggregates holding-level data
- `StatisticsCalculator`: Computes statistical metrics
- `SectorAnalyzer`: Analyzes sector-level performance

### 7. Threshold Configuration Manager
**Responsibility**: Manages threshold values and configuration
**Type**: Stateful (caches configuration)
**Key Capabilities**:
- Loads threshold values from configuration files
- Validates threshold ranges (0-100)
- Provides hot-reload for configuration changes
- Publishes configuration update events

**Interfaces**:
- `ThresholdConfigLoader`: Loads threshold configuration
- `ConfigurationValidator`: Validates threshold values
- `ConfigurationCache`: In-memory config caching

### 8. Analytics Storage Repository
**Responsibility**: Stores portfolio analytics and metrics
**Type**: Stateful (maintains data store)
**Key Capabilities**:
- In-memory storage for calculated analytics
- Provides query interface for metrics retrieval
- Maintains historical analytics data
- Implements data retention policies

**Interfaces**:
- `AnalyticsRepository`: Analytics storage and retrieval
- `MetricsQueryService`: Query interface for analytics
- `HistoricalDataManager`: Manages historical data

### 9. Data Cache Manager
**Responsibility**: Caches frequently accessed ESG data
**Type**: Stateful (maintains cache)
**Key Capabilities**:
- Caches ESG scores for fast threshold evaluation
- Implements cache invalidation strategies
- Provides cache hit/miss metrics
- Optimizes memory usage with LRU eviction

**Interfaces**:
- `ESGDataCache`: Caches ESG score data
- `CacheInvalidator`: Manages cache invalidation
- `CacheMetricsCollector`: Collects cache performance metrics

## Component Interaction Flows

### 1. Threshold Monitoring Flow
```
Event Consumer → Data Cache Manager → Threshold Monitoring Engine → Alert Generator
      ↓                ↓                        ↓                        ↓
Message Queue    ESG Data Cache        Threshold Config         Alert Objects
                                           Manager
                                             ↓
                                    Email Notification Service
                                             ↓
                                    Notification Template Engine
```

### 2. Portfolio Analytics Flow
```
Event Consumer → Portfolio Analytics Calculator → Analytics Storage Repository → Event Publisher
      ↓                      ↓                           ↓                          ↓
ESG Data Events      Statistical Calculations      Analytics Storage        Analytics Updated Event
```

### 3. Configuration Update Flow
```
Threshold Configuration Manager → Configuration Validator → Configuration Cache → Event Publisher
            ↓                           ↓                      ↓                    ↓
    Config File Changes           Validation Rules        Hot Reload         Config Updated Event
```

## Data Models and Contracts

### Core Domain Objects
```
ThresholdAlert:
- alertId: String
- portfolioId: String
- holdingId: String (optional)
- alertType: AlertType (INDIVIDUAL_HOLDING, PORTFOLIO_LEVEL)
- component: ESGComponent (ENVIRONMENTAL, SOCIAL, GOVERNANCE, COMPOSITE)
- currentScore: Double
- thresholdValue: Double
- severity: Severity (WARNING, CRITICAL)
- timestamp: LocalDateTime

PortfolioMetrics:
- portfolioId: String
- totalHoldings: Integer
- averageESGScore: Double
- scoreDistribution: Map<ScoreRange, Integer>
- sectorBreakdown: Map<String, SectorMetrics>
- lastUpdated: LocalDateTime

NotificationPreferences:
- userId: String
- emailAddress: String
- notificationFrequency: Frequency
- alertTypes: Set<AlertType>
- enabled: Boolean
```

### Event Contracts
```
ThresholdBreachDetected Event:
- eventId: UUID
- portfolioId: String
- alertDetails: ThresholdAlert
- severity: Severity
- timestamp: LocalDateTime

PortfolioAnalyticsUpdated Event:
- eventId: UUID
- portfolioId: String
- metrics: PortfolioMetrics
- updateTimestamp: LocalDateTime
```

## Integration Points

### Inbound Dependencies
- **Unit 1**: ESGDataProcessed events via message queue
- **Configuration Files**: Threshold values and email settings
- **SMTP Server**: Email delivery infrastructure

### Outbound Dependencies
- **Message Queue**: Event publishing to downstream units
- **Email Infrastructure**: SMTP server for notifications
- **Logging System**: Audit and error logging

## Performance Characteristics

### Response Time Requirements
- Threshold evaluation within 30 seconds of receiving ESG data
- Email notifications sent within 15 minutes of breach detection
- Portfolio analytics calculated within 1 minute

### Scalability Design
- Stateless components can be horizontally scaled
- Cache manager optimizes data access performance
- Event-driven architecture supports high throughput

### Reliability Features
- Retry mechanisms for failed email deliveries
- Alert deduplication to prevent spam
- Comprehensive error handling and recovery

## Security Considerations

### Data Protection
- Secure handling of portfolio and holding data
- Encrypted email communications
- Audit logging for all monitoring activities

### Access Control
- Configuration access restricted to administrators
- Email preferences managed per user
- Secure SMTP authentication

## Monitoring and Observability

### Health Checks
- Message queue connectivity
- SMTP server availability
- Configuration validity
- Cache performance

### Key Metrics
- Threshold evaluations per minute
- Alert generation rate
- Email delivery success rate
- Portfolio analytics calculation time
- Cache hit/miss ratios

### Alerting
- Failed threshold evaluations
- Email delivery failures
- Configuration errors
- Performance degradation

This high-level design establishes Unit 2 as a robust monitoring and alerting system with clear component boundaries and efficient data processing capabilities.