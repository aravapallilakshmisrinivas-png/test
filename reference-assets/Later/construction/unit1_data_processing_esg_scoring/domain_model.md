# Unit 1: Data Processing & ESG Scoring Engine - Domain Model

## Domain Overview

### Bounded Context
**ESG Data Processing & Scoring Context** - Responsible for the complete data pipeline from raw CSV ingestion through to final composite ESG score generation.

### Core Domain Responsibility
Transform raw ESG data into validated, normalized, and scored ESG metrics that serve as the foundation for all other platform capabilities.

## Ubiquitous Language

### Core Concepts
- **Raw ESG Data**: Unprocessed ESG metrics from CSV files
- **Data Validation**: Process of checking data completeness and accuracy
- **Data Normalization**: Standardization of data to consistent scales (0-100)
- **Environmental Score**: ESG component representing environmental impact (40% weight)
- **Social Score**: ESG component representing social responsibility (30% weight)
- **Governance Score**: ESG component representing governance quality (30% weight)
- **Composite ESG Score**: Final aggregated ESG score combining all components
- **ESG Methodology**: Rules and weightings for calculating ESG scores
- **Data Quality Report**: Summary of data validation results and issues

## Aggregates Design

### 1. ESG Data Processing Aggregate
**Aggregate Root**: `ESGDataBatch`
**Purpose**: Manages the complete lifecycle of a data processing batch from ingestion to final scoring
**Consistency Boundary**: All operations on a single data batch must be consistent

#### Aggregate Root: ESGDataBatch
```
ESGDataBatch (Entity)
├── BatchId (Value Object)
├── ProcessingStatus (Value Object)
├── SourceFile (Value Object)
├── ProcessingTimestamp (Value Object)
├── RawDataRecords (Collection of Entities)
├── ValidationResults (Value Object)
├── ProcessingMetrics (Value Object)
└── DataQualityReport (Entity)
```

**Invariants**:
- A batch cannot be marked as completed until all records are processed
- Invalid records must be flagged but not prevent batch completion
- Processing status must follow valid state transitions
- Data quality metrics must be calculated for every batch

#### Child Entities within ESGDataBatch:

##### RawDataRecord (Entity)
```
RawDataRecord
├── RecordId (Value Object)
├── HoldingIdentifier (Value Object)
├── RawESGMetrics (Value Object)
├── ValidationStatus (Value Object)
├── ValidationErrors (Collection of Value Objects)
└── ProcessingTimestamp (Value Object)
```

##### DataQualityReport (Entity)
```
DataQualityReport
├── ReportId (Value Object)
├── BatchId (Value Object)
├── TotalRecords (Value Object)
├── ValidRecords (Value Object)
├── InvalidRecords (Value Object)
├── QualityMetrics (Value Object)
├── ValidationSummary (Value Object)
└── GeneratedAt (Value Object)
```

### 2. ESG Score Calculation Aggregate
**Aggregate Root**: `ESGScoreCalculation`
**Purpose**: Manages the calculation and lifecycle of ESG scores for a specific holding
**Consistency Boundary**: All ESG score components for a holding must be calculated consistently

#### Aggregate Root: ESGScoreCalculation
```
ESGScoreCalculation (Entity)
├── CalculationId (Value Object)
├── HoldingIdentifier (Value Object)
├── CalculationDate (Value Object)
├── NormalizedESGData (Value Object)
├── EnvironmentalScore (Entity)
├── SocialScore (Entity)
├── GovernanceScore (Entity)
├── CompositeESGScore (Entity)
├── MethodologyVersion (Value Object)
└── CalculationStatus (Value Object)
```

**Invariants**:
- Component scores must sum to composite score using defined weightings (40/30/30)
- All scores must be in 0-100 range
- Calculation cannot be completed without all required input data
- Methodology version must be tracked for auditability

#### Child Entities within ESGScoreCalculation:

