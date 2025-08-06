# Unit 1: Comprehensive Data Models Design

## Domain-Driven Design (DDD) Data Models

### Value Objects

#### 1. HoldingId
```typescript
interface HoldingId {
  value: string;
  validate(): boolean;
  equals(other: HoldingId): boolean;
}

// JSON Schema
{
  "type": "object",
  "properties": {
    "value": {
      "type": "string",
      "pattern": "^[A-Z0-9]{6,12}$",
      "description": "Alphanumeric holding identifier"
    }
  },
  "required": ["value"]
}
```

#### 2. ESGComponentScore
```typescript
interface ESGComponentScore {
  value: number; // 0-100 range
  component: 'environmental' | 'social' | 'governance';
  subComponents: Map<string, number>;
  calculationDate: Date;
  methodologyVersion: string;
  
  validate(): boolean;
  isValid(): boolean;
  equals(other: ESGComponentScore): boolean;
}

// JSON Schema
{
  "type": "object",
  "properties": {
    "value": {
      "type": "number",
      "minimum": 0,
      "maximum": 100
    },
    "component": {
      "type": "string",
      "enum": ["environmental", "social", "governance"]
    },
    "subComponents": {
      "type": "object",
      "patternProperties": {
        "^[a-z_]+$": {
          "type": "number",
          "minimum": 0,
          "maximum": 100
        }
      }
    },
    "calculationDate": {
      "type": "string",
      "format": "date-time"
    },
    "methodologyVersion": {
      "type": "string",
      "pattern": "^\\d+\\.\\d+\\.\\d+$"
    }
  },
  "required": ["value", "component", "calculationDate", "methodologyVersion"]
}
```

#### 3. CompositeESGScore
```typescript
interface CompositeESGScore {
  value: number; // 0-100 range
  environmental: ESGComponentScore;
  social: ESGComponentScore;
  governance: ESGComponentScore;
  calculationDate: Date;
  methodologyVersion: string;
  
  validate(): boolean;
  getWeightedAverage(): number;
  equals(other: CompositeESGScore): boolean;
}
```

#### 4. ProcessingDate
```typescript
interface ProcessingDate {
  value: Date;
  timezone: string;
  
  toISOString(): string;
  isAfter(other: ProcessingDate): boolean;
  isBefore(other: ProcessingDate): boolean;
  equals(other: ProcessingDate): boolean;
}
```

#### 5. MethodologyVersion
```typescript
interface MethodologyVersion {
  major: number;
  minor: number;
  patch: number;
  
  toString(): string; // "1.2.3"
  isCompatibleWith(other: MethodologyVersion): boolean;
  isNewerThan(other: MethodologyVersion): boolean;
}
```

#### 6. DataQualityMetrics
```typescript
interface DataQualityMetrics {
  completeness: number; // 0-1 range
  accuracy: number; // 0-1 range
  consistency: number; // 0-1 range
  validity: number; // 0-1 range
  uniqueness: number; // 0-1 range
  timeliness: number; // 0-1 range
  
  getOverallQuality(): number;
  validate(): boolean;
}
```

### Entities

#### 1. Holding Entity
```typescript
interface Holding {
  id: HoldingId;
  name: string;
  sector: string;
  industry: string;
  marketCap: number;
  currency: string;
  createdAt: ProcessingDate;
  updatedAt: ProcessingDate;
  
  // Business methods
  updateBasicInfo(info: HoldingBasicInfo): void;
  validate(): boolean;
  equals(other: Holding): boolean;
}

// JSON Schema
{
  "type": "object",
  "properties": {
    "id": { "$ref": "#/definitions/HoldingId" },
    "name": {
      "type": "string",
      "minLength": 1,
      "maxLength": 255
    },
    "sector": {
      "type": "string",
      "enum": ["Technology", "Healthcare", "Financial", "Energy", "Industrial", "Consumer", "Materials", "Utilities", "Real Estate", "Communication"]
    },
    "industry": {
      "type": "string",
      "minLength": 1,
      "maxLength": 100
    },
    "marketCap": {
      "type": "number",
      "minimum": 0
    },
    "currency": {
      "type": "string",
      "pattern": "^[A-Z]{3}$"
    },
    "createdAt": { "$ref": "#/definitions/ProcessingDate" },
    "updatedAt": { "$ref": "#/definitions/ProcessingDate" }
  },
  "required": ["id", "name", "sector", "industry", "marketCap", "currency", "createdAt", "updatedAt"]
}
```

