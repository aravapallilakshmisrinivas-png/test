# ESG Data Platform - Data Model

## Core Data Entities

### 1. Portfolio Holdings
**Entity**: `portfolio_holdings`
- **holding_id** (Primary Key): Unique identifier for each holding
- **portfolio_id**: Portfolio identifier
- **security_id**: Security/instrument identifier (ISIN, CUSIP, etc.)
- **security_name**: Human-readable security name
- **sector**: Industry sector classification
- **weight**: Portfolio weight (percentage)
- **market_value**: Current market value
- **created_date**: Record creation timestamp
- **updated_date**: Last update timestamp

### 2. ESG Raw Data
**Entity**: `esg_raw_data`
- **data_id** (Primary Key): Unique identifier for each ESG data record
- **security_id**: Foreign key to portfolio holdings
- **data_date**: Date of ESG data measurement
- **carbon_emissions_scope1**: Scope 1 carbon emissions
- **carbon_emissions_scope2**: Scope 2 carbon emissions  
- **carbon_emissions_scope3**: Scope 3 carbon emissions
- **energy_efficiency_score**: Energy efficiency rating
- **renewable_energy_usage**: Renewable energy usage percentage
- **water_usage_intensity**: Water usage per unit of production
- **waste_management_score**: Waste management effectiveness score
- **biodiversity_impact_score**: Biodiversity impact assessment
- **employee_diversity_score**: Workforce diversity metrics
- **health_safety_incidents**: Health and safety incident rate
- **community_investment**: Community investment as % of revenue
- **supply_chain_score**: Supply chain labor practices score
- **board_independence**: Board independence percentage
- **executive_compensation_ratio**: CEO to median worker pay ratio
- **anticorruption_policy_score**: Anti-corruption policy effectiveness
- **transparency_disclosure_score**: Transparency and disclosure rating
- **data_source**: Source of ESG data
- **data_quality_flag**: Data quality indicator
- **created_date**: Record creation timestamp

### 3. ESG Calculated Scores
**Entity**: `esg_calculated_scores`
- **score_id** (Primary Key): Unique identifier for calculated score
- **security_id**: Foreign key to portfolio holdings
- **calculation_date**: Date of score calculation
- **environmental_score**: Environmental component score (0-100)
- **social_score**: Social component score (0-100)
- **governance_score**: Governance component score (0-100)
- **composite_esg_score**: Overall ESG score (0-100)
- **carbon_emissions_subscore**: Carbon emissions component (0-15)
- **energy_efficiency_subscore**: Energy efficiency component (0-10)
- **water_waste_subscore**: Water/waste management component (0-8)
- **biodiversity_subscore**: Biodiversity impact component (0-7)
- **diversity_subscore**: Diversity and inclusion component (0-10)
- **health_safety_subscore**: Health and safety component (0-8)
- **community_subscore**: Community impact component (0-7)
- **supply_chain_subscore**: Supply chain practices component (0-5)
- **board_subscore**: Board composition component (0-12)
- **compensation_subscore**: Executive compensation component (0-8)
- **anticorruption_subscore**: Anti-corruption component (0-5)
- **transparency_subscore**: Transparency practices component (0-5)
- **methodology_version**: Version of calculation methodology used
- **created_date**: Record creation timestamp

### 4. Portfolio ESG Aggregates
**Entity**: `portfolio_esg_aggregates`
- **aggregate_id** (Primary Key): Unique identifier for portfolio aggregate
- **portfolio_id**: Portfolio identifier
- **calculation_date**: Date of aggregate calculation
- **portfolio_esg_score**: Weighted average portfolio ESG score
- **portfolio_environmental_score**: Weighted average environmental score
- **portfolio_social_score**: Weighted average social score
- **portfolio_governance_score**: Weighted average governance score
- **holdings_count**: Number of holdings in portfolio
- **critical_holdings_count**: Holdings with ESG score < 30
- **warning_holdings_count**: Holdings with ESG score 30-50
- **watch_holdings_count**: Holdings with ESG score 50-70
- **good_holdings_count**: Holdings with ESG score > 70
- **created_date**: Record creation timestamp