##### EnvironmentalScore (Entity)
```
EnvironmentalScore
├── ScoreId (Value Object)
├── CarbonEmissionsScore (Value Object) // 15% weight
├── EnergyEfficiencyScore (Value Object) // 10% weight
├── WaterWasteScore (Value Object) // 8% weight
├── BiodiversityScore (Value Object) // 7% weight
├── ComponentWeightings (Value Object)
├── TotalEnvironmentalScore (Value Object) // 40% of total ESG
└── CalculationMetadata (Value Object)
```

##### SocialScore (Entity)
```
SocialScore
├── ScoreId (Value Object)
├── DiversityInclusionScore (Value Object) // 10% weight
├── HealthSafetyScore (Value Object) // 8% weight
├── CommunityImpactScore (Value Object) // 7% weight
├── SupplyChainScore (Value Object) // 5% weight
├── ComponentWeightings (Value Object)
├── TotalSocialScore (Value Object) // 30% of total ESG
└── CalculationMetadata (Value Object)
```

##### GovernanceScore (Entity)
```
GovernanceScore
├── ScoreId (Value Object)
├── BoardCompositionScore (Value Object) // 12% weight
├── ExecutiveCompensationScore (Value Object) // 8% weight
├── AntiCorruptionScore (Value Object) // 5% weight
├── TransparencyScore (Value Object) // 5% weight
├── ComponentWeightings (Value Object)
├── TotalGovernanceScore (Value Object) // 30% of total ESG
└── CalculationMetadata (Value Object)
```

##### CompositeESGScore (Entity)
```
CompositeESGScore
├── ScoreId (Value Object)
├── EnvironmentalContribution (Value Object) // 40%
├── SocialContribution (Value Object) // 30%
├── GovernanceContribution (Value Object) // 30%
├── FinalESGScore (Value Object) // 0-100 scale
├── ScoreGrade (Value Object) // Critical/Warning/Watch/Good
├── CalculationTimestamp (Value Object)
└── QualityIndicators (Value Object)
```

## Entities and Value Objects

### Value Objects

#### BatchId
```
BatchId
├── Value (String)
├── GeneratedAt (DateTime)
└── Format (UUID)
```

#### ProcessingStatus
```
ProcessingStatus
├── Status (Enum: Pending, InProgress, Completed, Failed)
├── StatusMessage (String)
└── LastUpdated (DateTime)
```

#### SourceFile
```
SourceFile
├── FilePath (String)
├── FileName (String)
├── FileSize (Long)
├── FileHash (String)
└── LastModified (DateTime)
```

#### HoldingIdentifier
```
HoldingIdentifier
├── SecurityId (String) // ISIN, CUSIP, etc.
├── SecurityName (String)
├── Sector (String)
└── IdentifierType (Enum)
```

#### RawESGMetrics
```
RawESGMetrics
├── CarbonEmissions (Decimal)
├── EnergyEfficiency (Decimal)
├── WaterUsage (Decimal)
├── WasteManagement (Decimal)
├── BiodiversityImpact (Decimal)
├── DiversityInclusion (Decimal)
├── HealthSafety (Decimal)
├── CommunityInvestment (Decimal)
├── SupplyChainPractices (Decimal)
├── BoardIndependence (Decimal)
├── ExecutiveCompensation (Decimal)
├── AntiCorruptionPolicies (Decimal)
└── TransparencyDisclosure (Decimal)
```

#### NormalizedESGData
```
NormalizedESGData
├── CarbonEmissionsNormalized (Decimal 0-100)
├── EnergyEfficiencyNormalized (Decimal 0-100)
├── WaterWasteNormalized (Decimal 0-100)
├── BiodiversityNormalized (Decimal 0-100)
├── DiversityInclusionNormalized (Decimal 0-100)
├── HealthSafetyNormalized (Decimal 0-100)
├── CommunityImpactNormalized (Decimal 0-100)
├── SupplyChainNormalized (Decimal 0-100)
├── BoardCompositionNormalized (Decimal 0-100)
├── ExecutiveCompensationNormalized (Decimal 0-100)
├── AntiCorruptionNormalized (Decimal 0-100)
├── TransparencyNormalized (Decimal 0-100)
└── NormalizationMetadata (Object)
```

