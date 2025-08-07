import service.ProcessingOrchestrator;
import model.ESGHolding;
import model.DataQualityReport;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import java.util.List;

public class ESGProcessingDemoApplication {
    private static final ProcessingOrchestrator orchestrator = new ProcessingOrchestrator();

    public static void main(String[] args) throws IOException {
        System.out.println("🚀 Starting ESG Processing Demo Application...");
        
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        
        server.createContext("/api/process", new ProcessFileHandler());
        server.createContext("/api/process-invalid", new ProcessInvalidFileHandler());
        server.createContext("/api/holdings", new GetHoldingsHandler());
        server.createContext("/api/clear", new ClearDataHandler());
        server.createContext("/", new StaticFileHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("✅ Demo application started!");
        System.out.println("🌐 Open your browser and go to: http://localhost:8081");
        System.out.println("📊 Sample data is embedded in the application");
        System.out.println("⏹️  Press Ctrl+C to stop the server");
    }

    static class ProcessFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                ProcessingOrchestrator.ProcessingResult result = orchestrator.processSampleData();
                String response = buildProcessingResponse(result);
                
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, response.length());
                
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
        
        private String buildProcessingResponse(ProcessingOrchestrator.ProcessingResult result) {
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"success\":").append(result.isSuccess()).append(",");
            json.append("\"message\":\"").append(result.getMessage()).append("\",");
            
            if (result.getHoldings() != null) {
                json.append("\"holdingsCount\":").append(result.getHoldings().size()).append(",");
                json.append("\"holdings\":[");
                
                for (int i = 0; i < result.getHoldings().size(); i++) {
                    ESGHolding holding = result.getHoldings().get(i);
                    if (i > 0) json.append(",");
                    json.append("{");
                    json.append("\"holdingId\":\"").append(holding.getHoldingId()).append("\",");
                    json.append("\"symbol\":\"").append(holding.getSymbol()).append("\",");
                    json.append("\"companyName\":\"").append(holding.getCompanyName()).append("\",");
                    json.append("\"sector\":\"").append(holding.getSector()).append("\",");
                    json.append("\"environmentalScore\":").append(holding.getEnvironmentalScore()).append(",");
                    json.append("\"socialScore\":").append(holding.getSocialScore()).append(",");
                    json.append("\"governanceScore\":").append(holding.getGovernanceScore()).append(",");
                    json.append("\"compositeScore\":").append(holding.getCompositeScore()).append(",");
                    json.append("\"marketValue\":").append(holding.getMarketValue());
                    json.append("}");
                }
                json.append("]");
            }
            
            if (result.getReport() != null) {
                DataQualityReport report = result.getReport();
                json.append(",\"qualityReport\":{");
                json.append("\"totalRecords\":").append(report.getTotalRecords()).append(",");
                json.append("\"validRecords\":").append(report.getValidRecords()).append(",");
                json.append("\"errorRecords\":").append(report.getErrorRecords()).append(",");
                json.append("\"qualityScore\":").append(report.getQualityScore());
                json.append("}");
            }
            
