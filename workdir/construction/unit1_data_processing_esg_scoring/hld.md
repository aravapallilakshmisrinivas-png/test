# Unit 1: Data Processing & ESG Scoring - High Level Design

## Unit Overview
Unit 1 serves as the foundational data processing layer, responsible for ingesting CSV files, validating data quality, calculating ESG scores, and publishing processed data to downstream units via message queue.

## Strategic Components Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        UNIT 1: DATA PROCESSING & ESG SCORING                    │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐             │
│  │  File Monitor   │───▶│ CSV Ingestion   │───▶│ Data Validator  │             │
│  │   Component     │    │    Engine       │    │   Framework     │             │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘             │
│           │                       │                       │                    │
│           ▼                       ▼                       ▼                    │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐             │
│  │ Configuration   │    │ Data Parser     │    │ Quality Report  │             │
│  │   Manager       │    │   Component     │    │   Generator     │             │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘             │
│                                   │                       │                    │
│                                   ▼                       ▼                    │
│                          ┌─────────────────┐    ┌─────────────────┐             │
│                          │ ESG Calculation │    │ Error Handler   │             │
│                          │     Engine      │    │   Component     │             │
│                          └─────────────────┘    └─────────────────┘             │
│                                   │                       │                    │
│                                   ▼                       ▼                    │
│                          ┌─────────────────┐    ┌─────────────────┐             │
│                          │ Score Storage   │    │ Audit Logger    │             │
│                          │   Repository    │    │   Component     │             │
│                          └─────────────────┘    └─────────────────┘             │
│                                   │                                             │
│                                   ▼                                             │
│                          ┌─────────────────┐                                   │
│                          │ Database Writer │                                   │
│                          │   Component     │                                   │
│                          └─────────────────┘                                   │
│                                   │                                             │
│                                   ▼                                             │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │                        SHARED DATABASE                                    │   │
│  │  • esg_holdings                                                     │   │
│  │  • portfolios                                                 │   │
│  │  • processing_status                                              │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────────┘
```

## Strategic Components Specification

### 1. File Monitor Component
**Responsibility**: Monitors configured file locations for new CSV files
**Type**: Stateful (maintains file system watchers)
**Key Capabilities**:
- Watches multiple directory paths for new files
- Triggers processing pipeline when files are detected
- Handles file locking and concurrent access
- Maintains processing queue for batch operations

**Interfaces**:
- `FileDetectionService`: Core file monitoring logic
- `FileQueueManager`: Manages processing queue
- `FileAccessValidator`: Ensures file readability

### 2. CSV Ingestion Engine
**Responsibility**: Reads and parses CSV files into structured data
**Type**: Stateless (processes files on demand)
**Key Capabilities**:
- Supports multiple CSV formats and encodings
- Handles large files with streaming processing
- Validates CSV structure and required columns
- Extracts portfolio and holding information

**Interfaces**:
- `CSVReader`: File reading and parsing
- `DataExtractor`: Converts CSV rows to domain objects
- `FormatValidator`: Validates CSV structure

### 3. Data Validator Framework
**Responsibility**: Validates data quality and completeness
**Type**: Stateless (rule-based validation)
**Key Capabilities**:
- Applies configurable validation rules
- Validates data ranges and formats
- Identifies missing or suspicious data points
- Generates detailed validation reports

**Interfaces**:
- `ValidationRuleEngine`: Executes validation rules
- `DataQualityAnalyzer`: Analyzes data completeness
- `ValidationReportBuilder`: Creates quality reports

### 4. ESG Calculation Engine
**Responsibility**: Calculates composite ESG scores using fixed weightings
**Type**: Stateless (pure calculation logic)
**Key Capabilities**:
- Applies fixed weighting (40% E, 30% S, 30% G)
- Calculates individual and portfolio-level scores
- Normalizes scores to 0-100 scale
- Maintains calculation audit trail

**Interfaces**:
- `ScoreCalculator`: Core ESG calculation logic
- `WeightingManager`: Manages scoring weights
- `NormalizationService`: Score normalization
- `CalculationAuditor`: Tracks calculation history

### 5. Configuration Manager
**Responsibility**: Manages file locations and processing parameters
**Type**: Stateful (caches configuration)
**Key Capabilities**:
- Loads configuration from files and environment
- Provides hot-reload capability for configuration changes
- Validates configuration parameters
- Publishes configuration update events

**Interfaces**:
- `ConfigurationLoader`: Loads config from sources
- `ConfigurationValidator`: Validates config parameters
- `ConfigurationCache`: In-memory config caching

### 6. Score Storage Repository
**Responsibility**: Stores calculated ESG scores and metadata
**Type**: Stateful (maintains data store)
**Key Capabilities**:
- In-memory storage for MVP (HashMap-based)
- Stores scores with timestamps and metadata
- Provides query interface for score retrieval
- Maintains data retention policies

**Interfaces**:
- `ESGScoreRepository`: Score storage and retrieval
- `PortfolioRepository`: Portfolio data management
- `DataRetentionManager`: Manages data lifecycle

### 7. Database Writer Component
**Responsibility**: Writes processed ESG data to shared database
**Type**: Stateless (database operations)
**Key Capabilities**:
- Writes ESG scores to database tables
- Handles transaction management
- Implements retry logic for failed writes
- Maintains data consistency

**Interfaces**:
- `ESGDataWriter`: Core database writing
- `TransactionManager`: Database transaction handling
- `DatabaseClient`: Database connection management

### 8. Error Handler Component
**Responsibility**: Centralized error handling and recovery
**Type**: Stateful (maintains error state)
**Key Capabilities**:
- Classifies and handles different error types
- Implements retry mechanisms for transient failures
- Generates error reports and notifications
- Maintains error history and patterns

**Interfaces**:
- `ErrorClassifier`: Categorizes error types
- `RetryManager`: Handles retry logic
- `ErrorReporter`: Error notification and logging

## Component Interaction Flows

### 1. Happy Path Processing Flow
```
File Monitor → CSV Ingestion → Data Validator → ESG Calculation → Score Storage → Event Publisher
     ↓              ↓              ↓               ↓               ↓              ↓
