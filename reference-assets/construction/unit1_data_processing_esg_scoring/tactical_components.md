# Unit 1: Tactical Components Detailed Specification

## Data Ingestion Orchestrator Components

### 1. FileWatcherService
**Purpose**: Monitor file system for new CSV files and trigger processing
**Pattern**: Observer Pattern with Event Emitter
**Dependencies**: chokidar, fs, path

```typescript
interface FileWatcherService {
  // Configuration
  watchPaths: string[];
  filePattern: RegExp;
  pollingInterval: number;
  
  // Core methods
  startWatching(): Promise<void>;
  stopWatching(): Promise<void>;
  addWatchPath(path: string): void;
  removeWatchPath(path: string): void;
  
  // Event handlers
  onFileDetected(callback: (filePath: string) => void): void;
  onFileChanged(callback: (filePath: string) => void): void;
  onFileRemoved(callback: (filePath: string) => void): void;
  onError(callback: (error: Error) => void): void;
  
  // Validation
  validateFile(filePath: string): Promise<FileValidationResult>;
  isProcessableFile(filePath: string): boolean;
}

interface FileValidationResult {
  isValid: boolean;
  fileSize: number;
  lastModified: Date;
  errors: string[];
  warnings: string[];
}
```

### 2. CSVParserService
**Purpose**: Stream-based CSV parsing with validation and error handling
**Pattern**: Strategy Pattern for different parsing strategies
**Dependencies**: papa-parse, stream

```typescript
interface CSVParserService {
  // Configuration
  parsingOptions: CSVParsingOptions;
  validationSchema: JSONSchema;
  
  // Core methods
  parseFile(filePath: string): AsyncIterable<CSVRecord>;
  parseStream(stream: ReadableStream): AsyncIterable<CSVRecord>;
  validateRecord(record: CSVRecord): ValidationResult;
  
  // Error handling
  onParsingError(callback: (error: ParsingError, lineNumber: number) => void): void;
  onValidationError(callback: (error: ValidationError, record: CSVRecord) => void): void;
  
  // Statistics
  getParsingStatistics(): ParsingStatistics;
  resetStatistics(): void;
}

interface CSVParsingOptions {
  delimiter: string;
  header: boolean;
  skipEmptyLines: boolean;
  trimHeaders: boolean;
  transformHeader: (header: string) => string;
  chunkSize: number;
}

interface ParsingStatistics {
  totalRecords: number;
  validRecords: number;
  invalidRecords: number;
  processingTime: number;
  throughput: number; // records per second
}
```

### 3. BatchJobScheduler
**Purpose**: Schedule and manage batch processing jobs with retry mechanisms
**Pattern**: Command Pattern with Queue
**Dependencies**: node-cron, bull (in-memory queue)

```typescript
interface BatchJobScheduler {
  // Job management
  scheduleJob(job: BatchJob): Promise<string>;
  cancelJob(jobId: string): Promise<boolean>;
  retryJob(jobId: string): Promise<void>;
  getJobStatus(jobId: string): Promise<JobStatus>;
  
  // Queue management
  getQueueSize(): number;
  pauseQueue(): Promise<void>;
  resumeQueue(): Promise<void>;
  clearQueue(): Promise<void>;
  
  // Scheduling
  scheduleCronJob(cronExpression: string, jobData: any): Promise<string>;
  scheduleDelayedJob(delay: number, jobData: any): Promise<string>;
  
  // Event handlers
  onJobCompleted(callback: (jobId: string, result: any) => void): void;
  onJobFailed(callback: (jobId: string, error: Error) => void): void;
  onJobProgress(callback: (jobId: string, progress: number) => void): void;
}

interface BatchJob {
  id: string;
  type: 'FILE_PROCESSING' | 'SCORE_CALCULATION' | 'QUALITY_REPORT';
  data: any;
  priority: number;
  attempts: number;
  maxAttempts: number;
  delay: number;
  backoff: BackoffStrategy;
}

interface JobStatus {
  id: string;
  status: 'waiting' | 'active' | 'completed' | 'failed' | 'delayed';
  progress: number;
  createdAt: Date;
  processedAt?: Date;
  completedAt?: Date;
  failedAt?: Date;
  error?: Error;
}
```

