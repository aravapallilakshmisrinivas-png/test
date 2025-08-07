# Unit 2: Portfolio Management & Alerting - Low Level Design

## Overview
This Low Level Design (LLD) provides detailed tactical specifications for implementing Unit 2: Portfolio Management & Alerting. It defines component interfaces, data models, algorithms, and implementation patterns based on the High Level Design.

## Design Decisions
- **Database Polling**: Scheduled polling with configurable intervals (default: 30 seconds)
- **Alert Deduplication**: Time-based window (15 minutes) with content hash comparison
- **Email Mock**: Simple in-memory mock with delivery simulation
- **Cache Invalidation**: Event-driven invalidation when new ESG data arrives
- **Configuration Format**: JSON for complex threshold structures
- **Analytics Calculation**: Real-time calculation triggered by data updates

## Detailed Data Models

### Core Domain Objects

```java
// ThresholdAlert - Alert entity for threshold breaches
class ThresholdAlert {
    String alertId;             // Unique alert identifier
    String portfolioId;         // Associated portfolio ID
    String holdingId;           // Holding ID (null for portfolio-level)
    AlertType alertType;        // INDIVIDUAL_HOLDING, PORTFOLIO_LEVEL
    ESGComponent component;     // ENVIRONMENTAL, SOCIAL, GOVERNANCE, COMPOSITE
    Double currentScore;        // Current ESG score
    Double thresholdValue;      // Configured threshold value
    Severity severity;          // WARNING, CRITICAL
    LocalDateTime timestamp;    // Alert generation time
    String description;         // Human-readable description
    AlertStatus status;         // ACTIVE, ACKNOWLEDGED, RESOLVED
    String contentHash;         // Hash for deduplication
    Map<String, Object> metadata; // Additional context
}

// PortfolioMetrics - Portfolio analytics data
class PortfolioMetrics {
    String portfolioId;         // Portfolio identifier
    String portfolioName;       // Portfolio name
    Integer totalHoldings;      // Total number of holdings
    Double averageESGScore;     // Portfolio average ESG score
    Double totalValue;          // Total portfolio market value
    ESGScores aggregateScores;  // Aggregate E, S, G scores
    Map<ScoreRange, Integer> scoreDistribution; // Score distribution
    Map<String, SectorMetrics> sectorBreakdown; // Sector analysis
    LocalDateTime lastUpdated;  // Last calculation time
    LocalDateTime dataAsOf;     // Data timestamp
}

// SectorMetrics - Sector-level analytics
class SectorMetrics {
    String sectorName;          // Sector identifier
    Integer holdingCount;       // Holdings in sector
    Double averageScore;        // Sector average ESG score
    Double totalValue;          // Sector total value
    Double minScore;            // Minimum ESG score
    Double maxScore;            // Maximum ESG score
    Double weightedScore;       // Value-weighted score
}

// NotificationPreferences - User notification settings
class NotificationPreferences {
    String userId;              // User identifier
    String emailAddress;        // Email address
    NotificationFrequency frequency; // IMMEDIATE, DAILY, WEEKLY
    Set<AlertType> alertTypes;  // Subscribed alert types
    Set<Severity> severityLevels; // Subscribed severity levels
    Boolean enabled;            // Notification enabled flag
    LocalDateTime lastUpdated;  // Preference update time
}

// ThresholdConfiguration - Threshold settings
class ThresholdConfiguration {
    String configId;            // Configuration identifier
    String portfolioId;         // Portfolio ID (null for global)
    ESGComponent component;     // E, S, G, or Composite
    Double warningThreshold;    // Warning level threshold
    Double criticalThreshold;   // Critical level threshold
    Boolean enabled;            // Threshold enabled flag
    LocalDateTime lastUpdated;  // Configuration update time
}

// EmailDeliveryRecord - Email tracking
class EmailDeliveryRecord {
    String deliveryId;          // Unique delivery ID
    String alertId;             // Associated alert ID
    String recipientEmail;      // Recipient email address
    String subject;             // Email subject
    String content;             // Email content
    DeliveryStatus status;      // PENDING, SENT, FAILED, BOUNCED
    LocalDateTime sentAt;       // Send timestamp
    Integer retryCount;         // Retry attempts
    String errorMessage;        // Error details if failed
}
```

