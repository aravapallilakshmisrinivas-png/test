# Unit 1: Data Processing & ESG Scoring - Low Level Design

## Overview
This Low Level Design (LLD) provides detailed tactical specifications for implementing Unit 1: Data Processing & ESG Scoring. It defines component interfaces, data models, algorithms, and implementation patterns based on the High Level Design.

## Design Decisions
- **In-Memory Storage**: ConcurrentHashMap for thread safety and simplicity
- **File Processing**: Parallel processing with configurable thread pools
- **Database Mock**: Pure in-memory HashMap structures for simplicity
- **Configuration Hot-Reload**: Only affects new files, not queued files
- **Error Recovery**: Automatic retry with configurable limits

## Detailed Data Models

### Core Domain Objects

```java
// ESGHolding - Core holding entity
class ESGHolding {
    String holdingId;           // Unique identifier
    String symbol;              // Stock symbol
    String companyName;         // Company name
    String sector;              // Industry sector
    Double environmentalScore;  // E score (0-100)
    Double socialScore;         // S score (0-100)
    Double governanceScore;     // G score (0-100)
    Double compositeScore;      // Weighted composite (0-100)
    Double marketValue;         // Market value in currency
    LocalDateTime lastUpdated;  // Last update timestamp
    String dataSource;          // Source of ESG data
    Map<String, Object> metadata; // Additional attributes
}

// Portfolio - Portfolio entity
class Portfolio {
    String portfolioId;         // Unique identifier
    String name;                // Portfolio name
    String description;         // Portfolio description
    List<ESGHolding> holdings;  // List of holdings
    ESGScores aggregateScores;  // Portfolio-level scores
    Double totalValue;          // Total portfolio value
    LocalDateTime lastUpdated;  // Last update timestamp
    String status;              // ACTIVE, INACTIVE, PROCESSING
}

// ESGScores - Score aggregation
class ESGScores {
    Double environmentalScore;  // Weighted E score
    Double socialScore;         // Weighted S score
    Double governanceScore;     // Weighted G score
    Double compositeScore;      // Overall composite score
    Integer holdingCount;       // Number of holdings
    Double coveragePercentage;  // Data coverage %
}

// DataQualityReport - Validation results
class DataQualityReport {
    String reportId;            // Unique report ID
    String fileName;            // Source file name
    String portfolioId;         // Associated portfolio
    Integer totalRecords;       // Total records processed
    Integer validRecords;       // Valid records count
    Integer errorRecords;       // Error records count
    List<ValidationError> errors; // Detailed errors
    Double qualityScore;        // Overall quality (0-100)
    LocalDateTime generatedAt;  // Report generation time
    ProcessingMetrics metrics;  // Performance metrics
}

// ValidationError - Error details
class ValidationError {
    String errorId;             // Unique error ID
    String recordId;            // Record identifier
    String fieldName;           // Field with error
    String errorType;           // MISSING, INVALID, OUT_OF_RANGE
    String errorMessage;        // Human-readable message
    String actualValue;         // Actual field value
    String expectedFormat;      // Expected format/range
    String severity;            // ERROR, WARNING, INFO
}

// ProcessingMetrics - Performance tracking
class ProcessingMetrics {
    Long processingTimeMs;      // Total processing time
    Long recordsPerSecond;      // Processing throughput
    Long memoryUsedMB;          // Memory consumption
    Integer threadsUsed;        // Parallel threads used
    Map<String, Long> phaseTimings; // Time per phase
}
```

## Component Detailed Specifications

### 1. File Monitor Component

**Tactical Implementation**:
```java
interface FileMonitorComponent {
    void startMonitoring();
    void stopMonitoring();
    void addWatchPath(String path);
    List<FileEvent> getQueuedFiles();
}

class FileMonitorComponentImpl {
    // Uses WatchService for file system monitoring
    // Maintains ConcurrentLinkedQueue for file events
    // Configurable polling interval (default: 5 seconds)
    // Thread pool for parallel directory scanning
}

class FileEvent {
    String filePath;
    String fileName;
    FileEventType eventType; // CREATED, MODIFIED, DELETED
    LocalDateTime timestamp;
    Long fileSize;
    String checksum; // MD5 hash for duplicate detection
}
```