### 4. IngestionStatusTracker
**Purpose**: Track processing status with state machine pattern
**Pattern**: State Machine Pattern
**Dependencies**: xstate

```typescript
interface IngestionStatusTracker {
  // State management
  createBatchStatus(batchId: string, fileName: string): Promise<BatchStatus>;
  updateBatchStatus(batchId: string, status: BatchStatusUpdate): Promise<void>;
  getBatchStatus(batchId: string): Promise<BatchStatus>;
  getAllBatchStatuses(): Promise<BatchStatus[]>;
  
  // State transitions
  startProcessing(batchId: string): Promise<void>;
  completeValidation(batchId: string, validationResult: ValidationResult): Promise<void>;
  completeNormalization(batchId: string): Promise<void>;
  completeScoring(batchId: string): Promise<void>;
  failProcessing(batchId: string, error: Error): Promise<void>;
  
  // Progress tracking
  updateProgress(batchId: string, progress: ProgressUpdate): Promise<void>;
  getProgress(batchId: string): Promise<ProcessingProgress>;
  
  // Event handlers
  onStatusChange(callback: (batchId: string, oldStatus: string, newStatus: string) => void): void;
}

interface BatchStatus {
  batchId: string;
  fileName: string;
  fileSize: number;
  status: BatchProcessingStatus;
  currentStep: ProcessingStep;
  progress: ProcessingProgress;
  startTime: Date;
  endTime?: Date;
  error?: Error;
  metadata: Record<string, any>;
}

type BatchProcessingStatus = 
  | 'DETECTED' 
  | 'PARSING' 
  | 'VALIDATING' 
  | 'NORMALIZING' 
  | 'SCORING' 
  | 'COMPLETED' 
  | 'FAILED';

interface ProcessingProgress {
  totalRecords: number;
  processedRecords: number;
  validRecords: number;
  invalidRecords: number;
  percentage: number;
  estimatedTimeRemaining: number;
}
```

### 5. RetryMechanism
**Purpose**: Implement circuit breaker and exponential backoff for resilience
**Pattern**: Circuit Breaker Pattern
**Dependencies**: opossum

```typescript
interface RetryMechanism {
  // Retry configuration
  maxRetries: number;
  baseDelay: number;
  maxDelay: number;
  backoffMultiplier: number;
  jitter: boolean;
  
  // Circuit breaker configuration
  circuitBreakerOptions: CircuitBreakerOptions;
  
  // Core methods
  executeWithRetry<T>(operation: () => Promise<T>, context: RetryContext): Promise<T>;
  executeWithCircuitBreaker<T>(operation: () => Promise<T>, key: string): Promise<T>;
  
  // Circuit breaker management
  getCircuitBreakerState(key: string): CircuitBreakerState;
  resetCircuitBreaker(key: string): void;
  
  // Retry strategies
  exponentialBackoff(attempt: number): number;
  linearBackoff(attempt: number): number;
  fixedDelay(attempt: number): number;
  
  // Event handlers
  onRetryAttempt(callback: (attempt: number, error: Error) => void): void;
  onCircuitBreakerOpen(callback: (key: string) => void): void;
  onCircuitBreakerClose(callback: (key: string) => void): void;
}

interface RetryContext {
  operationName: string;
  maxRetries?: number;
  backoffStrategy?: BackoffStrategy;
  shouldRetry?: (error: Error) => boolean;
  onRetry?: (attempt: number, error: Error) => void;
}

interface CircuitBreakerOptions {
  timeout: number;
  errorThresholdPercentage: number;
  resetTimeout: number;
  rollingCountTimeout: number;
  rollingCountBuckets: number;
}
```

### 6. DataIngestionOrchestrator
**Purpose**: Main orchestrator implementing Saga pattern for data ingestion workflow
**Pattern**: Saga Pattern for distributed transactions
**Dependencies**: All above components