#### 2. ESGData Entity
```typescript
interface ESGData {
  id: string;
  holdingId: HoldingId;
  rawData: RawESGMetrics;
  normalizedData: NormalizedESGMetrics;
  dataSource: string;
  dataDate: ProcessingDate;
  ingestionDate: ProcessingDate;
  version: number;
  
  // Business methods
  normalize(): NormalizedESGMetrics;
  validate(): ValidationResult;
  getDataLineage(): DataLineage[];
}
```

#### 3. Methodology Entity
```typescript
interface Methodology {
  id: string;
  version: MethodologyVersion;
  name: string;
  description: string;
  weightings: ComponentWeightings;
  calculationRules: CalculationRule[];
  validationRules: ValidationRule[];
  createdAt: ProcessingDate;
  createdBy: string;
  isActive: boolean;
  
  // Business methods
  calculateESGScore(data: NormalizedESGMetrics): CompositeESGScore;
  validate(): boolean;
  getDocumentation(): MethodologyDocumentation;
}
```

### Aggregate Roots

#### 1. ESGScore Aggregate Root
```typescript
interface ESGScoreAggregate {
  // Aggregate identity
  holdingId: HoldingId;
  
  // Aggregate state
  currentScore: CompositeESGScore;
  historicalScores: CompositeESGScore[];
  methodology: Methodology;
  lastCalculated: ProcessingDate;
  
  // Domain events
  private domainEvents: DomainEvent[];
  
  // Business methods
  calculateNewScore(esgData: ESGData, methodology: Methodology): void;
  updateScore(newScore: CompositeESGScore): void;
  getScoreHistory(fromDate?: Date, toDate?: Date): CompositeESGScore[];
  
  // Event handling
  addDomainEvent(event: DomainEvent): void;
  clearDomainEvents(): DomainEvent[];
  
  // Validation
  validate(): boolean;
  isConsistent(): boolean;
}
```

#### 2. DataBatch Aggregate Root
```typescript
interface DataBatchAggregate {
  // Aggregate identity
  batchId: string;
  
  // Aggregate state
  fileName: string;
  fileSize: number;
  totalRecords: number;
  processedRecords: number;
  failedRecords: number;
  status: BatchStatus;
  startTime: ProcessingDate;
  endTime?: ProcessingDate;
  qualityMetrics: DataQualityMetrics;
  
  // Domain events
  private domainEvents: DomainEvent[];
  
  // Business methods
  startProcessing(): void;
  processRecord(record: CSVRecord): ProcessingResult;
  completeProcessing(): void;
  failProcessing(error: Error): void;
  
  // Progress tracking
  getProgress(): number;
  getProcessingTime(): number;
  
  // Event handling
  addDomainEvent(event: DomainEvent): void;
  clearDomainEvents(): DomainEvent[];
}
```

### Domain Events

#### 1. FileDetectedEvent
```typescript
interface FileDetectedEvent extends DomainEvent {
  eventType: 'FileDetected';
  fileName: string;
  filePath: string;
  fileSize: number;
  detectedAt: ProcessingDate;
  batchId: string;
}
```

#### 2. DataValidationCompletedEvent
```typescript
interface DataValidationCompletedEvent extends DomainEvent {
  eventType: 'DataValidationCompleted';
  batchId: string;
  totalRecords: number;
  validRecords: number;
  invalidRecords: number;
  qualityMetrics: DataQualityMetrics;
  completedAt: ProcessingDate;
}
```

#### 3. ESGScoreCalculatedEvent
```typescript
interface ESGScoreCalculatedEvent extends DomainEvent {
  eventType: 'ESGScoreCalculated';
  holdingId: HoldingId;
  previousScore?: CompositeESGScore;
  newScore: CompositeESGScore;
  methodology: MethodologyVersion;
  calculatedAt: ProcessingDate;
  batchId: string;
}
```