## Component Detailed Specifications

### 1. Database Poller Component

**Tactical Implementation**:
```java
interface DatabasePollerComponent {
    void startPolling();
    void stopPolling();
    void setPollingInterval(Duration interval);
    List<ESGDataUpdate> pollForUpdates();
}

class DatabasePollerComponentImpl {
    private final ScheduledExecutorService scheduler;
    private final Duration pollingInterval = Duration.ofSeconds(30);
    private final ESGDataRepository esgRepository;
    private LocalDateTime lastPollTime;
    
    // Polls esg_holdings table for new/updated records
    // Tracks last poll timestamp to fetch incremental changes
    // Implements connection retry logic with exponential backoff
    // Routes data updates to threshold monitoring engine
}

class ESGDataUpdate {
    String portfolioId;
    List<ESGHolding> updatedHoldings;
    LocalDateTime updateTimestamp;
    UpdateType updateType; // INSERT, UPDATE, DELETE
}
```

**Key Algorithms**:
- **Incremental Polling**: Query records with lastUpdated > lastPollTime
- **Connection Management**: Connection pooling with health checks
- **Error Recovery**: Exponential backoff for failed polls (1s, 2s, 4s, max 30s)
- **Data Validation**: Validate ESG data before processing

### 2. Threshold Monitoring Engine

**Tactical Implementation**:
```java
interface ThresholdMonitoringEngine {
    List<ThresholdAlert> evaluateThresholds(List<ESGHolding> holdings);
    void updateThresholdConfiguration(ThresholdConfiguration config);
    ThresholdEvaluationResult evaluatePortfolio(Portfolio portfolio);
}

class ThresholdMonitoringEngineImpl {
    private final Map<String, ThresholdConfiguration> thresholdCache;
    private final ThresholdEvaluator evaluator;
    private final SeverityClassifier classifier;
    
    // Evaluates individual holdings and portfolio-level thresholds
    // Supports configurable thresholds for E, S, G, Composite scores
    // Implements severity classification (Warning: 10-20 points below, Critical: >20 points)
    // Caches threshold configurations for performance
}

class ThresholdEvaluationResult {
    List<ThresholdAlert> alerts;
    EvaluationMetrics metrics;
    LocalDateTime evaluationTime;
}
```

**Threshold Evaluation Algorithm**:
```
For each ESG component (E, S, G, Composite):
  If score < criticalThreshold:
    Generate CRITICAL alert
  Else if score < warningThreshold:
    Generate WARNING alert
    
Portfolio-level evaluation:
  Calculate weighted average score
  Apply same threshold logic to portfolio score
```

### 3. Alert Generator Component

**Tactical Implementation**:
```java
interface AlertGeneratorComponent {
    ThresholdAlert createAlert(ThresholdBreach breach);
    boolean isDuplicateAlert(ThresholdAlert alert);
    void updateAlertStatus(String alertId, AlertStatus status);
    List<ThresholdAlert> getActiveAlerts(String portfolioId);
}

class AlertGeneratorComponentImpl {
    private final ConcurrentHashMap<String, ThresholdAlert> activeAlerts;
    private final AlertDeduplicator deduplicator;
    private final AlertPriorityManager priorityManager;
    
    // Creates structured alert objects with context
    // Implements time-based deduplication (15-minute window)
    // Manages alert lifecycle and status transitions
    // Assigns priority based on severity and portfolio value
}

class AlertDeduplicator {
    private final Duration deduplicationWindow = Duration.ofMinutes(15);
    private final Map<String, LocalDateTime> recentAlerts;
    
    public boolean isDuplicate(ThresholdAlert alert) {
        String contentHash = calculateContentHash(alert);
        LocalDateTime lastSeen = recentAlerts.get(contentHash);
        return lastSeen != null && 
               Duration.between(lastSeen, LocalDateTime.now()).compareTo(deduplicationWindow) < 0;
    }
}
```