```typescript
interface DataIngestionOrchestrator {
  // Workflow management
  startIngestionWorkflow(filePath: string): Promise<string>;
  getWorkflowStatus(workflowId: string): Promise<WorkflowStatus>;
  cancelWorkflow(workflowId: string): Promise<void>;
  
  // Saga steps
  executeFileDetection(filePath: string): Promise<FileDetectionResult>;
  executeParsing(filePath: string): Promise<ParsingResult>;
  executeValidation(records: CSVRecord[]): Promise<ValidationResult>;
  executeNormalization(validRecords: CSVRecord[]): Promise<NormalizationResult>;
  executeScoring(normalizedData: NormalizedESGMetrics[]): Promise<ScoringResult>;
  
  // Compensation actions (for rollback)
  compensateFileDetection(context: WorkflowContext): Promise<void>;
  compensateParsing(context: WorkflowContext): Promise<void>;
  compensateValidation(context: WorkflowContext): Promise<void>;
  compensateNormalization(context: WorkflowContext): Promise<void>;
  compensateScoring(context: WorkflowContext): Promise<void>;
  
  // Event handlers
  onWorkflowStarted(callback: (workflowId: string) => void): void;
  onWorkflowCompleted(callback: (workflowId: string, result: any) => void): void;
  onWorkflowFailed(callback: (workflowId: string, error: Error) => void): void;
  onStepCompleted(callback: (workflowId: string, step: string, result: any) => void): void;
}

interface WorkflowStatus {
  workflowId: string;
  status: 'RUNNING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  currentStep: string;
  completedSteps: string[];
  failedStep?: string;
  error?: Error;
  startTime: Date;
  endTime?: Date;
  context: WorkflowContext;
}

interface WorkflowContext {
  filePath: string;
  batchId: string;
  totalRecords: number;
  processedRecords: number;
  metadata: Record<string, any>;
}
```

## Data Validation and Quality Engine Components

### 1. ValidationRuleEngine
**Purpose**: Configurable rule engine with JSON Schema validation
**Pattern**: Rule Engine Pattern
**Dependencies**: ajv, lodash

```typescript
interface ValidationRuleEngine {
  // Rule management
  addRule(rule: ValidationRule): void;
  removeRule(ruleId: string): void;
  updateRule(ruleId: string, rule: ValidationRule): void;
  getRules(): ValidationRule[];
  
  // Validation execution
  validateRecord(record: CSVRecord): ValidationResult;
  validateBatch(records: CSVRecord[]): BatchValidationResult;
  validateField(fieldName: string, value: any): FieldValidationResult;
  
  // Schema management
  setSchema(schema: JSONSchema): void;
  getSchema(): JSONSchema;
  validateSchema(schema: JSONSchema): boolean;
  
  // Rule compilation
  compileRules(): void;
  getRuleStatistics(): RuleStatistics;
}

interface ValidationRule {
  id: string;
  name: string;
  description: string;
  field: string;
  type: 'REQUIRED' | 'TYPE' | 'RANGE' | 'PATTERN' | 'CUSTOM';
  condition: any;
  severity: 'ERROR' | 'WARNING' | 'INFO';
  message: string;
  isActive: boolean;
}

interface ValidationResult {
  isValid: boolean;
  errors: ValidationError[];
  warnings: ValidationWarning[];
  fieldResults: Map<string, FieldValidationResult>;
  executionTime: number;
}
```

### 2. DataQualityCalculator
**Purpose**: Statistical analysis and quality metrics calculation
**Pattern**: Strategy Pattern for different quality metrics
**Dependencies**: simple-statistics, lodash

```typescript
interface DataQualityCalculator {
  // Quality metrics calculation
  calculateCompleteness(records: CSVRecord[]): CompletenessMetrics;
  calculateAccuracy(records: CSVRecord[]): AccuracyMetrics;
  calculateConsistency(records: CSVRecord[]): ConsistencyMetrics;
  calculateValidity(records: CSVRecord[]): ValidityMetrics;
  calculateUniqueness(records: CSVRecord[]): UniquenessMetrics;
  calculateTimeliness(records: CSVRecord[]): TimelinessMetrics;
  
  // Overall quality assessment
  calculateOverallQuality(records: CSVRecord[]): DataQualityMetrics;
  generateQualityReport(records: CSVRecord[]): QualityReport;
  
  // Statistical analysis
  calculateDescriptiveStatistics(field: string, values: number[]): DescriptiveStatistics;
  detectOutliers(values: number[]): OutlierDetectionResult;
  calculateCorrelation(field1: number[], field2: number[]): number;
  
  // Trend analysis
  calculateTrends(historicalData: HistoricalDataPoint[]): TrendAnalysis;
  compareWithBaseline(current: DataQualityMetrics, baseline: DataQualityMetrics): QualityComparison;
}

interface CompletenessMetrics {
  totalFields: number;
  completeFields: number;
  missingFields: number;
  completenessRatio: number;
  fieldCompleteness: Map<string, number>;
}

interface DescriptiveStatistics {
  count: number;
  mean: number;
  median: number;
  mode: number[];
  standardDeviation: number;
  variance: number;
  min: number;
  max: number;
  quartiles: [number, number, number];
  skewness: number;
  kurtosis: number;
}
```

