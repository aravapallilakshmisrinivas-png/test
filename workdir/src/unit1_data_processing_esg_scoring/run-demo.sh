#!/bin/bash

echo "🚀 Starting ESG Processing Demo Application..."
echo "📁 Current directory: $(pwd)"

# Navigate to the source directory
cd "$(dirname "$0")"

# Compile Java files
echo "🔨 Compiling Java files..."
find main/java -name "*.java" > sources.txt

# Create lib directory and download OpenCSV
mkdir -p lib
if [ ! -f "lib/opencsv-5.7.1.jar" ]; then
    echo "📦 Downloading OpenCSV library..."
    curl -L -o lib/opencsv-5.7.1.jar "https://repo1.maven.org/maven2/com/opencsv/opencsv/5.7.1/opencsv-5.7.1.jar"
fi

if [ ! -f "lib/commons-lang3-3.12.0.jar" ]; then
    echo "📦 Downloading Commons Lang3 library..."
    curl -L -o lib/commons-lang3-3.12.0.jar "https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.12.0/commons-lang3-3.12.0.jar"
fi

if [ ! -f "lib/commons-text-1.9.jar" ]; then
    echo "📦 Downloading Commons Text library..."
    curl -L -o lib/commons-text-1.9.jar "https://repo1.maven.org/maven2/org/apache/commons/commons-text/1.9/commons-text-1.9.jar"
fi

# Compile with classpath
javac -cp "lib/*:." -d . @sources.txt

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
    echo "🌐 Starting web server on http://localhost:8080"
    echo "⏹️  Press Ctrl+C to stop the server"
    echo ""
    
    # Run the application
    java -cp "lib/*:." ESGProcessingDemoApplication
else
    echo "❌ Compilation failed!"
    exit 1
fi