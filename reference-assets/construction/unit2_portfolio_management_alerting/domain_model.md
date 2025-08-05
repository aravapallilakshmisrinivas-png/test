# Unit 2: Portfolio Management & Alerting System - Domain Model

## Domain Overview

### Bounded Context
**Portfolio Management & Alerting Context** - Responsible for portfolio-level ESG analysis, monitoring, and automated alerting functionality.

### Core Domain Responsibility
Transform individual holding ESG scores into portfolio insights and manage the notification system for ESG threshold breaches.

## Ubiquitous Language

### Core Concepts
- **Portfolio**: Collection of holdings with associated weights and ESG performance
- **Portfolio ESG Score**: Weighted average ESG score across portfolio holdings
- **ESG Rating Band**: Classification of holdings by ESG score ranges (Critical/Warning/Watch/Good)
- **Alert**: Notification triggered by threshold breach or significant ESG changes
- **Threshold Breach**: Event when ESG score crosses configured boundary values
- **Alert Configuration**: Settings defining when and how alerts are triggered
- **Notification**: Message sent to users about ESG events and changes
- **Portfolio Composition**: Breakdown of portfolio by ESG performance categories
- **ESG Performance Tracking**: Historical monitoring of portfolio ESG metrics

## Aggregates Design

### 1. Portfolio ESG Performance Aggregate
**Aggregate Root**: `PortfolioESGPerformance`
**Purpose**: Manages the complete ESG performance state and analysis for a portfolio
**Consistency Boundary**: All ESG metrics and performance data for a single portfolio

#### Aggregate Root: PortfolioESGPerformance
```
PortfolioESGPerformance (Entity)
├── PortfolioId (Value Object)
├── PortfolioName (Value Object)
├── CalculationDate (Value Object)
├── Holdings (Collection of Entities)
├── PortfolioESGScore (Entity)
├── CompositionAnalysis (Entity)
├── PerformanceHistory (Entity)
├── LastUpdated (Value Object)
└── PerformanceStatus (Value Object)
```

**Invariants**:
- Portfolio weights must sum to 100% (±1% tolerance)
- Portfolio ESG score must be calculated from constituent holdings
- Holdings cannot be removed if they have active alerts
- Performance history must be maintained for 12 months minimum

#### Child Entities within PortfolioESGPerformance:

##### PortfolioHolding (Entity)
```
PortfolioHolding
├── HoldingId (Value Object)
├── HoldingIdentifier (Value Object)
├── HoldingName (Value Object)
├── Weight (Value Object)
├── CurrentESGScore (Value Object)
├── ESGScoreGrade (Value Object)
├── PreviousESGScore (Value Object)
├── ScoreChange (Value Object)
├── LastUpdated (Value Object)
└── AlertStatus (Value Object)
```

##### PortfolioESGScore (Entity)
```
PortfolioESGScore
├── ScoreId (Value Object)
├── CompositeScore (Value Object) // Weighted average
├── EnvironmentalScore (Value Object)
├── SocialScore (Value Object)
├── GovernanceScore (Value Object)
├── CalculationMethod (Value Object)
├── CalculationDate (Value Object)
├── QualityIndicator (Value Object)
└── ScoreMetadata (Value Object)
```

##### CompositionAnalysis (Entity)
```
CompositionAnalysis
├── AnalysisId (Value Object)
├── CriticalHoldings (Value Object) // Count and percentage <30
├── WarningHoldings (Value Object) // Count and percentage 30-50
├── WatchHoldings (Value Object) // Count and percentage 50-70
├── GoodHoldings (Value Object) // Count and percentage >70
├── TopPerformers (Collection of Value Objects)
├── BottomPerformers (Collection of Value Objects)
├── CompositionTrends (Value Object)
└── AnalysisDate (Value Object)
```

##### PerformanceHistory (Entity)
```
PerformanceHistory
├── HistoryId (Value Object)
├── HistoricalScores (Collection of Value Objects) // 12 months
├── ScoreTrends (Value Object)
├── PerformanceMetrics (Value Object)
├── BenchmarkComparisons (Value Object)
├── VolatilityMeasures (Value Object)
└── LastUpdated (Value Object)
```

### 2. ESG Alert Management Aggregate
**Aggregate Root**: `ESGAlertManagement`
**Purpose**: Manages alert configurations, generation, and notification lifecycle
**Consistency Boundary**: All alert-related operations and state for the system