### 3. AnomalyDetector
**Purpose**: Statistical outlier detection using various methods
**Pattern**: Strategy Pattern for different detection algorithms
**Dependencies**: ml-matrix, simple-statistics

```typescript
interface AnomalyDetector {
  // Detection methods
  detectZScoreOutliers(values: number[], threshold: number): OutlierDetectionResult;
  detectIQROutliers(values: number[]): OutlierDetectionResult;
  detectIsolationForestOutliers(data: number[][]): OutlierDetectionResult;
  detectLocalOutlierFactor(data: number[][]): OutlierDetectionResult;
  
  // Configuration
  setDetectionThreshold(method: string, threshold: number): void;
  getDetectionThreshold(method: string): number;
  
  // Batch detection
  detectAnomaliesInBatch(records: CSVRecord[]): BatchAnomalyResult;
  detectFieldAnomalies(field: string, values: number[]): FieldAnomalyResult;
  
  // Historical comparison
  detectAnomaliesAgainstBaseline(current: number[], baseline: number[]): BaselineAnomalyResult;
  
  // Reporting
  generateAnomalyReport(results: OutlierDetectionResult[]): AnomalyReport;
}

interface OutlierDetectionResult {
  method: string;
  outlierIndices: number[];
  outlierValues: number[];
  threshold: number;
  confidence: number;
  metadata: Record<string, any>;
}

interface BatchAnomalyResult {
  totalRecords: number;
  anomalousRecords: number;
  anomalyRate: number;
  fieldAnomalies: Map<string, FieldAnomalyResult>;
  overallConfidence: number;
}
```

## Data Normalization Processor Components

### 1. ScaleTransformer
**Purpose**: Implement various normalization algorithms
**Pattern**: Strategy Pattern for different scaling methods
**Dependencies**: ml-matrix, lodash

```typescript
interface ScaleTransformer {
  // Transformation methods
  minMaxScale(values: number[], min: number, max: number): number[];
  zScoreNormalize(values: number[]): number[];
  robustScale(values: number[]): number[];
  unitVectorScale(values: number[]): number[];
  
  // Batch transformation
  transformBatch(records: CSVRecord[], transformationConfig: TransformationConfig): NormalizedESGMetrics[];
  transformField(field: string, values: number[], method: ScalingMethod): number[];
  
  // Inverse transformation
  inverseTransform(normalizedValues: number[], originalStats: ScalingStatistics): number[];
  
  // Statistics management
  calculateScalingStatistics(values: number[]): ScalingStatistics;
  saveScalingStatistics(field: string, stats: ScalingStatistics): void;
  getScalingStatistics(field: string): ScalingStatistics;
  
  // Validation
  validateTransformation(original: number[], transformed: number[]): TransformationValidationResult;
}

interface TransformationConfig {
  method: ScalingMethod;
  targetRange: [number, number];
  handleOutliers: boolean;
  preserveZeros: boolean;
  fieldMappings: Map<string, string>;
}

interface ScalingStatistics {
  min: number;
  max: number;
  mean: number;
  standardDeviation: number;
  quartiles: [number, number, number];
  outlierThreshold: number;
}

type ScalingMethod = 'MIN_MAX' | 'Z_SCORE' | 'ROBUST' | 'UNIT_VECTOR';
```

### 2. MissingValueImputer
**Purpose**: Handle missing values using various imputation strategies
**Pattern**: Strategy Pattern for different imputation methods
**Dependencies**: ml-matrix, simple-statistics

