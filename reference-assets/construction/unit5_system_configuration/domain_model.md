# Unit 5: System Configuration Management - Domain Model

## Domain Overview

### Bounded Context
**System Configuration Context** - Responsible for managing system-wide configuration, settings, and administrative functionality.

### Core Domain Responsibility
Provide centralized configuration management for all other contexts while maintaining system parameters, thresholds, and operational settings.

## Ubiquitous Language

### Core Concepts
- **Configuration**: System parameters and settings that control behavior
- **Configuration File**: File-based storage of system settings in structured format
- **Configuration Validation**: Process of verifying configuration correctness and consistency
- **Configuration Version**: Tracked changes to configuration over time with rollback capability
- **Configuration Distribution**: Delivery of settings to other bounded contexts
- **System Parameter**: Individual configuration setting with name, value, and metadata

## Aggregates Design

### 1. System Configuration Aggregate
**Aggregate Root**: `SystemConfiguration`
**Purpose**: Manages all system configuration data and validation
**Consistency Boundary**: All configuration parameters and their relationships

#### Aggregate Root: SystemConfiguration
```
SystemConfiguration (Entity)
├── ConfigurationId (Value Object)
├── ConfigurationVersion (Value Object)
├── ESGMethodologyConfig (Entity)
├── AlertThresholdConfig (Entity)
├── DataProcessingConfig (Entity)
├── NotificationConfig (Entity)
├── DashboardConfig (Entity)
├── RiskCalculationConfig (Entity)
├── ValidationResults (Entity)
└── LastUpdated (Value Object)
```

#### Child Entities:

##### ESGMethodologyConfig (Entity)
```
ESGMethodologyConfig
├── MethodologyId (Value Object)
├── MethodologyVersion (Value Object)
├── ComponentWeightings (Value Object) // 40% E, 30% S, 30% G
├── EnvironmentalWeights (Value Object)
├── SocialWeights (Value Object)
├── GovernanceWeights (Value Object)
├── ScoringParameters (Value Object)
└── EffectiveDate (Value Object)
```

##### AlertThresholdConfig (Entity)
```
AlertThresholdConfig
├── ThresholdId (Value Object)
├── CriticalThreshold (Value Object) // <30
├── WarningThreshold (Value Object) // 30-50
├── WatchThreshold (Value Object) // 50-70
├── PortfolioThresholds (Value Object)
├── AlertFrequencies (Value Object)
└── NotificationSettings (Value Object)
```

## Domain Events

#### ConfigurationUpdated
```
ConfigurationUpdated
├── ConfigurationId (String)
├── PreviousVersion (String)
├── NewVersion (String)
├── ChangedParameters (List<String>)
├── AffectedContexts (List<String>)
├── EffectiveDate (DateTime)
└── EventTimestamp (DateTime)
```

#### ConfigurationValidationFailed
```
ConfigurationValidationFailed
├── ConfigurationId (String)
├── ValidationErrors (List<String>)
├── FailedParameters (List<String>)
├── ValidationSeverity (String)
└── EventTimestamp (DateTime)
```

## Domain Services

### ConfigurationManagementService
**Purpose**: Manages configuration lifecycle and distribution
```
ConfigurationManagementService
├── LoadConfigurationFromFiles(filePaths: List<String>): SystemConfiguration
├── ValidateConfiguration(config: SystemConfiguration): ValidationResult
├── DistributeConfiguration(config: SystemConfiguration, targetContexts: List<String>): void
├── CreateConfigurationVersion(config: SystemConfiguration): ConfigurationVersion
└── RollbackConfiguration(targetVersion: String): SystemConfiguration
```

### ConfigurationValidationService
**Purpose**: Validates configuration consistency and correctness
```
ConfigurationValidationService
├── ValidateESGMethodology(methodology: ESGMethodologyConfig): ValidationResult
├── ValidateThresholdConsistency(thresholds: AlertThresholdConfig): ValidationResult
├── ValidateParameterRanges(config: SystemConfiguration): ValidationResult
└── ValidateCrossDependencies(config: SystemConfiguration): ValidationResult
```

## Repositories

### SystemConfigurationRepository
```
SystemConfigurationRepository
├── Save(config: SystemConfiguration): void
├── FindCurrent(): SystemConfiguration
├── FindByVersion(version: String): SystemConfiguration
├── GetConfigurationHistory(): List<ConfigurationVersion>
└── SaveConfigurationFile(filePath: String, content: String): void
```

## Domain Policies

### ConfigurationValidationPolicy
```
ConfigurationValidationPolicy
├── ValidateWeightingSums(weightings: ComponentWeightings): PolicyResult
├── ValidateThresholdOrdering(thresholds: AlertThresholdConfig): PolicyResult
├── ValidateParameterRanges(parameters: List<Parameter>): PolicyResult
└── RequireVersionIncrement(newConfig: SystemConfiguration, currentConfig: SystemConfiguration): PolicyResult
```

## Anti-Corruption Layer Specifications

### Outbound to All Contexts
**Transformations**:
- SystemConfiguration → ContextSpecificConfiguration
- Map internal configuration model to context-specific formats
- ESGMethodologyConfig → ESGScoringParameters (for Unit 1)
- AlertThresholdConfig → ThresholdSettings (for Unit 2)
- RiskCalculationConfig → RiskParameters (for Unit 3)