**Alert Priority Algorithm**:
```
Priority Score = (Severity Weight × 100) + (Portfolio Value Weight × 50) + (Score Deviation × 10)
Where:
- Severity Weight: CRITICAL = 1.0, WARNING = 0.5
- Portfolio Value Weight: Normalized portfolio value (0-1)
- Score Deviation: Absolute difference from threshold
```

### 4. Email Notification Service

**Tactical Implementation**:
```java
interface EmailNotificationService {
    void sendAlert(ThresholdAlert alert, List<String> recipients);
    DeliveryStatus checkDeliveryStatus(String deliveryId);
    void retryFailedDeliveries();
    List<EmailDeliveryRecord> getDeliveryHistory(String alertId);
}

class MockEmailNotificationService {
    private final ConcurrentHashMap<String, EmailDeliveryRecord> deliveryRecords;
    private final ScheduledExecutorService retryScheduler;
    private final Random deliverySimulator; // Simulates delivery success/failure
    
    // Simulates SMTP operations with configurable success rate (95%)
    // Implements retry logic with exponential backoff
    // Tracks delivery status and provides delivery confirmations
    // Supports batch email sending for multiple recipients
}

class EmailRetryConfiguration {
    int maxRetries = 3;
    Duration initialDelay = Duration.ofMinutes(1);
    double backoffMultiplier = 2.0;
    Duration maxDelay = Duration.ofMinutes(30);
    double simulatedSuccessRate = 0.95; // 95% success rate
}
```

**Email Delivery Algorithm**:
```
1. Validate recipient email addresses
2. Generate email content using template engine
3. Simulate SMTP delivery with configured success rate
4. Record delivery attempt with timestamp
5. Schedule retry for failed deliveries
6. Update delivery status and notify tracking system
```

### 5. Notification Template Engine

**Tactical Implementation**:
```java
interface NotificationTemplateEngine {
    String generateEmailContent(ThresholdAlert alert, TemplateType type);
    String generateSubject(ThresholdAlert alert);
    TemplateContext buildContext(ThresholdAlert alert);
}

class NotificationTemplateEngineImpl {
    private final Map<TemplateType, String> templates;
    private final TemplateProcessor processor;
    
    // Supports HTML and plain text templates
    // Dynamic content generation with alert context
    // Template caching for performance
    // Internationalization support for multiple languages
}

class TemplateContext {
    String portfolioName;
    String holdingSymbol;
    String companyName;
    Double currentScore;
    Double thresholdValue;
    String severity;
    String component;
    LocalDateTime alertTime;
    String actionRequired;
}
```

**Email Templates**:
```html
<!-- Critical Alert Template -->
<h2>🚨 Critical ESG Alert - {{portfolioName}}</h2>
<p><strong>{{companyName}} ({{holdingSymbol}})</strong></p>
<p>{{component}} Score: <span style="color:red">{{currentScore}}</span></p>
<p>Threshold: {{thresholdValue}}</p>
<p>Action Required: {{actionRequired}}</p>
<p>Alert Time: {{alertTime}}</p>

<!-- Warning Alert Template -->
<h2>⚠️ ESG Warning - {{portfolioName}}</h2>
<p><strong>{{companyName}} ({{holdingSymbol}})</strong></p>
<p>{{component}} Score: <span style="color:orange">{{currentScore}}</span></p>
<p>Threshold: {{thresholdValue}}</p>
<p>Alert Time: {{alertTime}}</p>
```