```typescript
interface MissingValueImputer {
  // Imputation methods
  meanImputation(values: number[]): number[];
  medianImputation(values: number[]): number[];
  modeImputation(values: any[]): any[];
  forwardFillImputation(values: any[]): any[];
  backwardFillImputation(values: any[]): any[];
  linearInterpolation(values: number[]): number[];
  
  // Advanced imputation
  knnImputation(data: number[][], k: number): number[][];
  regressionImputation(data: number[][], targetColumn: number): number[];
  
  // Batch imputation
  imputeBatch(records: CSVRecord[], imputationConfig: ImputationConfig): CSVRecord[];
  imputeField(field: string, values: any[], method: ImputationMethod): any[];
  
  // Missing value analysis
  analyzeMissingValues(records: CSVRecord[]): MissingValueAnalysis;
  getMissingValuePattern(records: CSVRecord[]): MissingValuePattern;
  
  // Validation
  validateImputation(original: any[], imputed: any[]): ImputationValidationResult;
}

interface ImputationConfig {
  defaultMethod: ImputationMethod;
  fieldMethods: Map<string, ImputationMethod>;
  allowPartialImputation: boolean;
  qualityThreshold: number;
  preserveDistribution: boolean;
}

interface MissingValueAnalysis {
  totalRecords: number;
  recordsWithMissingValues: number;
  missingValueRate: number;
  fieldMissingRates: Map<string, number>;
  missingValuePatterns: MissingValuePattern[];
}

type ImputationMethod = 'MEAN' | 'MEDIAN' | 'MODE' | 'FORWARD_FILL' | 'BACKWARD_FILL' | 'LINEAR' | 'KNN' | 'REGRESSION' | 'DROP';
```

### 3. DataLineageTracker
**Purpose**: Track data transformations for audit and compliance
**Pattern**: Event Sourcing Pattern
**Dependencies**: uuid, lodash

```typescript
interface DataLineageTracker {
  // Lineage tracking
  startLineageTracking(batchId: string, sourceFile: string): string;
  recordTransformation(lineageId: string, transformation: TransformationStep): void;
  completeLineageTracking(lineageId: string): DataLineage;
  
  // Lineage queries
  getLineage(holdingId: HoldingId): DataLineage[];
  getTransformationHistory(holdingId: HoldingId): TransformationStep[];
  getSourceFiles(holdingId: HoldingId): string[];
  
  // Lineage analysis
  analyzeLineage(lineageId: string): LineageAnalysis;
  validateLineageIntegrity(lineageId: string): LineageValidationResult;
  
  // Lineage visualization
  generateLineageGraph(holdingId: HoldingId): LineageGraph;
  exportLineage(lineageId: string, format: 'JSON' | 'CSV' | 'XML'): string;
  
  // Compliance reporting
  generateComplianceReport(holdingIds: HoldingId[]): ComplianceReport;
  auditLineage(lineageId: string): AuditResult;
}

interface TransformationStep {
  stepId: string;
  stepName: string;
  stepType: 'PARSING' | 'VALIDATION' | 'NORMALIZATION' | 'IMPUTATION' | 'SCORING';
  inputData: any;
  outputData: any;
  transformation: string;
  parameters: Record<string, any>;
  timestamp: Date;
  executionTime: number;
  quality: TransformationQuality;
}

interface LineageAnalysis {
  totalSteps: number;
  transformationTypes: Map<string, number>;
  qualityScore: number;
  dataIntegrityScore: number;
  complianceScore: number;
  recommendations: string[];
}
```

## ESG Scoring Calculation Engine Components

### 1. ComponentScoreCalculator
**Purpose**: Calculate individual E, S, G component scores
**Pattern**: Strategy Pattern for different calculation methods
**Dependencies**: lodash, mathjs

