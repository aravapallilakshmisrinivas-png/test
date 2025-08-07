#!/bin/bash

echo "=== ESG Data Processing Demo - Complete Implementation ==="
echo "Starting Unit 1: Data Processing & ESG Scoring Demo..."

# Set up environment
export JAVA_HOME=${JAVA_HOME:-/usr/lib/jvm/default-java}
export CLASSPATH=".:lib/*"

# Compile all Java files
echo "Compiling Java files..."
find main/java -name "*.java" | xargs javac -cp "$CLASSPATH" -d .

if [ $? -ne 0 ]; then
    echo "Compilation failed. Please check for errors."
    exit 1
fi

echo "Compilation successful!"

# Run the demo application
echo "Starting ESG Processing Demo Application..."
echo "The application will be available at: http://localhost:8081"
echo "Press Ctrl+C to stop the application"

java -cp "$CLASSPATH" ESGProcessingDemoApplication

echo "Demo application stopped."