#### Aggregate Root: ESGAlertManagement
```
ESGAlertManagement (Entity)
├── AlertSystemId (Value Object)
├── AlertConfigurations (Collection of Entities)
├── ActiveAlerts (Collection of Entities)
├── AlertHistory (Entity)
├── NotificationSettings (Entity)
├── AlertMetrics (Entity)
└── LastConfigurationUpdate (Value Object)
```

**Invariants**:
- Alert thresholds must be in ascending order (Critical < Warning < Watch)
- Each alert must have valid notification settings
- Alert history must be maintained for audit purposes
- Active alerts cannot exceed system capacity limits

#### Child Entities within ESGAlertManagement:

##### AlertConfiguration (Entity)
```
AlertConfiguration
├── ConfigurationId (Value Object)
├── AlertType (Value Object) // Critical/Warning/Watch/Portfolio
├── ThresholdValue (Value Object)
├── AlertFrequency (Value Object) // Immediate/Daily/Weekly
├── NotificationMethod (Value Object) // Email
├── IsActive (Value Object)
├── Recipients (Collection of Value Objects)
├── MessageTemplate (Value Object)
└── LastModified (Value Object)
```

##### ESGAlert (Entity)
```
ESGAlert
├── AlertId (Value Object)
├── AlertType (Value Object)
├── TriggeredBy (Value Object) // HoldingId or PortfolioId
├── AlertMessage (Value Object)
├── CurrentESGScore (Value Object)
├── PreviousESGScore (Value Object)
├── ThresholdBreached (Value Object)
├── AlertSeverity (Value Object)
├── AlertStatus (Value Object) // Active/Acknowledged/Resolved
├── CreatedAt (Value Object)
├── NotificationsSent (Collection of Entities)
└── ResolutionNotes (Value Object)
```

##### AlertHistory (Entity)
```
AlertHistory
├── HistoryId (Value Object)
├── HistoricalAlerts (Collection of Value Objects)
├── AlertStatistics (Value Object)
├── AlertTrends (Value Object)
├── ResponseMetrics (Value Object)
├── ArchivalDate (Value Object)
└── RetentionPeriod (Value Object)
```

##### NotificationSettings (Entity)
```
NotificationSettings
├── SettingsId (Value Object)
├── EmailConfiguration (Value Object)
├── NotificationTemplates (Collection of Value Objects)
├── DeliverySettings (Value Object)
├── RetryConfiguration (Value Object)
├── EscalationRules (Value Object)
└── LastUpdated (Value Object)
```

## Entities and Value Objects

### Value Objects

#### PortfolioId
```
PortfolioId
├── Value (String)
├── PortfolioType (Enum)
└── CreatedDate (DateTime)
```

#### Weight
```
Weight
├── Percentage (Decimal 0-100)
├── AbsoluteValue (Decimal)
├── Currency (String)
└── LastUpdated (DateTime)
```

#### ESGScoreGrade
```
ESGScoreGrade
├── Grade (Enum: Critical, Warning, Watch, Good)
├── ScoreRange (Range)
├── ColorCode (String)
└── Description (String)
```

#### ScoreChange
```
ScoreChange
├── PreviousScore (Decimal)
├── CurrentScore (Decimal)
├── AbsoluteChange (Decimal)
├── PercentageChange (Decimal)
├── ChangeDirection (Enum: Improved, Declined, Unchanged)
├── ChangeSignificance (Enum: Minor, Moderate, Major)
└── ChangeDate (DateTime)
```

#### AlertSeverity
```
AlertSeverity
├── Level (Enum: Low, Medium, High, Critical)
├── Priority (Integer)
├── ResponseTimeRequired (TimeSpan)
└── EscalationRequired (Boolean)
```

#### ThresholdValue
```
ThresholdValue
├── Value (Decimal)
├── Operator (Enum: LessThan, GreaterThan, Equals)
├── ThresholdType (Enum: Absolute, Percentage, Change)
└── ValidFrom (DateTime)
```

#### AlertFrequency
```
AlertFrequency
├── Frequency (Enum: Immediate, Hourly, Daily, Weekly)
├── Schedule (String) // Cron expression
├── MaxAlertsPerPeriod (Integer)
└── QuietHours (TimeRange)
```

