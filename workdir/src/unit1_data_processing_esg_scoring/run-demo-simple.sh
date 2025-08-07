#!/bin/bash

echo "🚀 Starting ESG Processing Demo Application (No External Dependencies)..."
echo "📁 Current directory: $(pwd)"

# Navigate to the source directory
cd "$(dirname "$0")"

# Compile Java files (no external dependencies needed)
echo "🔨 Compiling Java files..."
find main/java -name "*.java" > sources.txt

# Compile without external libraries
javac -d . @sources.txt

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
    echo "🌐 Starting web server on http://localhost:8080"
    echo "📊 Sample ESG data is embedded in the application"
    echo "⏹️  Press Ctrl+C to stop the server"
    echo ""
    
    # Run the application
    java ESGProcessingDemoApplication
else
    echo "❌ Compilation failed!"
    exit 1
fi