#### 4. DataQualityReportGeneratedEvent
```typescript
interface DataQualityReportGeneratedEvent extends DomainEvent {
  eventType: 'DataQualityReportGenerated';
  batchId: string;
  reportId: string;
  qualityMetrics: DataQualityMetrics;
  reportPath: string;
  generatedAt: ProcessingDate;
}
```

### Raw Data Models

#### 1. CSVRecord (Raw Input)
```typescript
interface CSVRecord {
  // Required fields
  holding_id: string;
  holding_name: string;
  sector: string;
  industry: string;
  data_date: string;
  
  // Environmental metrics
  carbon_emissions?: number;
  energy_efficiency?: number;
  water_usage?: number;
  waste_management?: number;
  biodiversity_impact?: number;
  
  // Social metrics
  diversity_score?: number;
  employee_satisfaction?: number;
  health_safety_score?: number;
  community_investment?: number;
  supply_chain_ethics?: number;
  
  // Governance metrics
  board_independence?: number;
  board_diversity?: number;
  executive_compensation?: number;
  audit_quality?: number;
  transparency_score?: number;
  ethics_compliance?: number;
  
  // Metadata
  data_source?: string;
  data_quality?: string;
}

// JSON Schema for CSV validation
{
  "type": "object",
  "properties": {
    "holding_id": { "type": "string", "pattern": "^[A-Z0-9]{6,12}$" },
    "holding_name": { "type": "string", "minLength": 1, "maxLength": 255 },
    "sector": { "type": "string", "minLength": 1 },
    "industry": { "type": "string", "minLength": 1 },
    "data_date": { "type": "string", "format": "date" },
    
    // Environmental (optional but validated if present)
    "carbon_emissions": { "type": "number", "minimum": 0, "maximum": 100 },
    "energy_efficiency": { "type": "number", "minimum": 0, "maximum": 100 },
    "water_usage": { "type": "number", "minimum": 0, "maximum": 100 },
    "waste_management": { "type": "number", "minimum": 0, "maximum": 100 },
    "biodiversity_impact": { "type": "number", "minimum": 0, "maximum": 100 },
    
    // Social (optional but validated if present)
    "diversity_score": { "type": "number", "minimum": 0, "maximum": 100 },
    "employee_satisfaction": { "type": "number", "minimum": 0, "maximum": 100 },
    "health_safety_score": { "type": "number", "minimum": 0, "maximum": 100 },
    "community_investment": { "type": "number", "minimum": 0, "maximum": 100 },
    "supply_chain_ethics": { "type": "number", "minimum": 0, "maximum": 100 },
    
    // Governance (optional but validated if present)
    "board_independence": { "type": "number", "minimum": 0, "maximum": 100 },
    "board_diversity": { "type": "number", "minimum": 0, "maximum": 100 },
    "executive_compensation": { "type": "number", "minimum": 0, "maximum": 100 },
    "audit_quality": { "type": "number", "minimum": 0, "maximum": 100 },
    "transparency_score": { "type": "number", "minimum": 0, "maximum": 100 },
    "ethics_compliance": { "type": "number", "minimum": 0, "maximum": 100 }
  },
  "required": ["holding_id", "holding_name", "sector", "industry", "data_date"]
}
```

#### 2. NormalizedESGMetrics
```typescript
interface NormalizedESGMetrics {
  holdingId: HoldingId;
  dataDate: ProcessingDate;
  
  // Environmental metrics (normalized 0-100)
  environmental: {
    carbonEmissions: number;
    energyEfficiency: number;
    waterWasteManagement: number;
    biodiversityImpact: number;
  };
  
  // Social metrics (normalized 0-100)
  social: {
    diversityInclusion: number;
    healthSafety: number;
    communityImpact: number;
    supplyChainPractices: number;
  };
  
  // Governance metrics (normalized 0-100)
  governance: {
    boardComposition: number;
    executiveCompensation: number;
    antiCorruption: number;
    transparencyPractices: number;
  };
  
  // Metadata
  normalizationMethod: string;
  dataQuality: DataQualityMetrics;
  transformationHistory: TransformationStep[];
}
```