#### ValidationStatus
```
ValidationStatus
├── IsValid (Boolean)
├── ValidationLevel (Enum: Pass, Warning, Error)
├── ValidationTimestamp (DateTime)
└── ValidatedBy (String)
```

#### ValidationError
```
ValidationError
├── ErrorCode (String)
├── ErrorMessage (String)
├── FieldName (String)
├── ExpectedValue (String)
├── ActualValue (String)
└── Severity (Enum: Warning, Error, Critical)
```

#### ComponentWeightings
```
ComponentWeightings
├── Weights (Dictionary<String, Decimal>)
├── TotalWeight (Decimal) // Must equal 100%
├── MethodologyVersion (String)
└── LastUpdated (DateTime)
```

#### QualityIndicators
```
QualityIndicators
├── DataCompleteness (Decimal 0-100)
├── DataAccuracy (Decimal 0-100)
├── CalculationConfidence (Decimal 0-100)
├── QualityGrade (Enum: High, Medium, Low)
└── QualityFlags (List<String>)
```

## Business Rules and Invariants

### Data Processing Rules
1. **File Processing**: CSV files must be processed in chronological order
2. **Data Validation**: Invalid records are flagged but do not stop batch processing
3. **Normalization**: All ESG metrics must be normalized to 0-100 scale
4. **Completeness**: Batch processing requires minimum 80% valid records to succeed
5. **Auditability**: All processing steps must be logged and traceable

### ESG Scoring Rules
1. **Component Weightings**: Environmental (40%), Social (30%), Governance (30%)
2. **Sub-component Weightings**: Must sum to parent component weight
3. **Score Range**: All scores must be between 0-100
4. **Calculation Order**: Component scores calculated before composite score
5. **Methodology Consistency**: Same methodology version used for entire batch

### Data Quality Rules
1. **Missing Data**: Handle missing values with documented methodology
2. **Outlier Detection**: Flag values outside expected ranges
3. **Duplicate Detection**: Identify and handle duplicate records
4. **Consistency Checks**: Validate data relationships and dependencies
5. **Quality Reporting**: Generate quality reports for every batch

## Aggregate Behavior Methods

### ESGDataBatch Behaviors
- `StartProcessing()`: Initialize batch processing
- `AddRawDataRecord(record)`: Add raw data record to batch
- `ValidateData()`: Perform data validation on all records
- `NormalizeData()`: Apply normalization rules to valid data
- `CompleteProcessing()`: Mark batch as completed
- `GenerateQualityReport()`: Create data quality report
- `GetProcessingStatus()`: Return current processing status

### ESGScoreCalculation Behaviors
- `CalculateEnvironmentalScore()`: Calculate environmental component score
- `CalculateSocialScore()`: Calculate social component score
- `CalculateGovernanceScore()`: Calculate governance component score
- `GenerateCompositeScore()`: Calculate final ESG score
- `ValidateCalculation()`: Ensure calculation integrity
- `UpdateMethodologyVersion()`: Track methodology changes
- `GetScoreBreakdown()`: Return detailed score components

## Domain Events

### Data Processing Events

#### ESGDataBatchStarted
```
ESGDataBatchStarted
├── BatchId (String)
├── SourceFileName (String)
├── ExpectedRecordCount (Integer)
├── ProcessingStartTime (DateTime)
├── MethodologyVersion (String)
└── EventTimestamp (DateTime)
```
**Purpose**: Notify other contexts that ESG data processing has begun
**Consumers**: Analytics Context (for processing status), Configuration Context (for monitoring)

#### ESGDataValidated
```
ESGDataValidated
├── BatchId (String)
├── TotalRecords (Integer)
├── ValidRecords (Integer)
├── InvalidRecords (Integer)
├── ValidationSummary (Object)
├── QualityScore (Decimal)
└── EventTimestamp (DateTime)
```
**Purpose**: Communicate data validation results across contexts
**Consumers**: Analytics Context (for quality reporting), Portfolio Context (for data reliability)

