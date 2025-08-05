# Unit 1: Data Processing & ESG Scoring Engine

## Unit Overview

### Purpose
This unit handles the complete data pipeline from raw CSV ingestion through to final composite ESG score generation. It forms the foundational data processing and calculation engine for the entire ESG platform.

### Team Focus
Backend data engineering and calculation algorithms team with expertise in:
- Data processing and ETL pipelines
- Mathematical modeling and scoring algorithms
- Data validation and quality assurance
- Performance optimization for large datasets

### Unit Boundaries
- **Input**: Raw ESG data from CSV files
- **Output**: Validated, normalized ESG data and composite ESG scores
- **Scope**: Complete data-to-score pipeline with no external dependencies

## User Stories

### US-001: CSV Data Ingestion
- **As a** system administrator
- **I want** the system to automatically process ESG data from standardized CSV files in pre-configured locations
- **So that** ESG metrics are available for analysis without manual intervention

**Acceptance Criteria:**
- System reads CSV files from designated file paths
- Data validation ensures required ESG fields are present
- Invalid records are logged and skipped without stopping processing
- Processing status is tracked and reportable
- Data is normalized to standard 0-100 ESG scoring scale

### US-002: Data Validation and Quality Checks
- **As a** ESG analyst
- **I want** the system to validate incoming ESG data for completeness and accuracy
- **So that** only reliable data is used for scoring and analysis

**Acceptance Criteria:**
- Missing critical ESG metrics are flagged
- Data ranges are validated against expected bounds
- Duplicate records are identified and handled
- Data quality reports are generated
- Invalid data does not propagate to scoring calculations

### US-003: ESG Data Normalization
- **As a** ESG analyst
- **I want** ESG data from different sources to be normalized to consistent scales and formats
- **So that** meaningful comparisons can be made across holdings

**Acceptance Criteria:**
- All ESG metrics normalized to 0-100 scale
- Consistent data types and formats applied
- Missing values handled with documented methodology
- Normalization rules are transparent and auditable
- Historical data maintains consistency after normalization

### US-004: Environmental Score Calculation
- **As a** ESG analyst
- **I want** the system to calculate Environmental scores using the defined 40% weighting
- **So that** environmental impact is properly reflected in overall ESG scores

**Acceptance Criteria:**
- Carbon emissions weighted at 15% of total ESG score
- Energy efficiency weighted at 10% of total ESG score
- Water/waste management weighted at 8% of total ESG score
- Biodiversity impact weighted at 7% of total ESG score
- Environmental subscore calculation is transparent and reproducible
- Methodology documentation is accessible to users

### US-005: Social Score Calculation
- **As a** ESG analyst
- **I want** the system to calculate Social scores using the defined 30% weighting
- **So that** social responsibility factors are properly reflected in ESG scores

**Acceptance Criteria:**
- Diversity/inclusion weighted at 10% of total ESG score
- Health/safety performance weighted at 8% of total ESG score
- Community impact weighted at 7% of total ESG score
- Supply chain practices weighted at 5% of total ESG score
- Social subscore calculation is transparent and reproducible
- Methodology documentation is accessible to users

### US-006: Governance Score Calculation
- **As a** ESG analyst
- **I want** the system to calculate Governance scores using the defined 30% weighting
- **So that** governance quality is properly reflected in ESG scores

**Acceptance Criteria:**
- Board composition weighted at 12% of total ESG score
- Executive compensation weighted at 8% of total ESG score
- Anti-corruption policies weighted at 5% of total ESG score
- Transparency practices weighted at 5% of total ESG score
- Governance subscore calculation is transparent and reproducible
- Methodology documentation is accessible to users

### US-007: Composite ESG Score Generation
- **As a** portfolio manager
- **I want** the system to generate overall ESG scores combining Environmental (40%), Social (30%), and Governance (30%) components
- **So that** I can assess the overall ESG performance of holdings and portfolios

**Acceptance Criteria:**
- Final ESG score calculated using fixed weightings (40/30/30)
- Score range is 0-100 for consistency
- Component scores are preserved and accessible
- Calculation methodology is documented and transparent
- Scores are updated when underlying data changes

## Unit Architecture