### 5. ESG Risk Integration
**Entity**: `esg_risk_integration`
- **risk_id** (Primary Key): Unique identifier for risk calculation
- **security_id**: Foreign key to portfolio holdings
- **calculation_date**: Date of risk calculation
- **base_credit_risk**: Original credit risk score
- **esg_adjustment_factor**: ESG-based risk adjustment factor
- **esg_adjusted_risk**: ESG-adjusted credit risk score
- **environmental_risk_contribution**: Environmental component risk impact
- **social_risk_contribution**: Social component risk impact
- **governance_risk_contribution**: Governance component risk impact
- **risk_methodology_version**: Version of risk integration methodology
- **created_date**: Record creation timestamp

### 6. Alert Configurations
**Entity**: `alert_configurations`
- **config_id** (Primary Key): Unique identifier for alert configuration
- **alert_type**: Type of alert (critical, warning, watch)
- **threshold_value**: ESG score threshold value
- **alert_frequency**: Frequency of alert checking (immediate, daily, weekly)
- **notification_method**: Method of notification (email)
- **is_active**: Whether alert configuration is active
- **created_date**: Record creation timestamp
- **updated_date**: Last update timestamp

### 7. Alert History
**Entity**: `alert_history`
- **alert_id** (Primary Key): Unique identifier for alert instance
- **security_id**: Foreign key to portfolio holdings
- **portfolio_id**: Portfolio identifier
- **alert_type**: Type of alert triggered
- **alert_message**: Alert message content
- **esg_score_current**: Current ESG score that triggered alert
- **esg_score_previous**: Previous ESG score for comparison
- **threshold_breached**: Threshold value that was breached
- **notification_sent**: Whether notification was successfully sent
- **notification_timestamp**: Timestamp of notification delivery
- **alert_status**: Status of alert (active, acknowledged, resolved)
- **created_date**: Alert creation timestamp

### 8. User Roles and Permissions
**Entity**: `user_roles`
- **user_id** (Primary Key): Unique user identifier
- **username**: User login name
- **email**: User email address
- **role_type**: User role (portfolio_manager, esg_analyst, credit_risk_officer)
- **portfolio_access**: Comma-separated list of accessible portfolio IDs
- **permissions**: JSON object containing specific permissions
- **is_active**: Whether user account is active
- **created_date**: Account creation timestamp
- **last_login**: Last login timestamp

### 9. System Configuration
**Entity**: `system_configuration`
- **config_key** (Primary Key): Configuration parameter name
- **config_value**: Configuration parameter value
- **config_type**: Data type of configuration value
- **description**: Description of configuration parameter
- **is_active**: Whether configuration is active
- **created_date**: Configuration creation timestamp
- **updated_date**: Last update timestamp

### 10. Data Processing Log
**Entity**: `data_processing_log`
- **log_id** (Primary Key): Unique identifier for log entry
- **process_type**: Type of data processing (ingestion, calculation, aggregation)
- **process_start_time**: Process start timestamp
- **process_end_time**: Process completion timestamp
- **records_processed**: Number of records processed
- **records_successful**: Number of successfully processed records
- **records_failed**: Number of failed records
- **error_messages**: JSON array of error messages
- **process_status**: Status of processing (success, partial_success, failure)
- **created_date**: Log entry creation timestamp

## Data Relationships

### Primary Relationships
1. **Portfolio Holdings → ESG Raw Data**: One-to-many (one holding can have multiple ESG data points over time)
2. **Portfolio Holdings → ESG Calculated Scores**: One-to-many (one holding can have multiple calculated scores over time)
3. **Portfolio Holdings → ESG Risk Integration**: One-to-many (one holding can have multiple risk calculations over time)
4. **Portfolio Holdings → Alert History**: One-to-many (one holding can trigger multiple alerts)
5. **ESG Raw Data → ESG Calculated Scores**: One-to-one (each raw data record produces one calculated score)

### Aggregation Relationships
1. **Portfolio Holdings → Portfolio ESG Aggregates**: Many-to-one (multiple holdings aggregate to portfolio level)
2. **ESG Calculated Scores → Portfolio ESG Aggregates**: Many-to-one (multiple scores aggregate to portfolio level)

