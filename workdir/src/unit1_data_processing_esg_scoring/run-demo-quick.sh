#!/bin/bash

echo "🚀 ESG Processing Demo - Quick Start"

# Kill any existing Java processes on port 8081
lsof -ti:8081 | xargs kill -9 2>/dev/null || true

# Set classpath
export CLASSPATH=".:lib/*"

# Compile and run in one step
echo "Compiling and starting demo..."
find main/java -name "*.java" | xargs javac -cp "$CLASSPATH" -d . && \
java -cp "$CLASSPATH" ESGProcessingDemoApplication &

# Wait a moment for startup
sleep 2

echo "✅ Demo started at http://localhost:8081"
echo "📱 Opening browser..."

# Try to open browser (works on most systems)
if command -v open >/dev/null 2>&1; then
    open http://localhost:8081
elif command -v xdg-open >/dev/null 2>&1; then
    xdg-open http://localhost:8081
else
    echo "Please open http://localhost:8081 in your browser"
fi

echo "Press Enter to stop the demo"
read
pkill -f ESGProcessingDemoApplication