### 6. Portfolio Analytics Calculator

**Tactical Implementation**:
```java
interface PortfolioAnalyticsCalculator {
    PortfolioMetrics calculateMetrics(Portfolio portfolio);
    Map<String, SectorMetrics> calculateSectorBreakdown(List<ESGHolding> holdings);
    Map<ScoreRange, Integer> calculateScoreDistribution(List<ESGHolding> holdings);
    StatisticalSummary calculateStatistics(List<Double> scores);
}

class PortfolioAnalyticsCalculatorImpl {
    private final StatisticsCalculator statsCalculator;
    private final SectorAnalyzer sectorAnalyzer;
    
    // Real-time calculation triggered by data updates
    // Parallel processing for large portfolios
    // Caches intermediate calculations for performance
    // Supports weighted and unweighted aggregations
}

class StatisticalSummary {
    Double mean;
    Double median;
    Double standardDeviation;
    Double minimum;
    Double maximum;
    Integer count;
    Double sum;
}
```

**Portfolio Aggregation Algorithms**:
```
1. Value-Weighted Average:
   Portfolio Score = Σ(Holding Score × Market Value) / Total Portfolio Value

2. Equal-Weighted Average:
   Portfolio Score = Σ(Holding Score) / Number of Holdings

3. Score Distribution:
   Excellent (80-100): Count of holdings
   Good (60-79): Count of holdings
   Fair (40-59): Count of holdings
   Poor (0-39): Count of holdings

4. Sector Analysis:
   Group holdings by sector
   Calculate sector-level statistics
   Identify best/worst performing sectors
```

### 7. Threshold Configuration Manager

**Tactical Implementation**:
```java
interface ThresholdConfigurationManager {
    void loadConfiguration();
    void reloadConfiguration();
    ThresholdConfiguration getThreshold(String portfolioId, ESGComponent component);
    void updateThreshold(ThresholdConfiguration config);
    void addConfigurationListener(ConfigurationListener listener);
}

class ThresholdConfigurationManagerImpl {
    private final ConcurrentHashMap<String, ThresholdConfiguration> configCache;
    private final FileWatcher configFileWatcher;
    private final ConfigurationValidator validator;
    
    // JSON-based configuration with hot-reload capability
    // Validates threshold ranges (0-100) and logical consistency
    // Publishes configuration change events
    // Supports portfolio-specific and global thresholds
}
```

**Configuration Schema** (thresholds.json):
```json
{
  "globalThresholds": {
    "environmental": {
      "warningThreshold": 60.0,
      "criticalThreshold": 40.0,
      "enabled": true
    },
    "social": {
      "warningThreshold": 65.0,
      "criticalThreshold": 45.0,
      "enabled": true
    },
    "governance": {
      "warningThreshold": 70.0,
      "criticalThreshold": 50.0,
      "enabled": true
    },
    "composite": {
      "warningThreshold": 65.0,
      "criticalThreshold": 45.0,
      "enabled": true
    }
  },
  "portfolioSpecificThresholds": {
    "PORTFOLIO_001": {
      "environmental": {
        "warningThreshold": 70.0,
        "criticalThreshold": 50.0,
        "enabled": true
      }
    }
  }
}
```

### 8. Analytics Storage Repository

**Tactical Implementation**:
```java
interface AnalyticsStorageRepository {
    void saveMetrics(PortfolioMetrics metrics);
    Optional<PortfolioMetrics> getLatestMetrics(String portfolioId);
    List<PortfolioMetrics> getHistoricalMetrics(String portfolioId, LocalDateTime from, LocalDateTime to);
    void deleteOldMetrics(Duration retentionPeriod);
}

class InMemoryAnalyticsRepository {
    private final ConcurrentHashMap<String, PortfolioMetrics> currentMetrics;
    private final ConcurrentHashMap<String, List<PortfolioMetrics>> historicalMetrics;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    // Thread-safe in-memory storage with indexing
    // Automatic cleanup of old historical data
    // Query optimization for common access patterns
    // Memory management with configurable retention
}

class AnalyticsRetentionPolicy {
    Duration currentMetricsRetention = Duration.ofDays(30);
    Duration historicalMetricsRetention = Duration.ofDays(90);
    int maxHistoricalRecordsPerPortfolio = 1000;
}
```