**Key Algorithms**:
- **File Detection**: WatchService-based monitoring with fallback polling
- **Duplicate Prevention**: MD5 checksum comparison
- **Queue Management**: Priority queue based on file timestamp
- **Concurrent Access**: File locking using FileLock

### 2. CSV Ingestion Engine

**Tactical Implementation**:
```java
interface CSVIngestionEngine {
    IngestionResult ingestFile(String filePath);
    List<ESGHolding> parseCSV(InputStream inputStream);
    ValidationResult validateFormat(String filePath);
}

class CSVIngestionEngineImpl {
    // Uses OpenCSV library for parsing
    // Streaming approach for large files
    // Configurable batch size (default: 1000 records)
    // Memory-mapped files for large file handling
}

class IngestionResult {
    List<ESGHolding> holdings;
    List<String> warnings;
    ProcessingMetrics metrics;
    boolean success;
}
```

**Key Algorithms**:
- **Streaming Parser**: Process CSV in configurable batches
- **Memory Management**: Release processed batches immediately
- **Format Detection**: Auto-detect delimiter, encoding, headers
- **Error Recovery**: Skip malformed rows, continue processing

**Expected CSV Format**:
```csv
HoldingId,Symbol,CompanyName,Sector,EnvironmentalScore,SocialScore,GovernanceScore,MarketValue
H001,AAPL,Apple Inc,Technology,85.5,78.2,92.1,150000000
H002,MSFT,Microsoft Corp,Technology,88.7,85.3,89.4,200000000
```

### 3. Data Validator Framework

**Tactical Implementation**:
```java
interface DataValidatorFramework {
    ValidationResult validate(List<ESGHolding> holdings);
    void addValidationRule(ValidationRule rule);
    DataQualityReport generateReport(ValidationResult result);
}

class ValidationRule {
    String ruleName;
    String fieldName;
    Predicate<Object> validator;
    String errorMessage;
    ValidationSeverity severity;
}
```

**Validation Rules**:
1. **Required Fields**: holdingId, symbol, scores not null
2. **Score Ranges**: ESG scores between 0-100
3. **Data Types**: Numeric fields are valid numbers
4. **Business Rules**: Market value > 0, valid sector codes
5. **Referential Integrity**: Unique holding IDs within portfolio

**Quality Scoring Algorithm**:
```
Quality Score = (Valid Records / Total Records) × 100
Weighted by severity: ERROR (-10), WARNING (-5), INFO (-1)
```

### 4. ESG Calculation Engine

**Tactical Implementation**:
```java
interface ESGCalculationEngine {
    ESGScores calculateComposite(ESGHolding holding);
    ESGScores calculatePortfolioScores(Portfolio portfolio);
    CalculationAudit getAuditTrail(String calculationId);
}

class WeightingConfiguration {
    static final double ENVIRONMENTAL_WEIGHT = 0.40;
    static final double SOCIAL_WEIGHT = 0.30;
    static final double GOVERNANCE_WEIGHT = 0.30;
}
```

**Core Calculation Algorithms**:

1. **Composite Score Calculation**:
```
Composite Score = (E × 0.40) + (S × 0.30) + (G × 0.30)
Where E, S, G are normalized scores (0-100)
```

2. **Portfolio Aggregation**:
```
Portfolio Score = Σ(Holding Score × Market Value Weight)
Market Value Weight = Holding Market Value / Total Portfolio Value
```

3. **Normalization**:
```
Normalized Score = ((Raw Score - Min Score) / (Max Score - Min Score)) × 100
```

### 5. Configuration Manager

**Tactical Implementation**:
```java
interface ConfigurationManager {
    <T> T getProperty(String key, Class<T> type);
    void reloadConfiguration();
    void addConfigurationListener(ConfigurationListener listener);
}

class ApplicationConfiguration {
    // File monitoring settings
    List<String> watchPaths;
    int pollingIntervalSeconds;
    
    // Processing settings
    int threadPoolSize;
    int batchSize;
    boolean parallelProcessing;
    
    // Validation settings
    Map<String, ValidationRule> validationRules;
    double qualityThreshold;
    
    // ESG calculation settings
    WeightingConfiguration weights;
    boolean enableAuditTrail;
}
```