### Data Flow Within Unit
```
CSV Files → Data Ingestion → Data Validation → Data Normalization
                ↓               ↓                    ↓
         Raw Data Store → Quality Reports → Normalized Data Store
                                                     ↓
Environmental Calc ← Normalized Data → Social Calc → Governance Calc
        ↓                                ↓               ↓
   E-Score (40%) ←── Composite ESG Score Generation ──→ G-Score (30%)
                            ↓
                    Final ESG Scores
```

### Core Components
1. **CSV File Monitor**: Detects new files and triggers processing
2. **Data Ingestion Engine**: Parses CSV files and extracts ESG metrics
3. **Validation Framework**: Applies business rules and data quality checks
4. **Normalization Engine**: Standardizes data formats and scales
5. **Scoring Calculator**: Implements ESG scoring methodology
6. **Score Aggregator**: Combines component scores into composite ESG score

### Data Models
- **Raw ESG Data**: Unprocessed data from CSV files
- **Validated ESG Data**: Data that passed quality checks
- **Normalized ESG Data**: Standardized data ready for scoring
- **Component Scores**: Environmental, Social, Governance subscores
- **Composite ESG Scores**: Final ESG scores with component breakdown

## Unit Interfaces

### External Dependencies
- **File System**: Access to pre-configured CSV file locations
- **Configuration Service**: ESG methodology parameters and file paths (from Unit 5)

### Provided APIs

#### ESG Scores API
**Purpose**: Provides ESG scores to other units
**Endpoints**:
- `GET /esg-scores/{holding-id}` - Individual holding ESG score
- `GET /esg-scores/portfolio/{portfolio-id}` - All holdings in portfolio
- `GET /esg-scores/components/{holding-id}` - Component score breakdown
- `GET /esg-scores/historical/{holding-id}` - Historical ESG scores

**Data Format**:
```json
{
  "holding_id": "string",
  "calculation_date": "date",
  "composite_esg_score": "number (0-100)",
  "environmental_score": "number (0-40)",
  "social_score": "number (0-30)",
  "governance_score": "number (0-30)",
  "component_breakdown": {
    "carbon_emissions": "number",
    "energy_efficiency": "number",
    "water_waste": "number",
    "biodiversity": "number",
    "diversity": "number",
    "health_safety": "number",
    "community": "number",
    "supply_chain": "number",
    "board_composition": "number",
    "compensation": "number",
    "anticorruption": "number",
    "transparency": "number"
  },
  "methodology_version": "string"
}
```

#### Data Quality API
**Purpose**: Provides data quality information
**Endpoints**:
- `GET /data-quality/reports` - Latest data quality reports
- `GET /data-quality/status/{batch-id}` - Processing status
- `GET /data-quality/metrics` - Data quality metrics

#### ESG Methodology API
**Purpose**: Provides methodology documentation
**Endpoints**:
- `GET /methodology/current` - Current methodology version
- `GET /methodology/documentation` - Complete methodology documentation
- `GET /methodology/weightings` - Current weighting schemes

## Testing Strategy

### Unit Testing
- Individual component testing for each calculation algorithm
- Data validation rule testing
- Normalization logic testing
- Score calculation accuracy testing

### Integration Testing
- End-to-end data pipeline testing
- CSV file processing testing
- API endpoint testing
- Data quality validation testing

### Mock Dependencies
- **File System**: Mock CSV files with various data scenarios
- **Configuration Service**: Mock configuration data for testing

## Performance Requirements
- Process up to 1000 holdings within 2-hour batch window
- ESG score calculation response time < 500ms per holding
- Support concurrent API requests from other units
- Handle CSV files up to 100MB in size

## Quality Assurance
- ESG score calculations must have <2% variance from manual calculations
- Data processing must complete successfully for 99% of valid input records
- All calculation methodologies must be documented and auditable
- Component score weightings must sum to exactly 100%

## Deployment Considerations
- Can be deployed independently as foundational unit
- Requires database for storing processed data and scores
- Needs file system access for CSV processing
- Should include monitoring for data processing jobs

## Success Criteria
- All 7 user stories fully implemented and tested
- ESG scores available via stable API for other units
- Data quality reports generated for all processing batches
- Methodology documentation accessible and complete
- Unit can operate independently with mocked dependencies