### 9. Data Cache Manager

**Tactical Implementation**:
```java
interface DataCacheManager {
    void cacheESGData(String portfolioId, List<ESGHolding> holdings);
    Optional<List<ESGHolding>> getCachedData(String portfolioId);
    void invalidateCache(String portfolioId);
    void invalidateAllCache();
    CacheMetrics getCacheMetrics();
}

class InMemoryDataCacheManager {
    private final ConcurrentHashMap<String, CachedESGData> cache;
    private final CacheMetricsCollector metricsCollector;
    private final ScheduledExecutorService cleanupScheduler;
    
    // Event-driven cache invalidation
    // LRU eviction policy for memory management
    // Cache hit/miss metrics collection
    // Automatic cleanup of stale entries
}

class CachedESGData {
    List<ESGHolding> holdings;
    LocalDateTime cacheTime;
    LocalDateTime lastAccessed;
    int accessCount;
}

class CacheMetrics {
    long totalRequests;
    long cacheHits;
    long cacheMisses;
    double hitRatio;
    long evictionCount;
    long currentSize;
    long maxSize;
}
```

## Component Integration Patterns

### Threshold Monitoring Flow
```java
class ThresholdMonitoringOrchestrator {
    public void processESGDataUpdate(ESGDataUpdate update) {
        try {
            // 1. Cache updated ESG data
            cacheManager.cacheESGData(update.getPortfolioId(), update.getUpdatedHoldings());
            
            // 2. Evaluate thresholds
            List<ThresholdAlert> alerts = thresholdEngine.evaluateThresholds(update.getUpdatedHoldings());
            
            // 3. Generate and deduplicate alerts
            List<ThresholdAlert> newAlerts = alerts.stream()
                .filter(alert -> !alertGenerator.isDuplicateAlert(alert))
                .collect(Collectors.toList());
            
            // 4. Send email notifications
            for (ThresholdAlert alert : newAlerts) {
                List<String> recipients = getRecipients(alert.getPortfolioId());
                emailService.sendAlert(alert, recipients);
            }
            
            // 5. Calculate portfolio analytics
            Portfolio portfolio = buildPortfolio(update);
            PortfolioMetrics metrics = analyticsCalculator.calculateMetrics(portfolio);
            analyticsRepository.saveMetrics(metrics);
            
        } catch (Exception e) {
            errorHandler.handleProcessingError(e, update);
        }
    }
}
```

### Email Notification Flow
```java
class EmailNotificationOrchestrator {
    public void processAlertNotification(ThresholdAlert alert) {
        try {
            // 1. Get notification preferences
            List<NotificationPreferences> preferences = getNotificationPreferences(alert.getPortfolioId());
            
            // 2. Filter recipients based on preferences
            List<String> recipients = preferences.stream()
                .filter(pref -> pref.isEnabled() && pref.getAlertTypes().contains(alert.getAlertType()))
                .map(NotificationPreferences::getEmailAddress)
                .collect(Collectors.toList());
            
            // 3. Generate email content
            String subject = templateEngine.generateSubject(alert);
            String content = templateEngine.generateEmailContent(alert, TemplateType.HTML);
            
            // 4. Send emails with retry logic
            for (String recipient : recipients) {
                EmailDeliveryRecord record = emailService.sendEmail(recipient, subject, content);
                trackDelivery(record);
            }
            
        } catch (Exception e) {
            errorHandler.handleNotificationError(e, alert);
        }
    }
}
```

## Performance Specifications