            json.append("}");
            return json.toString();
        }
    }

    static class ProcessInvalidFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                ProcessingOrchestrator.ProcessingResult result = orchestrator.processInvalidSampleData();
                String response = buildProcessingResponse(result);
                
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, response.length());
                
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
        
        private String buildProcessingResponse(ProcessingOrchestrator.ProcessingResult result) {
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"success\":").append(result.isSuccess()).append(",");
            json.append("\"message\":\"").append(result.getMessage()).append("\",");
            
            if (result.getHoldings() != null) {
                json.append("\"holdingsCount\":").append(result.getHoldings().size()).append(",");
                json.append("\"holdings\":[");
                
                for (int i = 0; i < result.getHoldings().size(); i++) {
                    ESGHolding holding = result.getHoldings().get(i);
                    if (i > 0) json.append(",");
                    json.append("{");
                    json.append("\"holdingId\":\"").append(holding.getHoldingId()).append("\",");
                    json.append("\"symbol\":\"").append(holding.getSymbol()).append("\",");
                    json.append("\"companyName\":\"").append(holding.getCompanyName()).append("\",");
                    json.append("\"sector\":\"").append(holding.getSector()).append("\",");
                    json.append("\"environmentalScore\":").append(holding.getEnvironmentalScore()).append(",");
                    json.append("\"socialScore\":").append(holding.getSocialScore()).append(",");
                    json.append("\"governanceScore\":").append(holding.getGovernanceScore()).append(",");
                    json.append("\"compositeScore\":").append(holding.getCompositeScore()).append(",");
                    json.append("\"marketValue\":").append(holding.getMarketValue());
                    json.append("}");
                }
                json.append("]");
            }
            
            if (result.getReport() != null) {
                DataQualityReport report = result.getReport();
                json.append(",\"qualityReport\":{");
                json.append("\"totalRecords\":").append(report.getTotalRecords()).append(",");
                json.append("\"validRecords\":").append(report.getValidRecords()).append(",");
                json.append("\"errorRecords\":").append(report.getErrorRecords()).append(",");
                json.append("\"qualityScore\":").append(report.getQualityScore());
                json.append("}");
            }
            
            json.append("}");
            return json.toString();
        }
    }

    static class GetHoldingsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<ESGHolding> holdings = orchestrator.getAllHoldings();
            
            StringBuilder json = new StringBuilder();
            json.append("{\"holdings\":[");
            
            for (int i = 0; i < holdings.size(); i++) {
                ESGHolding holding = holdings.get(i);
                if (i > 0) json.append(",");
                json.append("{");
                json.append("\"holdingId\":\"").append(holding.getHoldingId()).append("\",");
                json.append("\"symbol\":\"").append(holding.getSymbol()).append("\",");
                json.append("\"companyName\":\"").append(holding.getCompanyName()).append("\",");
                json.append("\"compositeScore\":").append(holding.getCompositeScore());
                json.append("}");
            }
            json.append("]}");
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, json.length());
            
            OutputStream os = exchange.getResponseBody();
            os.write(json.toString().getBytes());
            os.close();
        }
    }

    static class ClearDataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            orchestrator.clearData();
            
            String response = "{\"success\":true,\"message\":\"Data cleared successfully\"}";
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, response.length());
            
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = "<!DOCTYPE html>" +
                "<html><head><title>ESG Processing Demo</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }" +
                ".container { max-width: 1200px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                "h1 { color: #2c3e50; text-align: center; margin-bottom: 30px; }" +
                ".controls { text-align: center; margin: 30px 0; }" +
                "button { background: #3498db; color: white; border: none; padding: 12px 24px; margin: 0 10px; border-radius: 5px; cursor: pointer; font-size: 16px; }" +
                "button:hover { background: #2980b9; }" +
                ".metrics { display: flex; justify-content: space-around; margin: 20px 0; }" +
                ".metric { text-align: center; padding: 15px; background: #ecf0f1; border-radius: 5px; }" +
                ".metric h3 { margin: 0; color: #2c3e50; }" +
                ".metric p { margin: 5px 0 0 0; font-size: 24px; font-weight: bold; color: #3498db; }" +
                "table { width: 100%; border-collapse: collapse; margin-top: 20px; }" +
                "th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }" +
                "th { background: #3498db; color: white; }" +
                ".score { font-weight: bold; }" +
                ".score.high { color: #27ae60; }" +
                ".score.medium { color: #f39c12; }" +
                ".score.low { color: #e74c3c; }" +
                ".status { padding: 10px; margin: 10px 0; border-radius: 5px; }" +
                ".status.success { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }" +
                ".status.error { background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<h1>🌱 ESG Data Processing & Scoring Demo</h1>" +
                "<div class='controls'>" +
                "<button onclick='processFile()'>📊 Process Valid ESG Data</button>" +
                "<button onclick='processInvalidFile()'>⚠️ Process Invalid Data</button>" +
                "<button onclick='clearData()'>🗑️ Clear Data</button>" +
                "<button onclick='loadHoldings()'>🔄 Refresh Holdings</button>" +
                "</div>" +
                "<div id='status'></div>" +
                "<div id='metrics' class='metrics' style='display:none;'>" +
                "<div class='metric'><h3>Total Holdings</h3><p id='totalHoldings'>0</p></div>" +
                "<div class='metric'><h3>Quality Score</h3><p id='qualityScore'>0%</p></div>" +
                "<div class='metric'><h3>Avg ESG Score</h3><p id='avgScore'>0</p></div>" +
                "</div>" +
                "<div id='results'></div>" +
                "</div>" +
                "<script>" +
                "function showStatus(message, isSuccess) {" +
                "document.getElementById('status').innerHTML = '<div class=\"status ' + (isSuccess ? 'success' : 'error') + '\">' + message + '</div>';" +
                "}" +
                "function processFile() {" +
                "showStatus('Processing valid ESG data...', true);" +
                "fetch('/api/process', { method: 'POST' })" +
                ".then(response => response.json())" +
                ".then(data => {" +
                "if (data.success) {" +
                "showStatus(data.message, true);" +
                "displayResults(data);" +
                "updateMetrics(data);" +
                "} else {" +
                "showStatus(data.message, false);" +
                "}" +
                "})" +
                ".catch(error => showStatus('Error: ' + error.message, false));" +
                "}" +
                "function processInvalidFile() {" +
                "showStatus('Processing invalid ESG data...', true);" +
                "fetch('/api/process-invalid', { method: 'POST' })" +
                ".then(response => response.json())" +
                ".then(data => {" +
                "if (data.success) {" +
                "showStatus(data.message + ' - Check quality score!', true);" +
                "displayResults(data);" +
                "updateMetrics(data);" +
                "} else {" +
                "showStatus(data.message, false);" +
                "}" +
                "})" +
                ".catch(error => showStatus('Error: ' + error.message, false));" +
                "}" +
                "function clearData() {" +
                "fetch('/api/clear', { method: 'POST' })" +
                ".then(response => response.json())" +
                ".then(data => {" +
                "showStatus(data.message, data.success);" +
                "document.getElementById('results').innerHTML = '';" +
                "document.getElementById('metrics').style.display = 'none';" +
                "});" +
                "}" +
                "function loadHoldings() {" +
                "fetch('/api/holdings')" +
                ".then(response => response.json())" +
                ".then(data => displayHoldings(data.holdings));" +
                "}" +
                "function updateMetrics(data) {" +
                "if (data.holdings && data.qualityReport) {" +
                "document.getElementById('totalHoldings').textContent = data.holdingsCount;" +
                "document.getElementById('qualityScore').textContent = Math.round(data.qualityReport.qualityScore) + '%';" +
                "const avgScore = data.holdings.reduce((sum, h) => sum + (h.compositeScore || 0), 0) / data.holdings.length;" +
                "document.getElementById('avgScore').textContent = Math.round(avgScore);" +
                "document.getElementById('metrics').style.display = 'flex';" +
                "}" +
                "}" +
                "function displayResults(data) {" +
                "if (!data.holdings) return;" +
                "let html = '<h2>📈 ESG Holdings Results</h2><table>';" +
                "html += '<tr><th>Symbol</th><th>Company</th><th>Sector</th><th>Environmental</th><th>Social</th><th>Governance</th><th>Composite</th></tr>';" +
                "data.holdings.forEach(holding => {" +
                "html += '<tr>';" +
                "html += '<td><strong>' + holding.symbol + '</strong></td>';" +
                "html += '<td>' + holding.companyName + '</td>';" +
                "html += '<td>' + holding.sector + '</td>';" +
                "html += '<td class=\"score ' + getScoreClass(holding.environmentalScore) + '\">' + Math.round(holding.environmentalScore) + '</td>';" +
                "html += '<td class=\"score ' + getScoreClass(holding.socialScore) + '\">' + Math.round(holding.socialScore) + '</td>';" +
                "html += '<td class=\"score ' + getScoreClass(holding.governanceScore) + '\">' + Math.round(holding.governanceScore) + '</td>';" +
                "html += '<td class=\"score ' + getScoreClass(holding.compositeScore) + '\"><strong>' + Math.round(holding.compositeScore) + '</strong></td>';" +
                "html += '</tr>';" +
                "});" +
                "html += '</table>';" +
                "document.getElementById('results').innerHTML = html;" +
                "}" +
                "function displayHoldings(holdings) {" +
                "if (holdings.length === 0) {" +
                "document.getElementById('results').innerHTML = '<p>No holdings data available. Process data first.</p>';" +
                "return;" +
                "}" +
                "displayResults({holdings: holdings});" +
                "}" +
                "function getScoreClass(score) {" +
                "if (score >= 80) return 'high';" +
                "if (score >= 60) return 'medium';" +
                "return 'low';" +
                "}" +
                "</script></body></html>";
            
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, html.length());
            
            OutputStream os = exchange.getResponseBody();
            os.write(html.getBytes());
            os.close();
        }
    }
}