**Configuration Schema** (config.properties):
```properties
# File Monitoring
file.watch.paths=/data/input,/data/staging
file.polling.interval.seconds=5
file.max.size.mb=100

# Processing
processing.thread.pool.size=4
processing.batch.size=1000
processing.parallel.enabled=true

# Validation
validation.quality.threshold=0.85
validation.skip.on.error=false

# ESG Calculation
esg.weights.environmental=0.40
esg.weights.social=0.30
esg.weights.governance=0.30
```

### 6. In-Memory Repository Structure

**Tactical Implementation**:
```java
interface ESGScoreRepository {
    void save(ESGHolding holding);
    Optional<ESGHolding> findById(String holdingId);
    List<ESGHolding> findByPortfolio(String portfolioId);
    void deleteByPortfolio(String portfolioId);
}

class InMemoryESGScoreRepository {
    private final ConcurrentHashMap<String, ESGHolding> holdingsStore;
    private final ConcurrentHashMap<String, Set<String>> portfolioIndex;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
}

interface PortfolioRepository {
    void save(Portfolio portfolio);
    Optional<Portfolio> findById(String portfolioId);
    List<Portfolio> findAll();
    void delete(String portfolioId);
}
```

**Storage Strategy**:
- **Primary Storage**: ConcurrentHashMap<String, Entity>
- **Indexing**: Secondary maps for common queries
- **Thread Safety**: ConcurrentHashMap + ReadWriteLock for complex operations
- **Memory Management**: LRU eviction for large datasets

### 7. Database Writer Component

**Tactical Implementation**:
```java
interface DatabaseWriterComponent {
    void writeESGData(List<ESGHolding> holdings);
    void writePortfolioData(Portfolio portfolio);
    void updateProcessingStatus(String portfolioId, ProcessingStatus status);
}

class MockDatabaseClient {
    private final ConcurrentHashMap<String, Object> mockTables;
    private final AtomicLong transactionCounter;
    
    // Simulates database operations with configurable delays
    // Implements retry logic with exponential backoff
    // Tracks operation metrics and success rates
}
```

**Mock Database Schema**:
```sql
-- ESG Holdings Table
esg_holdings: {
    holding_id: String,
    portfolio_id: String,
    symbol: String,
    company_name: String,
    sector: String,
    environmental_score: Double,
    social_score: Double,
    governance_score: Double,
    composite_score: Double,
    market_value: Double,
    last_updated: Timestamp
}

-- Portfolio Analytics Table
portfolio_analytics: {
    portfolio_id: String,
    portfolio_name: String,
    total_holdings: Integer,
    total_value: Double,
    avg_esg_score: Double,
    last_calculated: Timestamp
}

-- Processing Status Table
processing_status: {
    job_id: String,
    portfolio_id: String,
    status: String,
    records_processed: Integer,
    errors_count: Integer,
    started_at: Timestamp,
    completed_at: Timestamp
}
```

### 8. Error Handling Framework

**Tactical Implementation**:
```java
interface ErrorHandlerComponent {
    void handleError(ProcessingError error);
    List<ProcessingError> getErrorHistory(String portfolioId);
    RetryResult retryFailedOperation(String operationId);
}

class ErrorClassification {
    TRANSIENT_ERROR,    // Network, temporary file locks
    DATA_ERROR,         // Invalid data format, missing fields
    SYSTEM_ERROR,       // Out of memory, disk space
    CONFIGURATION_ERROR // Invalid config, missing files
}

class RetryConfiguration {
    int maxRetries = 3;
    long initialDelayMs = 1000;
    double backoffMultiplier = 2.0;
    long maxDelayMs = 30000;
}
```

**Error Recovery Strategies**:
1. **Transient Errors**: Exponential backoff retry (max 3 attempts)
2. **Data Errors**: Skip record, log error, continue processing
3. **System Errors**: Fail fast, alert administrators
4. **Configuration Errors**: Fail startup, require manual fix

## Component Integration Patterns