### Memory Management
- **Cache Size**: 100MB maximum for ESG data cache
- **Analytics Retention**: 30 days current, 90 days historical
- **Alert History**: 1000 alerts per portfolio maximum
- **GC Strategy**: G1GC for low-latency processing

### Concurrency Configuration
- **Database Polling**: Single thread with 30-second intervals
- **Threshold Evaluation**: Parallel processing for large portfolios
- **Email Sending**: Thread pool of 5 threads
- **Analytics Calculation**: CPU cores × 1 threads

### Performance Targets
- **Threshold Evaluation**: < 30 seconds from data receipt
- **Email Delivery**: < 15 minutes from breach detection
- **Portfolio Analytics**: < 1 minute calculation time
- **Cache Hit Ratio**: > 80% for frequently accessed data
- **Alert Deduplication**: 99% accuracy in preventing duplicates

## Security Implementation

### Input Validation
```java
class ThresholdSecurityValidator {
    public void validateThresholdConfiguration(ThresholdConfiguration config) {
        // Validate threshold ranges (0-100)
        // Ensure warning > critical thresholds
        // Validate portfolio ID format
        // Check for injection patterns
    }
    
    public void validateEmailAddress(String email) {
        // RFC 5322 email validation
        // Domain validation
        // Blacklist checking
    }
}
```

### Audit Logging
```java
class MonitoringAuditLogger {
    public void logThresholdBreach(ThresholdAlert alert) {
        // Log all threshold breaches
        // Include portfolio, holding, and threshold details
        // Store in secure audit log
    }
    
    public void logEmailDelivery(EmailDeliveryRecord record) {
        // Log all email delivery attempts
        // Include recipient, status, and retry information
        // Maintain delivery audit trail
    }
}
```

## Testing Strategy

### Unit Test Coverage
- **Component Tests**: Each component with mock dependencies
- **Integration Tests**: End-to-end monitoring flows
- **Performance Tests**: Load testing with large portfolios
- **Email Tests**: Mock SMTP delivery scenarios

### Test Data Fixtures
```java
class MonitoringTestDataBuilder {
    public static List<ESGHolding> createHoldingsWithThresholdBreaches();
    public static ThresholdConfiguration createTestThresholds();
    public static NotificationPreferences createTestPreferences();
    public static Portfolio createLargeTestPortfolio(int holdingCount);
}
```

## Configuration Examples

### Development Configuration
```json
{
  "polling": {
    "intervalSeconds": 10,
    "retryAttempts": 3
  },
  "email": {
    "mockDeliverySuccessRate": 1.0,
    "deliveryDelayMs": 100
  },
  "cache": {
    "maxSizeMB": 50,
    "ttlMinutes": 30
  },
  "alerts": {
    "deduplicationWindowMinutes": 5,
    "maxAlertsPerPortfolio": 100
  }
}
```

### Production Configuration
```json
{
  "polling": {
    "intervalSeconds": 30,
    "retryAttempts": 5
  },
  "email": {
    "mockDeliverySuccessRate": 0.95,
    "deliveryDelayMs": 5000
  },
  "cache": {
    "maxSizeMB": 100,
    "ttlMinutes": 60
  },
  "alerts": {
    "deduplicationWindowMinutes": 15,
    "maxAlertsPerPortfolio": 1000
  }
}
```

## Deployment Guidelines

### System Requirements
- **Java**: OpenJDK 11 or higher
- **Memory**: 2GB RAM minimum for caching and analytics
- **Storage**: 5GB available disk space
- **Network**: Database connectivity for polling

### Startup Sequence
1. Load and validate threshold configuration
2. Initialize in-memory repositories and cache
3. Start database polling service
4. Verify email service connectivity
5. Begin threshold monitoring and analytics calculation

This Low Level Design provides comprehensive implementation guidance for all monitoring and alerting components while maintaining simplicity and focusing on the core portfolio management requirements.