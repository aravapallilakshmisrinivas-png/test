# ESG Data Processing Demo Guide

## Overview
This demo showcases Unit 1: Data Processing & ESG Scoring functionality with a complete implementation of all 8 strategic components.

## Quick Start
1. Navigate to the project directory:
   ```bash
   cd workdir/src/unit1_data_processing_esg_scoring/
   ```

2. Run the demo:
   ```bash
   chmod +x run-demo-complete.sh
   ./run-demo-complete.sh
   ```

3. Open browser to: `http://localhost:8081`

## Demo Features

### Core Processing Pipeline
- **File Monitoring**: Simulated file detection and queuing
- **CSV Ingestion**: Parse and extract ESG data from CSV files
- **Data Validation**: Comprehensive validation with error reporting
- **ESG Calculation**: Weighted scoring (40% E, 30% S, 30% G)
- **Database Storage**: In-memory storage with transaction support
- **Error Handling**: Automatic retry with exponential backoff
- **Audit Logging**: Complete processing audit trail
- **Health Monitoring**: System component health checks

### Sample Data
- **Valid Data**: 8 companies with proper ESG scores
- **Invalid Data**: 2 companies with validation errors
- **Real-time Processing**: Simulated processing delays
- **Quality Metrics**: Validation statistics and scoring

### Web Interface
- **Process ESG Data**: Trigger complete processing pipeline
- **Real-time Updates**: Live metrics and progress tracking
- **Results Table**: Detailed ESG scores and validation status
- **Processing Log**: Timestamped audit trail
- **Clear Results**: Reset for new processing runs

## Architecture Components

### 1. File Monitor Component
- Watches for new CSV files
- Queues files for processing
- Handles file detection events

### 2. CSV Ingestion Engine
- Parses CSV files
- Extracts ESG data
- Validates file format

### 3. Data Validator Framework
- Validates ESG score ranges (0-100)
- Checks required fields
- Generates quality reports

### 4. ESG Calculation Engine
- Calculates weighted ESG scores
- Normalizes score ranges
- Maintains calculation audit trail

### 5. Database Writer Component
- Stores processed data
- Handles transactions
- Implements retry logic

### 6. Error Handler Component
- Classifies error types
- Manages retry attempts
- Reports processing issues

### 7. Processing Orchestrator
- Coordinates component workflow
- Manages data flow
- Handles end-to-end processing

### 8. Audit & Logging Framework
- Tracks processing events
- Monitors system health
- Generates performance metrics

## Testing Scenarios

### Valid Data Processing
1. Click "Process ESG Data"
2. Observe real-time metrics updates
3. Review successful ESG score calculations
4. Check audit log for processing events

### Error Handling
1. Process data with invalid scores
2. Observe validation error reporting
3. Check retry mechanisms
4. Review error classification

### System Health
1. Monitor component health status
2. Check processing metrics
3. Review audit trail completeness

## Configuration Options
- ESG weighting factors (configurable)
- Validation rules (customizable)
- Retry limits (adjustable)
- Processing thread pools (scalable)

## Troubleshooting
- **Port 8081 in use**: Change port in ESGProcessingDemoApplication.java
- **Compilation errors**: Ensure Java 11+ and proper classpath
- **Browser issues**: Try different browser or clear cache

## Next Steps
- Extend with real file system integration
- Add database persistence
- Implement message queue architecture
- Scale for production workloads