#### NotificationDelivery
```
NotificationDelivery
├── DeliveryId (String)
├── RecipientEmail (String)
├── DeliveryStatus (Enum: Pending, Sent, Delivered, Failed)
├── DeliveryAttempts (Integer)
├── SentAt (DateTime)
├── DeliveredAt (DateTime)
└── ErrorMessage (String)
```

#### PerformanceMetrics
```
PerformanceMetrics
├── AverageESGScore (Decimal)
├── ESGVolatility (Decimal)
├── ScoreImprovement (Decimal)
├── BenchmarkOutperformance (Decimal)
├── ConsistencyScore (Decimal)
└── TrendDirection (Enum)
```

## Domain Events

### Portfolio Performance Events

#### PortfolioESGScoreUpdated
```
PortfolioESGScoreUpdated
├── PortfolioId (String)
├── PreviousScore (Decimal)
├── NewScore (Decimal)
├── ScoreChange (Decimal)
├── ComponentChanges (Object)
├── UpdatedHoldings (List<String>)
├── CalculationDate (DateTime)
└── EventTimestamp (DateTime)
```

#### PortfolioCompositionChanged
```
PortfolioCompositionChanged
├── PortfolioId (String)
├── CompositionBefore (Object)
├── CompositionAfter (Object)
├── ChangedHoldings (List<String>)
├── ImpactOnESGScore (Decimal)
└── EventTimestamp (DateTime)
```

### Alert Events

#### ESGThresholdBreached
```
ESGThresholdBreached
├── AlertId (String)
├── PortfolioId (String)
├── HoldingId (String)
├── ThresholdType (String)
├── ThresholdValue (Decimal)
├── CurrentValue (Decimal)
├── BreachSeverity (String)
├── AlertMessage (String)
└── EventTimestamp (DateTime)
```

#### AlertGenerated
```
AlertGenerated
├── AlertId (String)
├── AlertType (String)
├── TriggeredBy (String)
├── Recipients (List<String>)
├── AlertSeverity (String)
├── NotificationMethod (String)
├── ScheduledDelivery (DateTime)
└── EventTimestamp (DateTime)
```

#### NotificationSent
```
NotificationSent
├── NotificationId (String)
├── AlertId (String)
├── RecipientEmail (String)
├── DeliveryMethod (String)
├── DeliveryStatus (String)
├── DeliveryAttempt (Integer)
└── EventTimestamp (DateTime)
```

## Domain Services

### PortfolioESGAnalysisService
**Purpose**: Orchestrates portfolio-level ESG analysis and calculations
```
PortfolioESGAnalysisService
├── CalculatePortfolioESGScore(holdings: List<PortfolioHolding>): PortfolioESGScore
├── AnalyzePortfolioComposition(portfolio: PortfolioESGPerformance): CompositionAnalysis
├── UpdatePerformanceHistory(portfolio: PortfolioESGPerformance, newScore: PortfolioESGScore): void
├── IdentifyPerformanceChanges(current: PortfolioESGScore, previous: PortfolioESGScore): List<ScoreChange>
└── GeneratePerformanceReport(portfolio: PortfolioESGPerformance): PerformanceReport
```

### AlertProcessingService
**Purpose**: Manages alert generation, processing, and notification workflow
```
AlertProcessingService
├── EvaluateAlertConditions(portfolio: PortfolioESGPerformance, configurations: List<AlertConfiguration>): List<ESGAlert>
├── GenerateAlert(condition: AlertCondition, configuration: AlertConfiguration): ESGAlert
├── ProcessAlertQueue(alerts: List<ESGAlert>): void
├── ScheduleNotifications(alert: ESGAlert): List<NotificationDelivery>
└── UpdateAlertStatus(alertId: String, status: AlertStatus): void
```

### NotificationService
**Purpose**: Handles notification delivery and tracking
```
NotificationService
├── SendEmailNotification(alert: ESGAlert, recipient: String): NotificationDelivery
├── FormatAlertMessage(alert: ESGAlert, template: MessageTemplate): String
├── TrackDeliveryStatus(deliveryId: String): DeliveryStatus
├── HandleDeliveryFailure(delivery: NotificationDelivery): void
└── GenerateDeliveryReport(period: TimePeriod): DeliveryReport
```