Configuration  Data Parser   Quality Report   Calculation    Repository    Message Queue
  Manager                      Generator        Auditor
```

### 2. Error Handling Flow
```
Any Component → Error Handler → Error Classifier → Retry Manager → Audit Logger
                     ↓               ↓               ↓              ↓
               Error Reporter   Error Categories  Retry Logic   Error History
```

### 3. Configuration Update Flow
```
Configuration Manager → Configuration Validator → Configuration Cache → Event Publisher
         ↓                       ↓                      ↓                    ↓
   Config Loader           Validation Rules        Hot Reload         Config Updated Event
```

## Data Models and Contracts

### Core Domain Objects
```
ESGHolding:
- holdingId: String
- symbol: String
- sector: String
- environmentalScore: Double
- socialScore: Double
- governanceScore: Double
- compositeScore: Double
- timestamp: LocalDateTime

Portfolio:
- portfolioId: String
- name: String
- holdings: List<ESGHolding>
- aggregateScores: ESGScores
- lastUpdated: LocalDateTime

DataQualityReport:
- reportId: String
- fileName: String
- totalRecords: Integer
- validRecords: Integer
- errorRecords: Integer
- validationErrors: List<ValidationError>
- qualityScore: Double
```

### Event Contracts
```
ESGDataProcessed Event:
- eventId: UUID
- portfolioId: String
- holdings: List<ESGHolding>
- processingTimestamp: LocalDateTime
- qualityMetrics: DataQualityMetrics

DataValidationFailed Event:
- eventId: UUID
- fileName: String
- validationErrors: List<ValidationError>
- failedRecordCount: Integer
- timestamp: LocalDateTime
```

## Integration Points

### Inbound Dependencies
- **File System**: CSV file access
- **Configuration Files**: System parameters and file locations
- **Shared Database**: Configuration and status tables

### Outbound Dependencies
- **Shared Database**: ESG data and status updates
- **Logging System**: Audit and error logging
- **Monitoring System**: Health and performance metrics

## Performance Characteristics

### Throughput Requirements
- Process 1000 holdings within 2 minutes
- Support concurrent processing of multiple files
- Handle files up to 10MB in size

### Scalability Design
- Stateless components can be horizontally scaled
- File processing can be parallelized
- In-memory storage optimized for fast access

### Reliability Features
- Retry mechanisms for transient failures
- Graceful degradation for partial failures
- Comprehensive error logging and recovery

## Security Considerations

### Data Protection
- Input validation to prevent injection attacks
- Secure file access with permission validation
- Audit logging for all data processing activities

### Access Control
- Configuration access restricted to administrators
- File system access with minimal required permissions
- Event publishing with authenticated message queue access

## Monitoring and Observability

### Health Checks
- File system accessibility
- Message queue connectivity
- Configuration validity
- Component responsiveness

### Key Metrics
- Files processed per hour
- Processing success rate
- Data quality scores
- Error rates by category
- Processing latency percentiles

### Alerting
- Processing failures
- Data quality degradation
- Configuration errors
- Performance threshold breaches

This high-level design provides the strategic foundation for implementing Unit 1 with clear component boundaries, interaction patterns, and integration points while maintaining the flexibility for detailed implementation decisions.