```typescript
interface ComponentScoreCalculator {
  // Component calculation
  calculateEnvironmentalScore(data: NormalizedESGMetrics): ESGComponentScore;
  calculateSocialScore(data: NormalizedESGMetrics): ESGComponentScore;
  calculateGovernanceScore(data: NormalizedESGMetrics): ESGComponentScore;
  
  // Sub-component calculation
  calculateSubComponentScore(component: string, subComponent: string, value: number, weight: number): number;
  
  // Batch calculation
  calculateComponentScores(dataArray: NormalizedESGMetrics[]): ComponentScoreResult[];
  
  // Validation
  validateComponentScore(score: ESGComponentScore): ComponentValidationResult;
  validateWeightings(weightings: ComponentWeightings): WeightingValidationResult;
  
  // Configuration
  setWeightings(weightings: ComponentWeightings): void;
  getWeightings(): ComponentWeightings;
  updateWeighting(component: string, subComponent: string, weight: number): void;
  
  // Analysis
  analyzeComponentContribution(score: ESGComponentScore): ComponentContributionAnalysis;
  compareComponentScores(score1: ESGComponentScore, score2: ESGComponentScore): ComponentComparison;
}

interface ComponentScoreResult {
  holdingId: HoldingId;
  environmental: ESGComponentScore;
  social: ESGComponentScore;
  governance: ESGComponentScore;
  calculationMetadata: CalculationMetadata;
}

interface ComponentContributionAnalysis {
  component: string;
  totalScore: number;
  subComponentContributions: Map<string, number>;
  dominantFactors: string[];
  improvementAreas: string[];
}
```

### 2. WeightedAggregator
**Purpose**: Aggregate component scores into composite ESG score
**Pattern**: Template Method Pattern
**Dependencies**: mathjs, lodash

```typescript
interface WeightedAggregator {
  // Aggregation methods
  calculateWeightedAverage(scores: ComponentScore[], weights: number[]): number;
  calculateCompositeScore(componentScores: ComponentScoreResult): CompositeESGScore;
  
  // Batch aggregation
  aggregateBatch(componentScores: ComponentScoreResult[]): CompositeESGScore[];
  
  // Validation
  validateWeights(weights: number[]): WeightValidationResult;
  validateComponentScores(scores: ComponentScore[]): ComponentValidationResult;
  
  // Analysis
  analyzeScoreContribution(compositeScore: CompositeESGScore): ScoreContributionAnalysis;
  performSensitivityAnalysis(componentScores: ComponentScoreResult, weightVariations: number[]): SensitivityAnalysisResult;
  
  // Configuration
  setAggregationMethod(method: AggregationMethod): void;
  getAggregationMethod(): AggregationMethod;
  
  // Comparison
  compareCompositeScores(score1: CompositeESGScore, score2: CompositeESGScore): ScoreComparison;
}

interface ScoreContributionAnalysis {
  totalScore: number;
  environmentalContribution: number;
  socialContribution: number;
  governanceContribution: number;
  contributionPercentages: Map<string, number>;
  balanceScore: number; // How balanced the contributions are
}

type AggregationMethod = 'WEIGHTED_AVERAGE' | 'GEOMETRIC_MEAN' | 'HARMONIC_MEAN' | 'CUSTOM';
```

### 3. ScoreValidator
**Purpose**: Validate calculated scores against business rules
**Pattern**: Chain of Responsibility Pattern
**Dependencies**: joi, lodash

```typescript
interface ScoreValidator {
  // Validation methods
  validateCompositeScore(score: CompositeESGScore): ScoreValidationResult;
  validateComponentScore(score: ESGComponentScore): ComponentValidationResult;
  validateScoreRange(score: number, min: number, max: number): RangeValidationResult;
  
  // Business rule validation
  validateBusinessRules(score: CompositeESGScore): BusinessRuleValidationResult;
  validateScoreConsistency(scores: CompositeESGScore[]): ConsistencyValidationResult;
  validateScoreProgression(historicalScores: CompositeESGScore[]): ProgressionValidationResult;
  
  // Validation rules management
  addValidationRule(rule: ValidationRule): void;
  removeValidationRule(ruleId: string): void;
  getValidationRules(): ValidationRule[];
  
  // Batch validation
  validateScoreBatch(scores: CompositeESGScore[]): BatchValidationResult;
  
  // Reporting
  generateValidationReport(results: ScoreValidationResult[]): ValidationReport;
}

interface ScoreValidationResult {
  isValid: boolean;
  score: CompositeESGScore;
  violations: ValidationViolation[];
  warnings: ValidationWarning[];
  confidence: number;
  validationTime: Date;
}

interface ValidationViolation {
  ruleId: string;
  ruleName: string;
  severity: 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'LOW';
  message: string;
  field: string;
  actualValue: any;
  expectedValue: any;
}
```

This comprehensive tactical components specification provides the detailed blueprint for implementing each component of the ESG Data Processing & Scoring Engine, following enterprise-grade patterns and best practices.