### Configuration Relationships
1. **Alert Configurations → Alert History**: One-to-many (one configuration can generate multiple alerts)
2. **User Roles → Alert History**: One-to-many (one user can receive multiple alerts)
3. **System Configuration → Data Processing Log**: One-to-many (configuration changes can trigger multiple processes)

## Data Flow Architecture

### 1. Data Ingestion Flow
```
CSV Files → Data Validation → ESG Raw Data → Data Quality Checks → Processing Log
```

### 2. Score Calculation Flow
```
ESG Raw Data → Normalization → Component Scoring → Composite Scoring → ESG Calculated Scores
```

### 3. Portfolio Aggregation Flow
```
ESG Calculated Scores + Portfolio Holdings → Weighted Aggregation → Portfolio ESG Aggregates
```

### 4. Risk Integration Flow
```
ESG Calculated Scores + Base Credit Risk → Linear Adjustment → ESG Risk Integration
```

### 5. Alert Processing Flow
```
ESG Calculated Scores → Threshold Comparison → Alert Generation → Alert History → Notification
```

### 6. Dashboard Data Flow
```
Portfolio ESG Aggregates + ESG Calculated Scores + ESG Risk Integration → Dashboard Views
```

## Data Quality and Validation Rules

### ESG Raw Data Validation
- **Required Fields**: security_id, data_date, and at least 80% of ESG metric fields
- **Data Ranges**: All ESG metrics must be within expected ranges (0-100 for scores, positive values for ratios)
- **Date Validation**: data_date must be within last 24 months and not future-dated
- **Duplicate Detection**: Combination of security_id + data_date must be unique

### ESG Score Validation
- **Score Ranges**: All component and composite scores must be between 0-100
- **Weighting Validation**: Component weightings must sum to 100% (40% E + 30% S + 30% G)
- **Calculation Consistency**: Composite scores must equal weighted sum of components
- **Historical Consistency**: Score changes >20 points require validation flag

### Portfolio Aggregate Validation
- **Weight Validation**: Portfolio holdings weights must sum to 100% (±1% tolerance)
- **Count Validation**: Holdings count must equal sum of all ESG rating band counts
- **Score Consistency**: Portfolio scores must be within range of constituent holding scores
- **Temporal Consistency**: Portfolio aggregates must have corresponding holding scores for same date

## Performance Considerations

### Indexing Strategy
- **Primary Keys**: Clustered indexes on all primary keys
- **Foreign Keys**: Non-clustered indexes on all foreign key relationships
- **Date Fields**: Indexes on calculation_date, data_date, created_date for time-based queries
- **Portfolio Queries**: Composite index on (portfolio_id, calculation_date)
- **Alert Queries**: Composite index on (alert_type, created_date, alert_status)

### Data Archival Strategy
- **Raw Data**: Archive ESG raw data older than 24 months to separate archive tables
- **Calculated Scores**: Maintain 12 months of calculated scores in primary tables
- **Alert History**: Archive alert history older than 6 months
- **Processing Logs**: Archive processing logs older than 3 months

### Query Optimization
- **Dashboard Queries**: Pre-calculated aggregates for common dashboard views
- **Historical Analysis**: Separate tables for historical trend data
- **Real-time Alerts**: Optimized queries for threshold breach detection
- **Export Functions**: Indexed views for common export scenarios

## Data Security and Privacy

### Access Control
- **Row-Level Security**: Users can only access portfolios assigned to their role
- **Column-Level Security**: Sensitive fields restricted based on user role
- **Audit Logging**: All data access and modifications logged
- **Encryption**: Sensitive data encrypted at rest and in transit

### Data Retention
- **ESG Data**: Retain for minimum 7 years for regulatory compliance
- **Alert History**: Retain for minimum 3 years
- **User Activity**: Retain for minimum 1 year
- **System Logs**: Retain for minimum 6 months

### Backup and Recovery
- **Daily Backups**: Full database backup daily during off-peak hours
- **Transaction Log Backups**: Every 15 minutes during business hours
- **Recovery Testing**: Monthly recovery testing procedures
- **Disaster Recovery**: Cross-region backup replication for disaster recovery