### Processing Orchestrator Flow
```java
class ProcessingOrchestrator {
    public ProcessingResult processFile(String filePath) {
        try {
            // 1. Validate file access
            fileMonitor.validateFileAccess(filePath);
            
            // 2. Ingest CSV data
            IngestionResult ingestion = csvEngine.ingestFile(filePath);
            
            // 3. Validate data quality
            ValidationResult validation = validator.validate(ingestion.getHoldings());
            
            // 4. Calculate ESG scores
            List<ESGHolding> scoredHoldings = calculator.calculateScores(ingestion.getHoldings());
            
            // 5. Store results
            repository.saveAll(scoredHoldings);
            
            // 6. Write to database
            databaseWriter.writeESGData(scoredHoldings);
            
            // 7. Generate reports
            return buildSuccessResult(scoredHoldings, validation);
            
        } catch (Exception e) {
            return errorHandler.handleProcessingError(e, filePath);
        }
    }
}
```

### Parallel Processing Strategy
```java
class ParallelProcessingManager {
    private final ExecutorService threadPool;
    private final int batchSize;
    
    public List<ESGHolding> processHoldingsParallel(List<ESGHolding> holdings) {
        List<List<ESGHolding>> batches = partition(holdings, batchSize);
        
        List<CompletableFuture<List<ESGHolding>>> futures = batches.stream()
            .map(batch -> CompletableFuture.supplyAsync(() -> processBatch(batch), threadPool))
            .collect(Collectors.toList());
            
        return futures.stream()
            .map(CompletableFuture::join)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }
}
```

## Performance Specifications

### Memory Management
- **Heap Size**: 2GB minimum, 8GB recommended
- **Batch Processing**: 1000 records per batch
- **Memory Cleanup**: Explicit cleanup after each batch
- **GC Strategy**: G1GC for low-latency processing

### Concurrency Configuration
- **Thread Pool Size**: CPU cores × 2 (default: 8)
- **File Processing**: 1 file per thread maximum
- **Database Connections**: 10 connection pool size
- **Queue Capacity**: 1000 pending files maximum

### Performance Targets
- **Throughput**: 10,000 holdings per minute
- **Latency**: < 2 seconds for 1000 holdings
- **Memory Usage**: < 1GB for 100,000 holdings
- **Error Rate**: < 1% for valid input data

## Security Implementation

### Input Validation
```java
class SecurityValidator {
    public void validateFilePath(String path) {
        // Prevent directory traversal attacks
        // Validate file extensions (.csv only)
        // Check file size limits
        // Verify file permissions
    }
    
    public void sanitizeInput(String input) {
        // Remove SQL injection patterns
        // Escape special characters
        // Validate against whitelist
    }
}
```

### Audit Logging
```java
class AuditLogger {
    public void logFileAccess(String filePath, String userId) {
        // Log all file access attempts
        // Include timestamp, user, action, result
        // Store in secure audit log
    }
    
    public void logDataModification(String portfolioId, String operation) {
        // Log all data changes
        // Include before/after values
        // Maintain immutable audit trail
    }
}
```

## Testing Strategy

### Unit Test Coverage
- **Component Tests**: Each component with mock dependencies
- **Integration Tests**: End-to-end processing flows
- **Performance Tests**: Load testing with large datasets
- **Error Scenario Tests**: All error conditions and recovery

### Test Data Fixtures
```java
class TestDataBuilder {
    public static List<ESGHolding> createValidHoldings(int count);
    public static List<ESGHolding> createInvalidHoldings();
    public static String createCSVFile(List<ESGHolding> holdings);
    public static Portfolio createTestPortfolio(String id);
}
```

## Configuration Examples

### Development Configuration
```properties
# Development settings
file.watch.paths=./test-data/input
processing.thread.pool.size=2
processing.batch.size=100
validation.quality.threshold=0.70
logging.level=DEBUG
```

### Production Configuration
```properties
# Production settings
file.watch.paths=/data/esg/input,/data/esg/staging
processing.thread.pool.size=8
processing.batch.size=1000
validation.quality.threshold=0.90
logging.level=INFO
database.connection.pool.size=20
```

## Deployment Guidelines

### System Requirements
- **Java**: OpenJDK 11 or higher
- **Memory**: 4GB RAM minimum
- **Storage**: 10GB available disk space
- **Network**: Low-latency database connectivity

### Startup Sequence
1. Load and validate configuration
2. Initialize in-memory repositories
3. Start file monitoring services
4. Verify database connectivity
5. Begin processing queued files

This Low Level Design provides comprehensive implementation guidance for all components while maintaining simplicity and focusing on the core ESG data processing requirements.