### Audit and Lineage Models

#### 1. DataLineage
```typescript
interface DataLineage {
  id: string;
  holdingId: HoldingId;
  sourceFile: string;
  transformationSteps: TransformationStep[];
  createdAt: ProcessingDate;
  createdBy: string;
}

interface TransformationStep {
  stepId: string;
  stepName: string;
  inputData: any;
  outputData: any;
  transformation: string;
  timestamp: ProcessingDate;
  parameters: Record<string, any>;
}
```

#### 2. AuditTrail
```typescript
interface AuditTrail {
  id: string;
  entityType: string;
  entityId: string;
  action: 'CREATE' | 'UPDATE' | 'DELETE' | 'CALCULATE';
  oldValue?: any;
  newValue?: any;
  timestamp: ProcessingDate;
  userId: string;
  correlationId: string;
  metadata: Record<string, any>;
}
```

### Configuration Models

#### 1. ComponentWeightings
```typescript
interface ComponentWeightings {
  environmental: {
    totalWeight: 40; // Fixed at 40%
    subComponents: {
      carbonEmissions: 15;
      energyEfficiency: 10;
      waterWasteManagement: 8;
      biodiversityImpact: 7;
    };
  };
  social: {
    totalWeight: 30; // Fixed at 30%
    subComponents: {
      diversityInclusion: 10;
      healthSafety: 8;
      communityImpact: 7;
      supplyChainPractices: 5;
    };
  };
  governance: {
    totalWeight: 30; // Fixed at 30%
    subComponents: {
      boardComposition: 12;
      executiveCompensation: 8;
      antiCorruption: 5;
      transparencyPractices: 5;
    };
  };
}
```

#### 2. ProcessingConfiguration
```typescript
interface ProcessingConfiguration {
  batchSize: number; // Default: 10
  maxRetries: number;
  retryDelayMs: number;
  timeoutMs: number;
  
  validation: {
    strictMode: boolean;
    allowPartialData: boolean;
    qualityThreshold: number;
  };
  
  normalization: {
    method: 'min-max' | 'z-score' | 'robust';
    handleOutliers: boolean;
    imputationStrategy: 'mean' | 'median' | 'forward-fill' | 'drop';
  };
  
  caching: {
    enabled: boolean;
    ttlSeconds: number;
    maxSize: number;
  };
}
```

## In-Memory Storage Schema

### Storage Collections
```typescript
interface InMemoryStorage {
  // Core entities
  holdings: Map<string, Holding>;
  esgData: Map<string, ESGData>;
  esgScores: Map<string, ESGScoreAggregate>;
  methodologies: Map<string, Methodology>;
  
  // Processing data
  dataBatches: Map<string, DataBatchAggregate>;
  processingQueue: Queue<ProcessingJob>;
  
  // Audit and lineage
  auditTrail: AuditTrail[];
  dataLineage: Map<string, DataLineage>;
  
  // Caching
  scoreCache: Map<string, CompositeESGScore>;
  configurationCache: Map<string, any>;
  
  // Indexes for performance
  indexes: {
    holdingsBySector: Map<string, HoldingId[]>;
    scoresByDate: Map<string, HoldingId[]>;
    batchesByStatus: Map<BatchStatus, string[]>;
  };
}
```

### Migration Strategy for Future Database Integration
```typescript
interface DatabaseMigrationStrategy {
  // Table definitions for PostgreSQL
  tables: {
    holdings: TableDefinition;
    esg_data: TableDefinition;
    esg_scores: TableDefinition;
    methodologies: TableDefinition;
    data_batches: TableDefinition;
    audit_trail: TableDefinition;
    data_lineage: TableDefinition;
  };
  
  // Migration scripts
  migrations: MigrationScript[];
  
  // Data export/import utilities
  exportToDatabase(): Promise<void>;
  importFromDatabase(): Promise<void>;
}
```

This comprehensive data model design provides a solid foundation for the ESG Data Processing & Scoring Engine, following DDD principles and ensuring scalability, maintainability, and compliance requirements.