#### ESGScoreCalculated
```
ESGScoreCalculated
├── HoldingIdentifier (String)
├── CompositeESGScore (Decimal)
├── EnvironmentalScore (Decimal)
├── SocialScore (Decimal)
├── GovernanceScore (Decimal)
├── ScoreGrade (String)
├── CalculationDate (DateTime)
├── MethodologyVersion (String)
├── QualityIndicators (Object)
└── EventTimestamp (DateTime)
```
**Purpose**: Notify other contexts of new/updated ESG scores
**Consumers**: Portfolio Context (for aggregation), Risk Context (for risk calculations), Analytics Context (for visualization)

#### ESGDataBatchCompleted
```
ESGDataBatchCompleted
├── BatchId (String)
├── ProcessingDuration (TimeSpan)
├── TotalRecordsProcessed (Integer)
├── SuccessfulCalculations (Integer)
├── FailedCalculations (Integer)
├── OverallQualityScore (Decimal)
├── CompletionStatus (String)
└── EventTimestamp (DateTime)
```
**Purpose**: Signal completion of entire batch processing cycle
**Consumers**: All contexts (for synchronization), Analytics Context (for processing metrics)

#### DataQualityIssueDetected
```
DataQualityIssueDetected
├── BatchId (String)
├── HoldingIdentifier (String)
├── IssueType (String)
├── IssueDescription (String)
├── IssueSeverity (String)
├── AffectedFields (List<String>)
├── RecommendedAction (String)
└── EventTimestamp (DateTime)
```
**Purpose**: Alert about data quality problems requiring attention
**Consumers**: Analytics Context (for quality dashboards), Configuration Context (for monitoring)

### Score Calculation Events

#### ESGMethodologyUpdated
```
ESGMethodologyUpdated
├── PreviousVersion (String)
├── NewVersion (String)
├── ChangedComponents (List<String>)
├── EffectiveDate (DateTime)
├── ImpactedHoldings (List<String>)
└── EventTimestamp (DateTime)
```
**Purpose**: Notify contexts of methodology changes requiring recalculation
**Consumers**: All contexts (for consistency), Analytics Context (for change tracking)

## Domain Services

### ESGDataProcessingService
**Purpose**: Orchestrates the complete data processing pipeline
**Responsibilities**:
- Coordinate data ingestion, validation, normalization, and scoring
- Manage processing workflow and error handling
- Ensure data consistency across processing steps

```
ESGDataProcessingService
├── ProcessCSVFile(filePath: String): ESGDataBatch
├── ValidateDataBatch(batch: ESGDataBatch): ValidationResults
├── NormalizeESGData(rawData: RawESGMetrics): NormalizedESGData
├── CalculateESGScores(normalizedData: NormalizedESGData): ESGScoreCalculation
└── GenerateProcessingReport(batch: ESGDataBatch): ProcessingReport
```

### ESGScoringMethodologyService
**Purpose**: Implements ESG scoring algorithms and methodology
**Responsibilities**:
- Apply ESG scoring methodology consistently
- Calculate component and composite scores
- Ensure scoring transparency and auditability

```
ESGScoringMethodologyService
├── CalculateEnvironmentalScore(data: NormalizedESGData): EnvironmentalScore
├── CalculateSocialScore(data: NormalizedESGData): SocialScore
├── CalculateGovernanceScore(data: NormalizedESGData): GovernanceScore
├── GenerateCompositeScore(components: ComponentScores): CompositeESGScore
├── ValidateScoreCalculation(calculation: ESGScoreCalculation): Boolean
└── GetMethodologyDocumentation(): MethodologyDocument
```

### DataValidationService
**Purpose**: Implements comprehensive data validation rules
**Responsibilities**:
- Validate data completeness and accuracy
- Apply business rules and constraints
- Generate validation reports and recommendations

```
DataValidationService
├── ValidateDataCompleteness(record: RawDataRecord): ValidationResult
├── ValidateDataRanges(record: RawDataRecord): ValidationResult
├── DetectDuplicateRecords(batch: ESGDataBatch): List<DuplicateRecord>
├── ValidateBusinessRules(record: RawDataRecord): ValidationResult
└── GenerateValidationReport(results: List<ValidationResult>): ValidationReport
```