### ThresholdMonitoringService
**Purpose**: Continuously monitors ESG scores against configured thresholds
```
ThresholdMonitoringService
├── MonitorHoldingThresholds(holding: PortfolioHolding, thresholds: List<ThresholdValue>): List<ThresholdBreach>
├── MonitorPortfolioThresholds(portfolio: PortfolioESGPerformance, thresholds: List<ThresholdValue>): List<ThresholdBreach>
├── EvaluateThresholdBreach(currentValue: Decimal, threshold: ThresholdValue): Boolean
├── CalculateBreachSeverity(breach: ThresholdBreach): AlertSeverity
└── UpdateThresholdConfiguration(newThresholds: List<ThresholdValue>): void
```

## Repositories

### PortfolioESGPerformanceRepository
```
PortfolioESGPerformanceRepository
├── Save(portfolio: PortfolioESGPerformance): void
├── FindById(portfolioId: PortfolioId): PortfolioESGPerformance
├── FindByESGScoreRange(minScore: Decimal, maxScore: Decimal): List<PortfolioESGPerformance>
├── FindPortfoliosWithAlerts(): List<PortfolioESGPerformance>
├── GetPerformanceHistory(portfolioId: PortfolioId, period: TimePeriod): PerformanceHistory
└── UpdateHoldingESGScore(portfolioId: PortfolioId, holdingId: String, newScore: Decimal): void
```

### ESGAlertManagementRepository
```
ESGAlertManagementRepository
├── Save(alertManagement: ESGAlertManagement): void
├── FindActiveAlerts(): List<ESGAlert>
├── FindAlertsByPortfolio(portfolioId: PortfolioId): List<ESGAlert>
├── FindAlertsByStatus(status: AlertStatus): List<ESGAlert>
├── GetAlertHistory(period: TimePeriod): AlertHistory
├── SaveAlertConfiguration(configuration: AlertConfiguration): void
└── GetNotificationSettings(): NotificationSettings
```

## Domain Policies

### PortfolioAnalysisPolicy
```
PortfolioAnalysisPolicy
├── ValidatePortfolioWeights(holdings: List<PortfolioHolding>): PolicyResult
├── ValidateESGScoreCalculation(score: PortfolioESGScore, holdings: List<PortfolioHolding>): PolicyResult
├── RequirePerformanceHistoryUpdate(portfolio: PortfolioESGPerformance): PolicyResult
├── ValidateCompositionAnalysis(analysis: CompositionAnalysis): PolicyResult
└── EnforceDataRetentionPolicy(history: PerformanceHistory): PolicyResult
```

### AlertGenerationPolicy
```
AlertGenerationPolicy
├── ValidateThresholdConfiguration(thresholds: List<ThresholdValue>): PolicyResult
├── PreventDuplicateAlerts(newAlert: ESGAlert, existingAlerts: List<ESGAlert>): PolicyResult
├── EnforceAlertFrequencyLimits(alert: ESGAlert, recentAlerts: List<ESGAlert>): PolicyResult
├── ValidateAlertSeverity(alert: ESGAlert): PolicyResult
└── RequireAlertAcknowledgment(alert: ESGAlert): PolicyResult
```

### NotificationDeliveryPolicy
```
NotificationDeliveryPolicy
├── ValidateRecipientList(recipients: List<String>): PolicyResult
├── EnforceDeliveryRetryLimits(delivery: NotificationDelivery): PolicyResult
├── ValidateNotificationContent(message: String): PolicyResult
├── EnforceQuietHours(deliveryTime: DateTime): PolicyResult
└── RequireDeliveryConfirmation(delivery: NotificationDelivery): PolicyResult
```

## Anti-Corruption Layer Specifications

### Inbound from ESG Data Processing Context
**Transformations**:
- ESGScoreCalculated → PortfolioHoldingESGScore
- Map ESG score structure to portfolio holding format
- Extract relevant score components for portfolio analysis
- Add portfolio-specific metadata and classifications

### Inbound from Configuration Context
**Transformations**:
- AlertThresholdConfiguration → AlertConfiguration
- NotificationSettingsConfiguration → NotificationSettings
- Map external configuration to internal alert domain objects

### Outbound to Analytics Context
**Transformations**:
- PortfolioESGPerformance → PortfolioAnalyticsData
- ESGAlert → AlertAnalyticsData
- Include historical trends and performance metrics
- Add visualization-friendly metadata and aggregations
