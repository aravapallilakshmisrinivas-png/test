# ESG Data Processing & Scoring Demo

## 🌱 Overview
This is a demonstration of Unit 1: Data Processing & ESG Scoring system that processes ESG (Environmental, Social, Governance) data and calculates composite scores using embedded sample data.

## 🚀 Quick Start (No External Dependencies)

### Run with Simple Script (Recommended)
```bash
./run-demo-simple.sh
```

### Manual Compilation and Run
```bash
# Compile Java files (no external dependencies)
find main/java -name "*.java" > sources.txt
javac -d . @sources.txt

# Run the application
java ESGProcessingDemoApplication
```

## 🌐 Access the Demo
Once started, open your browser and go to:
**http://localhost:8080**

## 📊 Features Demonstrated

### Core Processing Pipeline
- **CSV Ingestion**: Reads and parses ESG data from CSV files
- **Data Validation**: Validates data quality with comprehensive rules
- **ESG Calculation**: Calculates composite scores using configurable weights (40% E, 30% S, 30% G)
- **In-Memory Storage**: Thread-safe storage using ConcurrentHashMap
- **Quality Reporting**: Generates data quality reports with metrics

### Web Interface
- **Process Sample Data**: Processes pre-loaded sample ESG data
- **View Results**: Displays holdings with color-coded ESG scores
- **Quality Metrics**: Shows processing statistics and quality scores
- **Clear Data**: Resets the in-memory storage

## 📁 Sample Data
The demo includes embedded sample data:
- **Valid ESG Data** - Clean ESG data for 10 major companies (Apple, Microsoft, Tesla, etc.)
- **Invalid ESG Data** - Data with validation errors to demonstrate quality checking

## 🎯 ESG Scoring Logic
- **Environmental Score**: 40% weight
- **Social Score**: 30% weight  
- **Governance Score**: 30% weight
- **Composite Score**: Weighted average of E, S, G scores
- **Color Coding**: 
  - 🟢 Green (80-100): High performance
  - 🟡 Yellow (60-79): Medium performance
  - 🔴 Red (0-59): Low performance

## 🏗️ Architecture Components
- **FileMonitorComponent**: Monitors for new CSV files
- **CSVIngestionEngine**: Parses CSV data into ESG holdings
- **DataValidatorFramework**: Validates data quality
- **ESGCalculationEngine**: Calculates composite ESG scores
- **ProcessingOrchestrator**: Coordinates end-to-end processing
- **ESGScoreRepository**: In-memory storage for processed data

## 🔧 Configuration
Default configuration in `config.properties`:
- Thread pool size: 4
- Batch size: 1000 records
- Quality threshold: 85%
- ESG weights: E=40%, S=30%, G=30%

## 🧪 Testing the Demo
1. **Start the application** using `./run-demo-simple.sh`
2. **Open browser** to http://localhost:8080
3. **Click "Process Valid ESG Data"** to process clean sample data
4. **Click "Process Invalid Data"** to see validation errors in action
5. **View the results** in the table with color-coded scores
6. **Check quality metrics** displayed at the top
7. **Try "Clear Data"** and "Refresh Holdings" buttons

## 📈 Expected Results
Processing the sample data should show:
- 10 holdings processed successfully
- Quality score around 100% (clean data)
- Average composite ESG score around 80
- Holdings from Technology, Healthcare, Financial, and other sectors

## 🛑 Stopping the Demo
Press `Ctrl+C` in the terminal to stop the server.

## 🔍 Technical Details
- **Language**: Java 11+
- **Dependencies**: None (completely self-contained)
- **Data Source**: Embedded static sample data
- **Storage**: In-memory ConcurrentHashMap
- **Web Server**: Built-in Java HttpServer
- **Architecture**: Event-driven with component-based design
- **Thread Safety**: ConcurrentHashMap and thread-safe operations

This demo showcases a complete ESG data processing pipeline with real-time web interface for monitoring and interaction.