### DataNormalizationService
**Purpose**: Standardizes ESG data to consistent scales and formats
**Responsibilities**:
- Apply normalization algorithms
- Handle missing values and outliers
- Maintain normalization consistency

```
DataNormalizationService
├── NormalizeToScale(value: Decimal, sourceRange: Range, targetRange: Range): Decimal
├── HandleMissingValues(data: RawESGMetrics, strategy: MissingValueStrategy): RawESGMetrics
├── DetectAndHandleOutliers(data: RawESGMetrics): RawESGMetrics
├── ApplyNormalizationRules(data: RawESGMetrics): NormalizedESGData
└── ValidateNormalizationResults(normalized: NormalizedESGData): Boolean
```

## Repositories

### ESGDataBatchRepository
**Purpose**: Manage persistence of ESG data batches and processing state
**Interface**:
```
ESGDataBatchRepository
├── Save(batch: ESGDataBatch): void
├── FindById(batchId: BatchId): ESGDataBatch
├── FindByStatus(status: ProcessingStatus): List<ESGDataBatch>
├── FindByDateRange(startDate: DateTime, endDate: DateTime): List<ESGDataBatch>
├── GetProcessingHistory(holdingId: HoldingIdentifier): List<ESGDataBatch>
└── Delete(batchId: BatchId): void
```

### ESGScoreCalculationRepository
**Purpose**: Manage persistence of ESG score calculations and results
**Interface**:
```
ESGScoreCalculationRepository
├── Save(calculation: ESGScoreCalculation): void
├── FindById(calculationId: String): ESGScoreCalculation
├── FindByHolding(holdingId: HoldingIdentifier): List<ESGScoreCalculation>
├── FindLatestByHolding(holdingId: HoldingIdentifier): ESGScoreCalculation
├── FindByDateRange(startDate: DateTime, endDate: DateTime): List<ESGScoreCalculation>
├── FindByScoreRange(minScore: Decimal, maxScore: Decimal): List<ESGScoreCalculation>
└── GetScoreHistory(holdingId: HoldingIdentifier): List<ESGScoreCalculation>
```

### DataQualityReportRepository
**Purpose**: Manage persistence of data quality reports and metrics
**Interface**:
```
DataQualityReportRepository
├── Save(report: DataQualityReport): void
├── FindById(reportId: String): DataQualityReport
├── FindByBatch(batchId: BatchId): DataQualityReport
├── FindByQualityThreshold(threshold: Decimal): List<DataQualityReport>
├── GetQualityTrends(period: TimePeriod): List<QualityMetric>
└── GetLatestQualityReport(): DataQualityReport
```

## Domain Policies

### DataValidationPolicy
**Purpose**: Define rules and constraints for data validation
**Rules**:
- ESG metrics must be within expected ranges (0-100 after normalization)
- Critical fields (SecurityId, ESG scores) cannot be missing
- Duplicate records based on SecurityId + Date are flagged
- Data older than 24 months triggers warning
- Batch must have >80% valid records to proceed

```
DataValidationPolicy
├── ValidateRequiredFields(record: RawDataRecord): PolicyResult
├── ValidateDataRanges(record: RawDataRecord): PolicyResult
├── ValidateDuplicates(batch: ESGDataBatch): PolicyResult
├── ValidateDataFreshness(record: RawDataRecord): PolicyResult
└── ValidateBatchQuality(batch: ESGDataBatch): PolicyResult
```

### ESGScoringPolicy
**Purpose**: Enforce ESG scoring methodology and business rules
**Rules**:
- Component weightings must sum to 100% (40% E, 30% S, 30% G)
- All scores must be in 0-100 range
- Missing component data results in proportional adjustment
- Methodology version must be consistent within batch
- Score calculations must be reproducible and auditable

```
ESGScoringPolicy
├── ValidateComponentWeightings(weightings: ComponentWeightings): PolicyResult
├── ValidateScoreRanges(calculation: ESGScoreCalculation): PolicyResult
├── HandleMissingComponents(data: NormalizedESGData): PolicyResult
├── ValidateMethodologyConsistency(batch: ESGDataBatch): PolicyResult
└── ValidateCalculationIntegrity(calculation: ESGScoreCalculation): PolicyResult
```

### DataProcessingPolicy
**Purpose**: Define rules for data processing workflow and error handling
**Rules**:
- Files must be processed in chronological order
- Processing cannot proceed with critical validation errors
- Failed records are logged but don't stop batch processing
- Processing timeout after 2 hours triggers failure
- Concurrent processing of same file is prevented

```
DataProcessingPolicy
├── ValidateProcessingOrder(files: List<SourceFile>): PolicyResult
├── ValidateCriticalErrors(batch: ESGDataBatch): PolicyResult
├── HandleProcessingFailures(batch: ESGDataBatch): PolicyResult
├── ValidateProcessingTimeout(batch: ESGDataBatch): PolicyResult
└── PreventConcurrentProcessing(file: SourceFile): PolicyResult
```

### DataQualityPolicy
**Purpose**: Define standards and thresholds for data quality
**Rules**:
- Overall batch quality score must be >70% for acceptance
- Critical data quality issues require immediate attention
- Quality degradation >20% triggers investigation
- Quality reports must be generated for every batch
- Quality metrics must be tracked over time

```
DataQualityPolicy
├── ValidateQualityThresholds(report: DataQualityReport): PolicyResult
├── EscalateCriticalIssues(issues: List<DataQualityIssue>): PolicyResult
├── DetectQualityDegradation(currentReport: DataQualityReport, historicalReports: List<DataQualityReport>): PolicyResult
├── RequireQualityReporting(batch: ESGDataBatch): PolicyResult
└── TrackQualityMetrics(report: DataQualityReport): PolicyResult
```

## Factories

### ESGDataBatchFactory
**Purpose**: Create ESG data batch aggregates with proper initialization
```
ESGDataBatchFactory
├── CreateFromCSVFile(filePath: String, methodologyVersion: String): ESGDataBatch
├── CreateForReprocessing(originalBatch: ESGDataBatch): ESGDataBatch
└── CreateEmptyBatch(batchId: BatchId): ESGDataBatch
```

### ESGScoreCalculationFactory
**Purpose**: Create ESG score calculation aggregates with proper setup
```
ESGScoreCalculationFactory
├── CreateFromNormalizedData(data: NormalizedESGData, methodology: ESGMethodology): ESGScoreCalculation
├── CreateForRecalculation(existingCalculation: ESGScoreCalculation, newMethodology: ESGMethodology): ESGScoreCalculation
└── CreateEmptyCalculation(holdingId: HoldingIdentifier): ESGScoreCalculation
```

## Anti-Corruption Layer Specifications

### Outbound to Portfolio Context
**Purpose**: Transform ESG scores for portfolio consumption
**Transformations**:
- ESGScoreCalculation → PortfolioHoldingESGScore
- Map internal score structure to portfolio-specific format
- Include only relevant score components for portfolio analysis
- Add portfolio-specific metadata (weights, classifications)

### Outbound to Risk Context
**Purpose**: Transform ESG data for risk integration
**Transformations**:
- ESGScoreCalculation → RiskAdjustmentESGData
- Map ESG scores to risk factor format
- Include component breakdown for risk factor analysis
- Add risk-specific quality indicators

### Outbound to Analytics Context
**Purpose**: Transform ESG data for visualization and reporting
**Transformations**:
- ESGScoreCalculation → AnalyticsESGData
- ESGDataBatch → ProcessingMetrics
- DataQualityReport → QualityAnalytics
- Include historical data and trends
- Add visualization-friendly metadata

### Inbound from Configuration Context
**Purpose**: Transform configuration data for internal use
**Transformations**:
- ConfigurationESGMethodology → ESGMethodology
- ConfigurationDataProcessing → ProcessingParameters
- Map external configuration format to internal domain objects
- Validate and apply configuration changes
